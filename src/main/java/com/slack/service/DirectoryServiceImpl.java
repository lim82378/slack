package com.slack.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slack.dao.DirectoryDao;
import com.slack.dto.Users3Dto;
import com.slack.dto.UsersProfileDto;

@Service
public class DirectoryServiceImpl implements DirectoryService  {
	@Autowired
	DirectoryDao dDao;
	
	/** [MJ] 워크스페이스 구성원 조회
	 *  input : workspaceIdx, orderByType
	 *  output : List<Users3Dto>
	 */
	@Override
	public List<Users3Dto> getWorkspaceMembers(int workspaceIdx, String orderByType) {
		return dDao.getWorkspaceMembers(workspaceIdx, orderByType);
	}
	
	/** [MJ] 워크스페이스 나의 프로필 이미지 조회
	 *  input : workspaceIdx, userId
	 *  output : Users3Dto
	 */
	@Override
	public Users3Dto getWorkspaceMyImg(int workspaceIdx, String userId) {
		return dDao.getWorkspaceMyImg(workspaceIdx, userId);
	}
	
	/** [MJ] 프로필 상세 조회
	 *  input : workspaceIdx, userId
	 *  output : UsersProfileDto
	 */
	@Override
	public UsersProfileDto getProfileDetail(int workspaceIdx, String userId) {
		return dDao.getProfileDetail(workspaceIdx, userId);
	}
	
	/** [MJ] 특정 워크스페이스 내의 프로필 목록 조회
	 *  input : workspaceIdx
	 *  output : List<UsersProfileDto>
	 */
	@Override
	public List<UsersProfileDto> getProfileListWithWorkspaceIdx(int workspaceIdx) {
		return dDao.getProfileListWithWorkspaceIdx(workspaceIdx);
	}
	
	/** [MJ] 내 프로필 상태설정
	 *  input : condition, userId
	 *  output : -
	 */
	@Override
	public void updateSetMyCondition(String condition, String userId) {
		dDao.updateSetMyCondition(condition, userId);
	}
	
	/** [MJ] 워크스페이스 사용자 검색
	 *  input : workspaceIdx, search
	 *  output : List<Map<String, Object>>
	 */
	@Override
	public List<Map<String, Object>> workspaceUsersSearch(int workspaceIdx, String search) {
		return dDao.workspaceUsersSearch(workspaceIdx ,search);
	}
	
	/** [MJ] 내 프로필 이름, 직함 변경
	 *  input : name, title, userId
	 *  output : -
	 */
	@Override
	public void setMyProfileNameAndTitle(String name, String title, String userId) {
		dDao.setMyProfileNameAndTitle(name, title, userId);
	}
	
	/** [MJ] 내 프로필 워크스페이스의 닉네임 변경
	 *  input : nickname, userId, workspaceIdx
	 *  output : -
	 */
	@Override
	public void setMyProfileNickname(String nickname, String userId, int workspaceIdx) {
		dDao.setMyProfileNickname(nickname, userId, workspaceIdx);
	}

	
}
