package com.slack.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slack.dao.DmDao;
import com.slack.dao.WorkspaceDao;
import com.slack.dto.DmContentDto;
import com.slack.dto.DmDetailListDto;
import com.slack.dto.DmDto;
import com.slack.dto.DmLeftListDto;
import com.slack.dto.DmListDto;
import com.slack.dto.DmUsersDto;
import com.slack.dto.DmWorkspaceUsersDto;
import com.slack.dto.WorkspaceUsersDto;

@Service
public class DmServiceImpl implements DmService {
	@Autowired
	DmDao dDao;
	@Autowired
	WorkspaceDao wDao;
	
	/** [SB] 키워드로 디엠목록조회
	 * input : dmFrom, keyword, workspaceIdx
	 * output : :List<DmContentDto>
	 */
	@Override
	public List<DmContentDto> selectDmListByContent(String dmFrom, String keyword, int workspaceIdx) {
		return dDao.selectDmListByContent(dmFrom, keyword, workspaceIdx);
	}
	
	/** [SB] 디엠테이블에 추가 
	 * input : DmListDto 
	 * output : 
	 */
	@Override
	@Transactional
	public void addDm(DmListDto dto) {
		dDao.insertDm(dto);
		String content = dto.getContent();
		
		Pattern pattern = Pattern.compile("data-id=\"([^\"]+)\"");      // 정규표현식으로 저장된 Content에서 멘션 추출
	    Matcher matcher = pattern.matcher(content);
	    while (matcher.find()) {
	        String mentionedId = matcher.group(1); 
	        dDao.insertMention(dto.getDmFrom(), dto.getDmIdx(), mentionedId, dto.getWorkspaceIdx()); 
	    }
	    Pattern filePattern = Pattern.compile("upload/([a-zA-Z0-9_.-]+)");
	    Matcher fileMatcher = filePattern.matcher(content);

	    while (fileMatcher.find()) {
	        String fileName = fileMatcher.group(1); 
	        dDao.updateDmIdxToFiles(dto.getDmIdx(), fileName);
	    }
	    
	}
	
	/** [SB] 왼쪽에 나오는 디엠목록조회 
	 * input : userId, workspaceIdx , dmDao
	 * output : List<DmLeftListDto>
	 */
	@Override
	public List<DmLeftListDto> getLeftList(String userId, int workspaceIdx) {
		List<DmWorkspaceUsersDto> listUsers = dDao.selectUserIdFromDm(userId, workspaceIdx);

		Set<String> otherUserIdList = new HashSet<>();
		
		for (DmWorkspaceUsersDto dto : listUsers) {
			if (dto.getDmFrom().equals(userId)) {
				otherUserIdList.add(dto.getDmTo());
			} else {
				otherUserIdList.add(dto.getDmFrom());
			}
		}


		List<DmLeftListDto> listRet = new ArrayList<>();
		
		for (String otherUserId : otherUserIdList) {
			DmLeftListDto dllDto = new DmLeftListDto();
			
			DmDto dto = dDao.selectDmList(userId, otherUserId, workspaceIdx);
			dllDto.setDmIdx(dto.getDmIdx());
			dllDto.setContent(dto.getContent());
			dllDto.setSentTime(dto.getSentTime());
			WorkspaceUsersDto users = dDao.selectInfoFromWorkspaceUsers(otherUserId, workspaceIdx);
			dllDto.setProfileImage(users.getProfileImage());
			dllDto.setNickname(users.getNickname());
			
			dllDto.setDmTo(otherUserId);
			listRet.add(dllDto);
		}
		// sentTime 기준 내림차순 (최신순) 정렬
		Collections.sort(listRet, (o1, o2) -> o2.getSentTime().compareTo(o1.getSentTime()));
		return listRet;
	}

