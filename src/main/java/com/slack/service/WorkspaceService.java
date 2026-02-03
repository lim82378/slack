package com.slack.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.slack.dto.UsersDto;
import com.slack.dto.WorkspaceDto;
import com.slack.dto.WorkspaceUsersMentionDto;


public interface WorkspaceService {
	//[MJ] 워크스페이스 이름 조회
	String workspaceNameSelect(int workspaceIdx);
	//[MJ] 워크스페이스 구성원 수 조회
	int workspaceUsersCount(int workspaceIdx);
	//[MJ] 워크스페이스 구성원 이메일, 이름 검색시
	List<Map<String, Object>> workspaceUserSearch(int workspaceIdx, String search);
	//[MJ] 워크스페이스에 초대(조회 및 추가)
	UsersDto usersSelect(String userId);
	//[MJ] 워크스페이스 사용자 추가
	void workspaceInsert(int workspaceIdx, String userId, String nickname, String profileImage);
	//[MJ] 워크스페이스 유저 프로필 업로드
	void profileUpload(String profileImage,String userId, int workspaceIdx);
	//[SB] 워크스페이스에 추가 
	int insertNewWorkspaceAndUsers( String workspaceName, String userId,String nickname, String profileImage);
	//[SB] 초대코드 추가 
	void insertInviteCode(int workspaceIdx, String userId, String inviteCode);
	//[SB] 워크스페이스idx로 워크스페이스 이름조회
	String selectWorkspaceNameByIdx(int workspaceIdx);
	//[SB] 프로필이미지 수정 
	void changeProfileImage(int workspaceIdx, String userId, String profileImage);
	//[SB] 유저아이디, 닉네임조회 
	List<WorkspaceUsersMentionDto>getUserIdAndNickname(int workspaceIdx);
	
	List<WorkspaceDto> selectAllWorkspaceInfo(String userId);
	String selectUsersNickname(String userId);
	int workspaceListCheck(String userId);
	String workspaceProfileNameSelect(int workspaceIdx, String userId);
}
