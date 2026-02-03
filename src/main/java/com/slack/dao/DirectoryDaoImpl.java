package com.slack.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slack.dto.Users3Dto;
import com.slack.dto.UsersProfileDto;

@Repository
public class DirectoryDaoImpl implements DirectoryDao{
	@Autowired
	SqlSession sqlSession;
	
	/** [MJ] 워크스페이스 구성원 조회
	 *  input : workspaceIdx, orderByType
	 *  output : List<Users3Dto>
	 */
	@Override
	public List<Users3Dto> getWorkspaceMembers(int workspaceIdx, String orderByType) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("workspaceIdx", workspaceIdx);
		map1.put("orderByType", orderByType);
		return sqlSession.selectList("DirectoryMapper.getWorkspaceMembers",map1);
	}
	
	/** [MJ] 워크스페이스 나의 프로필 이미지 조회
	 *  input : workspaceIdx, userId
	 *  output : Users3Dto
	 */
	@Override
	public Users3Dto getWorkspaceMyImg(int workspaceIdx, String userId) {
		HashMap<String, Object> map1= new HashMap<>();
		map1.put("workspaceIdx", workspaceIdx);
		map1.put("userId", userId);
		return sqlSession.selectOne("DirectoryMapper.getWorkspaceMyImg",map1);
	}
	
	/** [MJ] 프로필 상세 조회
	 *  input : workspaceIdx, userId
	 *  output : UsersProfileDto
	 */
	@Override
	public UsersProfileDto getProfileDetail(int workspaceIdx, String userId) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("workspaceIdx", workspaceIdx);
		map1.put("userId", userId);
		return sqlSession.selectOne("DirectoryMapper.getProfileDetail",map1);
	}
	
	/** [MJ] 특정 워크스페이스 내의 프로필 목록 조회
	 *  input : workspaceIdx
	 *  output : List<UsersProfileDto>
	 */
	@Override
	public List<UsersProfileDto> getProfileListWithWorkspaceIdx(int workspaceIdx) {
		return sqlSession.selectList("DirectoryMapper.allProfileSelect",workspaceIdx);
	}
	
	/** [MJ] 내 프로필 상태설정
	 *  input : condition, userId
	 *  output : -
	 */
	@Override
	public void updateSetMyCondition(String condition, String userId) {
		HashMap<String,String> map1=new HashMap<>();
		map1.put("condition", condition);
		map1.put("userId", userId);
		sqlSession.update("DirectoryMapper.setMyCondition",map1);
	}
	
	/** [MJ] 워크스페이스 사용자 검색
	 *  input : workspaceIdx, search
	 *  output : List<Map<String, Object>>
	 */
	@Override
	public List<Map<String, Object>> workspaceUsersSearch(int workspaceIdx, String search) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("workspaceIdx", workspaceIdx);
		map.put("search", search);
		return sqlSession.selectList("DirectoryMapper.workspaceUsersSearch",map);
	}
	
	/** [MJ] 내 프로필 이름, 직함 변경
	 *  input : name, title, userId
	 *  output : -
	 */
	@Override
	public void setMyProfileNameAndTitle(String name, String title, String userId) {
		HashMap<String,String> map=new HashMap<>();
		map.put("name", name);
		map.put("title", title);
		map.put("userId",userId);
		sqlSession.update("DirectoryMapper.setMyProfileNameAndTitle",map);
	}
	
	/** [MJ] 내 프로필 워크스페이스의 닉네임 변경
	 *  input : nickname, userId, workspaceIdx
	 *  output : -
	 */
	@Override
	public void setMyProfileNickname(String nickname, String userId, int workspaceIdx) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("nickname", nickname);
		map.put("userId", userId);
		map.put("workspaceIdx", workspaceIdx);
		sqlSession.update("DirectoryMapper.setMyProfileNickname",map);
	}
}
