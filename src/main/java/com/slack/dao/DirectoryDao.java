package com.slack.dao;

import java.util.List;
import java.util.Map;

import com.slack.dto.Users3Dto;
import com.slack.dto.UsersProfileDto;

public interface DirectoryDao {
	//[MJ] 워크스페이스 구성원 조회
	List<Users3Dto> getWorkspaceMembers(int workspaceIdx, String orderByType);
	//[MJ] 워크스페이스 내 프로필 조회
	Users3Dto getWorkspaceMyImg(int workspaceIdx, String userId);
	//[MJ] 프로필 상세 조회
	UsersProfileDto getProfileDetail(int workspaceIdx, String userId);
	//[MJ] 특정 워크스페이스 내의 프로필 목록 조회
	List<UsersProfileDto> getProfileListWithWorkspaceIdx(int workspaceIdx);
	//[MJ] 내 프로필 상태설정
	void updateSetMyCondition(String condition, String userId);
	//[MJ] 워크스페이스 사용자 검색
	List<Map<String, Object>> workspaceUsersSearch(int workspaceIdx ,String search);
	//[MJ] 내 프로필 이름, 직함 변경
	void setMyProfileNameAndTitle(String name, String title, String userId);
	//[MJ] 내 프로필 워크스페이스의 닉네임 변경
	void setMyProfileNickname(String nickname, String userId, int workspaceIdx);
}
