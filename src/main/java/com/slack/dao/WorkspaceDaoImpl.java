package com.slack.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slack.dto.UsersDto;
import com.slack.dto.WorkspaceDto;
import com.slack.dto.WorkspaceUsersMentionDto;

@Repository
public class WorkspaceDaoImpl implements WorkspaceDao{
	@Autowired
	SqlSession sqlSession;
	
	/** [MJ] 워크스페이스 이름 조회
	 * 	input : workspaceIdx
	 * 	output : workspaceName
	 */
	@Override
	public String workspaceNameSelect(int workspaceIdx) {
		return sqlSession.selectOne("WorkspaceMapper.workspaceNameSelect",workspaceIdx);
	}
	
	/** [MJ] 워크스페이스 구성원 수 조회
	 * 	input : workspaceIdx
	 * 	output : count
	 */
	@Override
	public int workspaceUsersCount(int workspaceIdx) {
		return sqlSession.selectOne("WorkspaceMapper.workspaceUsersCount",workspaceIdx);
	}
	
	/** [MJ] 워크스페이스 구성원 이메일, 이름 검색시
	 * 	input : workspaceIdx, search
	 * 	output : List<Map<String, Object>>
	 */
	@Override
	public List<Map<String, Object>> workspaceUserSearch(int workspaceIdx, String search) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("workspaceIdx", workspaceIdx);
		map.put("search", search);
		return sqlSession.selectList("WorkspaceMapper.workspaceUserSearch",map);
	}
	
	/** [MJ] 워크스페이스에 초대(조회 및 추가)
	 * 	input : userId
	 * 	output : UsersDto
	 */
	@Override
	public UsersDto usersSelect(String userId) {
		return sqlSession.selectOne("WorkspaceMapper.usersSelect",userId);
	}
	
	/** [MJ] 워크스페이스 사용자 추가
	 * 	input : workspaceIdx, userId, nickname, profileImage
	 * 	output : -
	 */
	@Override
	public void workspaceInsert(int workspaceIdx, String userId, String nickname, String profileImage) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("workspaceIdx", workspaceIdx);
		map.put("userId", userId);
		map.put("nickname", nickname);
		map.put("profileImage", profileImage);
		sqlSession.insert("WorkspaceMapper.workspaceInsert", map);
	}
	
	/** [MJ] 워크스페이스 유저 프로필 업로드
	 * 	input : workspaceIdx, profileImage, userId
	 * 	output : -
	 */
	@Override
	public void profileUpload(String profileImage, String userId, int workspaceIdx) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("profileImage", profileImage);
		map.put("userId", userId);
		map.put("workspaceIdx", workspaceIdx);
		sqlSession.update("WorkspaceMapper.profileUpload",map);
	}
	
	
	/** [SB] 워크스페이스에 추가  
	 * input : workspaceName, userId
	 * output : 
	 */
	@Override
	public void insertWorkspace( String workspaceName, String userId) {
		Map<String, Object>map1 = new HashMap<>();
		map1.put("workspaceName", workspaceName);
		map1.put("userId", userId);
		
		sqlSession.insert("WorkspaceMapper.insertWorkspace", map1);
	}
	
	/** [SB] 워크스페이스 유저스테이블에 추가  
	 * input : workspaceIdx, userId, nickname, profileImage
	 * output : 
	 */
	@Override
	public void insertWorkspaceUsers(int workspaceIdx, String userId, String nickname, String profileImage) {
		Map<String, Object>map1 = new HashMap<>();
		map1.put("workspaceIdx", workspaceIdx);
		map1.put("userId", userId);
		map1.put("nickname", nickname);
		map1.put("profileImage", profileImage);
		sqlSession.insert("WorkspaceMapper.insertWorkspaceUsers", map1);
	}
	
	/** [SB] 워크스페이스idx 최댓값 조회 
	 * input : 
	 * output : workspaceIdx
	 */
	@Override
	public int selectMaxWorkspaceIdx() {
		return sqlSession.selectOne("WorkspaceMapper.selectMaxWorkspaceIdx");
	}
	
	/** [SB] 초대코드 추가  
	 * input : workspaceIdx, userId, iniviteCode
	 * output : 
	 */
	@Override
	public void insertInviteCode(int workspaceIdx, String userId, String inviteCode) {
		Map<String, Object>map1 = new HashMap<>();
		map1.put("workspaceIdx", workspaceIdx);
		map1.put("userId", userId);
		map1.put("inviteCode", inviteCode);
		sqlSession.insert("WorkspaceMapper.insertInviteCode", map1);
	}
	
	/** [SB] 워크스페이스 이름조회 
	 * input : workspaceIdx
	 * output : workspaceName
	 */
	@Override
	public String selectWorkspaceName(int workspaceIdx) {
		String wName = sqlSession.selectOne("WorkspaceMapper.selectWorkspaceName",workspaceIdx);	
		System.out.println("dao, workspaceName : " + wName);
		return wName;
	}
	
	/** [SB] 프로필이미지 수정 
	 * input : workspaceIdx, userId, profileImage
	 * output : 
	 */
	@Override
	public void updateProfileImage(int workspaceIdx, String userId, String profileImage) {
		Map<String, Object>map1 = new HashMap<>();
		map1.put("workspaceIdx", workspaceIdx);
		map1.put("userId", userId);
		map1.put("profileImage", profileImage);
		sqlSession.update("WorkspaceMapper.updateProfileImage", map1);
	}
	
	/** [SB] 닉네임, 유저아이디 조회  
	 * input : workspaceIdx
	 * output : List<WorkspaceUsersMentionDto>
	 */
	@Override
	public List<WorkspaceUsersMentionDto> selectUserIdAndNicknameForMention(int workspaceIdx) {
		return sqlSession.selectList("WorkspaceMapper.selectUserIdAndNickNameForMention", workspaceIdx);
	}
	
	
	
	@Override
	public List<WorkspaceDto> selectAllWorkspaceInfo(String userId) {
		return sqlSession.selectList("WorkspaceMapper.selectAllWorkspaceInfo",userId);
	}
	@Override
	public String selectUsersNickname(String userId) {
		return sqlSession.selectOne("WorkspaceMapper.selectUsersNickname",userId);
	}
	@Override
	public int workspaceListCheck(String userId) {
		return sqlSession.selectOne("WorkspaceMapper.workspaceListCheck",userId);
	}
	@Override
	public String workspaceProfileNameSelect(int workspaceIdx, String userId) {
		Map<String, Object>map = new HashMap<>();
		map.put("workspaceIdx", workspaceIdx);
		map.put("userId", userId);
		return sqlSession.selectOne("WorkspaceMapper.workspaceProfileNameSelect",map);
	}

}