	/** [SB] 디엠상세내용조회 
	 * input : dmIdx, userId, dmDao
	 * output : List<DmListDto>
	 */
	@Override
	public List<DmListDto> getDmListDetail(int dmIdx, String userId) {
		DmDetailListDto dto = dDao.selectFromToByDmIdx(dmIdx);
	      
	       String myId = userId; // 로그인한 나
	       String opponentId;   // 상대방
	       
	       // [핵심] 무조건 '나'와 다른 ID를 가진 사람이 상대방입니다.
	       if (dto.getDmFrom().equals(myId)) {
	           opponentId = dto.getDmTo();
	       } else {
	           opponentId = dto.getDmFrom();
	       }
	      int workspaceIdx = dto.getWorkspaceIdx(); //워크스페이스 Idx 가져오기
	      List<DmListDto> dmList = dDao.selectDmDetail(myId, opponentId, workspaceIdx); //
	      
	      Map<String, String> mapImageProfile = new HashMap<>();
	      Map<String, String> mapNickname = new HashMap<>();
	      for(DmListDto dto1 : dmList) {
	         dto1.setDmToId(opponentId);       // 파일업로드 ajax controller에서 상대방 아이디를 가져오기위해 필요함. 
	         if(mapImageProfile.get(dto1.getDmTo())!=null) {
	            dto1.setToProfileImage(mapImageProfile.get(dto1.getDmTo()));
	         } else {
	            String profileImage = wDao.workspaceProfileNameSelect(workspaceIdx, dto1.getDmTo());
	            dto1.setToProfileImage(profileImage);
	            mapImageProfile.put(dto1.getDmTo(), profileImage);
	         }
	         if(mapImageProfile.get(dto1.getDmFrom())!=null) {
	            dto1.setFromProfileImage(mapImageProfile.get(dto1.getDmFrom()));
	         } else {
	            String profileImage = wDao.workspaceProfileNameSelect(workspaceIdx, dto1.getDmFrom());
	            dto1.setFromProfileImage(wDao.workspaceProfileNameSelect(workspaceIdx, dto1.getDmFrom()));
	            mapImageProfile.put(dto1.getDmFrom(), profileImage);
	         }
	         if(mapNickname.get(dto1.getDmFrom())!=null) {
	            dto1.setDmFrom(mapNickname.get(dto1.getDmFrom()));
	         } else {
	            String nickname = dDao.selectUsersDm(dto1.getDmFrom(), workspaceIdx);
	            dto1.setDmFrom(nickname);
	         }
	         if(mapNickname.get(dto1.getDmTo())!=null) {
	            dto1.setDmTo(mapNickname.get(dto1.getDmTo()));
	         } else {
	            String nickname = dDao.selectUsersDm(dto1.getDmTo(), workspaceIdx);
	            dto1.setDmTo(nickname);
	         }
	      }
	      return dmList;
	}
	
	/** [SB] 유저아이디조회 
	 * input : userId, workspaceIdx 
	 * output : List<DmUsersDto>
	 */
	@Override
	public List<DmUsersDto> getUsersNameForNewDm(String userId, int workspaceIdx) {
		return dDao.selectUsersForNewDm(userId, workspaceIdx);
	}
	
	/** [SB] 닉네임조회 
	 * input : userId, workspaceIdx
	 * output : nickname
	 */
	@Override
	public String getNicknameForDm(String userId, int workspaceIdx) {
		return dDao.selectUsersDm(userId, workspaceIdx);
	}
	
	/** [SB] dmIdx로 dmFromTo조회 
	 * input : dmIdx
	 * output : DmDetailListDto 
	 */
	@Override
	public DmDetailListDto getFromToByDmIdx(int dmIdx) {
		
		return dDao.selectFromToByDmIdx(dmIdx);
	}
	
	/** [SB] 디엠idx의 최댓값조회 
	 * input : dmFrom, dmTo, workspaceIdx 
	 * output : dmIdx
	 */
	@Override
	public Integer getMaxDmByDmFromDmTo(String dmFrom, String dmTo, int workspaceIdx) {
		return dDao.selectDmMax(dmFrom, dmTo, workspaceIdx);
	}
	
	/** [SB] 멘션추가 
	 * input : userId, dmIdx, userMentioned, workspaceIdx 
	 * output : 
	 */
	@Override
	public void addMention(String userId, int dmIdx, String userMentioned, int workspaceIdx) {
		dDao.insertMention(userId, dmIdx, userMentioned, workspaceIdx);
	}
}
