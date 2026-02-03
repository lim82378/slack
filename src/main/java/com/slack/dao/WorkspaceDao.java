package com.slack.dao;

import java.util.List;
import java.util.Map;

import com.slack.dto.UsersDto;
import com.slack.dto.WorkspaceDto;
import com.slack.dto.WorkspaceUsersMentionDto;

public interface WorkspaceDao {
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
	//[SB]워크스페이스 구성원 추가 
	void insertWorkspace(String workspaceName, String userId);
	//[SB]워크스페이스idx 최댓값조회 
	int selectMaxWorkspaceIdx();
	//[SB] 워크스페이스 유저스 테이블에 추가 
	void insertWorkspaceUsers(int workspaceIdx, String userId, String nickname, String profileImage);
	//[SB] 초대코드 추가 
	void insertInviteCode(int workspaceIdx, String userId, String inviteCode);
	//[SB] 워크스페이스이름 조회
	String selectWorkspaceName(int workspaceIdx);
	//[SB] 유저아이디, 닉네임 조회 
	List<WorkspaceUsersMentionDto> selectUserIdAndNicknameForMention(int workspaceIdx);
	//[SB] 프로필이미지 수정
	void updateProfileImage(int workspaceIdx, String userId, String profileImage);
	
	List<WorkspaceDto> selectAllWorkspaceInfo(String userId);
	String selectUsersNickname(String userId);
	int workspaceListCheck(String userId);
	String workspaceProfileNameSelect(int workspaceIdx, String userId);
}
