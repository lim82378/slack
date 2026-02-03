package com.slack.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slack.dao.WorkspaceDao;
import com.slack.dto.UsersDto;
import com.slack.dto.WorkspaceDto;
import com.slack.dto.WorkspaceUsersMentionDto;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
	@Autowired
	WorkspaceDao wDao;
	
	/** [MJ] 워크스페이스 이름 조회
	 * 	input : workspaceIdx
	 * 	output : workspaceName
	 */
	@Override
	public String workspaceNameSelect(int workspaceIdx) {
		return wDao.workspaceNameSelect(workspaceIdx);
	}
	
	/** [MJ] 워크스페이스 구성원 수 조회
	 * 	input : workspaceIdx
	 * 	output : count
	 */
	@Override
	public int workspaceUsersCount(int workspaceIdx) {
		return wDao.workspaceUsersCount(workspaceIdx);
	}
	
	/** [MJ] 워크스페이스 구성원 이메일, 이름 검색시
	 * 	input : workspaceIdx, search
	 * 	output : List<Map<String, Object>>
	 */
	@Override
	public List<Map<String, Object>> workspaceUserSearch(int workspaceIdx, String search) {
		return wDao.workspaceUserSearch(workspaceIdx, search);
	}
	
	/** [MJ] 워크스페이스에 초대(조회 및 추가)
	 * 	input : userId
	 * 	output : UsersDto
	 */
	@Override
	public UsersDto usersSelect(String userId) {
		return wDao.usersSelect(userId);
	}
	
	/** [MJ] 워크스페이스 사용자 추가
	 * 	input : workspaceIdx, userId, nickname, profileImage
	 * 	output : -
	 */
	@Override
	public void workspaceInsert(int workspaceIdx, String userId, String nickname, String profileImage) {
		wDao.workspaceInsert(workspaceIdx, userId, nickname, profileImage);
	}
	
	/** [MJ] 워크스페이스 유저 프로필 업로드
	 * 	input : workspaceIdx, profileImage, userId
	 * 	output : -
	 */
	@Override
	public void profileUpload(String profileImage, String userId, int workspaceIdx) {
		wDao.profileUpload(profileImage, userId, workspaceIdx);
	}
	
	/** [SB] 워크스페이스에 추가  
	 * input : workspaceName, userId
	 * output : 
	 */
	@Override
	@Transactional
	public int insertNewWorkspaceAndUsers(String workspaceName, String userId, String nickname, String profileImage) {
		wDao.insertWorkspace(workspaceName, userId);
		int workspaceIdx = wDao.selectMaxWorkspaceIdx();
		wDao.insertWorkspaceUsers(workspaceIdx, userId, nickname, profileImage);
		return workspaceIdx;
	}
	
	/** [SB] 초대코드 추가  
	 * input : workspaceIdx, userId, iniviteCode
	 * output : 
	 */
	@Override
	public void insertInviteCode(int workspaceIdx, String userId, String inviteCode) {
		wDao.insertInviteCode(workspaceIdx, userId, inviteCode);
	}
	
	/** [SB] 워크스페이스 이름조회 
	 * input : workspaceIdx
	 * output : workspaceName
	 */
	@Override
	public String selectWorkspaceNameByIdx(int workspaceIdx) {
		return wDao.selectWorkspaceName(workspaceIdx);
	}
	
	/** [SB] 프로필이미지 수정 
	 * input : workspaceIdx, userId, profileImage
	 * output : 
	 */
	@Override
	public void changeProfileImage(int workspaceIdx, String userId, String profileImage) {
		wDao.updateProfileImage(workspaceIdx, userId, profileImage);
	}
	
	/** [SB] 닉네임, 유저아이디 조회  
	 * input : workspaceIdx
	 * output : List<WorkspaceUsersMentionDto>
	 */
	@Override
	public List<WorkspaceUsersMentionDto> getUserIdAndNickname(int workspaceIdx) {
		return wDao.selectUserIdAndNicknameForMention(workspaceIdx);
	}
	
	
	
	
	
	@Override
	public List<WorkspaceDto> selectAllWorkspaceInfo(String userId) {
		return wDao.selectAllWorkspaceInfo(userId);
	}
	@Override
	public String selectUsersNickname(String userId) {
		return wDao.selectUsersNickname(userId);
	}
	@Override
	public int workspaceListCheck(String userId) {
		return wDao.workspaceListCheck(userId);
	}
	@Override
	public String workspaceProfileNameSelect(int workspaceIdx, String userId) {
		return wDao.workspaceProfileNameSelect(workspaceIdx, userId);
	}
}
