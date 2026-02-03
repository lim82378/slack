package com.slack.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slack.dto.DmContentDto;
import com.slack.dto.DmDetailListDto;
import com.slack.dto.DmDto;
import com.slack.dto.DmListDto;
import com.slack.dto.DmUsersDto;
import com.slack.dto.DmWorkspaceUsersDto;
import com.slack.dto.MentionDto;
import com.slack.dto.WorkspaceUsersDto;

@Repository
public class DmDaoImpl implements DmDao {
	@Autowired
	SqlSession sqlSession;
	
	/** [SB] 멘션추가기능
	 *  input : userId, dmIdx, userMentioned, workspaceIdx
	 *  output : 없음
	 */
	@Override
	public void insertMention(String userId, int dmIdx, String userMentioned, int workspaceIdx) {
		MentionDto dto = new MentionDto();
		dto.setUserId(userId); 
		dto.setDmIdx(dmIdx);
		dto.setUserMentioned(userMentioned);
		dto.setWorkspaceIdx(workspaceIdx);
		
		sqlSession.insert("DmMapper.insertMention", dto );
	}
	/** [SB] 키워드로 디엠목록조회
	 *  input : dmFrom, keyword, workspaceIdx
	 *  output : List<DmContentDto>
	 */
	@Override
	public List<DmContentDto> selectDmListByContent(String dmFrom, String keyword, int workspaceIdx) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("dmFrom", dmFrom);
		map1.put("keyword", keyword);
		map1.put("workspaceIdx", workspaceIdx);
		return sqlSession.selectList("DmMapper.selectDmListByContent", map1);
	}
	/** [SB] 디엠추가기능
	 *  input : DmListDto
	 *  output : 
	 */
	@Override
	public void insertDm(DmListDto dto) {
		sqlSession.insert("DmMapper.insertDm",dto);
		
	}
	/** [SB] 디엠상세내용조회
	 *  input : dmFrom, dmTo,m workspaceIdx
	 *  output : List<DmListDto>
	 */
	@Override
	public List<DmListDto> selectDmDetail(String dmFrom, String dmTo, int workspaceIdx) {
		Map<String, Object> map1 = new HashMap<>();
		map1.put("dmFrom",dmFrom);
		map1.put("dmTo",dmTo);
		map1.put("workspaceIdx",workspaceIdx);
		return sqlSession.selectList("DmMapper.selectDmDetail", map1);
	}
	/** [SB] 유저아이디조회
	 *  input : dmFromTo, workspaceIdx
	 *  output : List<DmWorkspaceUsersDto>
	 */
	@Override
	public List<DmWorkspaceUsersDto> selectUserIdFromDm(String dmFromTo, int workspaceIdx) {
		Map<String, Object> map1 = new HashMap<>();
		map1.put("dmFromTo",dmFromTo);
		map1.put("workspaceIdx",workspaceIdx);
		return sqlSession.selectList("DmMapper.selectUserIdFromDm", map1);
	}
	/** [SB] 디엠idx로 dmTo조회 
	 *  input : dmIdx
	 *  output : DmDetailListDto
	 */
	@Override
	public DmDetailListDto selectFromToByDmIdx(int dmIdx) {
		return sqlSession.selectOne("DmMapper.selectFromToByDmIdx", dmIdx);
	}
	/** [SB] 디엠목록조회
	 *  input : a, b, workspaceIdx
	 *  output : DmDto
	 */
	@Override
	public DmDto selectDmList(String a, String b, int workspaceIdx) {
		Map<String, Object> map1 = new HashMap<>();
		map1.put("a",a);
		map1.put("b",b);
		map1.put("workspaceIdx",workspaceIdx);
		return sqlSession.selectOne("DmMapper.selectDmList", map1);
	}
	/** [SB] 워크스페이스 유저스테이블에서 사용자정보 조회
	 *  input : userId, workspaceIdx
	 *  output : workspaceUsersDto
	 */
	@Override
	public WorkspaceUsersDto selectInfoFromWorkspaceUsers(String userId, int workspaceIdx) {
		Map<String, Object> map1 = new HashMap<>();
		map1.put("userId",userId);
		map1.put("workspaceIdx",workspaceIdx);
		WorkspaceUsersDto dto = sqlSession.selectOne("DmMapper.selectInfoFromWorkspaceUsers", map1);
		return dto;
	}
	/** [SB] 유저아이디조회 ( 새매세지 ) 
	 *  input : userId , workspaceIdx
	 *  output : List<DmUsersDto>
	 */
	@Override
	public List<DmUsersDto> selectUsersForNewDm(String userId, int workspaceIdx) {
		Map<String, Object>map1 = new HashMap<>();
		map1.put("userId", userId);
		map1.put("workspaceIdx", workspaceIdx);
		return sqlSession.selectList("DmMapper.selectUsersFromNewDm", map1);
	}
	/** [SB] 유저정보조회 ( 디엠 ) 
	 *  input : userId, workspaceIdx
	 *  output : nickname
	 */
	@Override
	public String selectUsersDm(String userId, int workspaceIdx) {
		Map<String, Object>map1 = new HashMap<>();
		map1.put("userId", userId);
		map1.put("workspaceIdx", workspaceIdx);
		return sqlSession.selectOne("DmMapper.selectUsersDm", map1);
	}
	/** [SB] dmIdx 최댓값 조회 
	 *  input : dmFrom, dmTo, workspaceIdx	
	 *  output : dmIdx
	 */
	@Override
	public Integer selectDmMax(String dmFrom, String dmTo, int workspaceIdx) {
		Map<String, Object>map1 = new HashMap<>();
		map1.put("dmFrom", dmFrom);
		map1.put("dmTo", dmTo);
		map1.put("workspaceIdx", workspaceIdx);
		return sqlSession.selectOne("DmMapper.selectDmMax", map1);
	}
	/** [SB] 디엠idx수정 
	 *  input : dmIdx, fileName
	 *  output :
	 */
	@Override
	public void updateDmIdxToFiles(int dmIdx, String fileName) {
		Map<String, Object>map1 = new HashMap<>();
		map1.put("dmIdx", dmIdx);
		map1.put("fileName", fileName);
		sqlSession.update("DmMapper.updateDmIdxToFiles", map1);
	}
}





