package com.slack.dao;

import java.util.List;

import com.slack.dto.DmContentDto;
import com.slack.dto.DmDetailListDto;
import com.slack.dto.DmDto;
import com.slack.dto.DmListDto;
import com.slack.dto.DmUsersDto;
import com.slack.dto.DmWorkspaceUsersDto;
import com.slack.dto.WorkspaceUsersDto;

public interface DmDao {
	// [SB] 멘션추가기능
	void insertMention(String userId, int dmIdx, String userMentioned, int workspaceIdx);
	// [SB] 키워드로 디엠목록조회
	List<DmContentDto> selectDmListByContent(String dmFrom,  String keyword, int workspaceIdx);
	// [SB] 디엠추가기능
	void insertDm(DmListDto dto);
	// [SB] 디엠idx로 dmTo조회 
	DmDetailListDto selectFromToByDmIdx(int dmIdx);
	// [SB] 디엠상세내용조회
	List<DmListDto> selectDmDetail(String dmFrom, String dmTo, int workspaceIdx);
	// [SB] 디엠으로 유저아이디 조회
	List<DmWorkspaceUsersDto> selectUserIdFromDm(String dmFromTo, int workspaceIdx);
	// [SB] 디엠목록조회
	DmDto selectDmList(String a, String b, int workspaceIdx);
	// [SB] 워크스페이스에서 사용자정보 조회
	WorkspaceUsersDto selectInfoFromWorkspaceUsers(String userId, int workspaceIdx);
	// [SB] 유저정보조회 ( 새메세지 )
	List<DmUsersDto> selectUsersForNewDm(String userId, int workspaceIdx);
	// [SB] 유저정보조회 ( 디엠 )
	String selectUsersDm(String userId, int workspaceIdx);
	// [SB] dmIdx 최댓값 조회 
	Integer selectDmMax(String dmFrom, String dmTo, int workspaceIdx);
	// [SB] 파일테이블에서 dmIdx 수정 
	void updateDmIdxToFiles(int dmIdx, String fileName);
}
