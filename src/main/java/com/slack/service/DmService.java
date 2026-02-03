package com.slack.service;

import java.util.List;

import com.slack.dto.DmContentDto;
import com.slack.dto.DmDetailListDto;
import com.slack.dto.DmLeftListDto;
import com.slack.dto.DmListDto;
import com.slack.dto.DmUsersDto;

public interface DmService {
	// [SB] 멘션추가기능
	void addMention(String userId,  int dmIdx, String userMentioned, int workspaceIdx);
	// [SB] 키워드로 디엠목록조회
	List<DmContentDto>selectDmListByContent(String dmFrom, String keyword, int workspaceIdx);
	// [SB] 디엠추가기능
	void addDm(DmListDto dto);
	List<DmLeftListDto> getLeftList(String userId, int workspaceIdx);
	// [SB] 디엠상세내용조회
	List<DmListDto> getDmListDetail (int dmIdx, String userId);
	// [SB] 유저정보조회 ( 새메세지 )
	List<DmUsersDto> getUsersNameForNewDm(String userId ,int workspaceIdx);
	String getNicknameForDm(String userId, int workspaceIdx);
	// [SB] 디엠idx로 dmTo조회 
	DmDetailListDto getFromToByDmIdx(int dmIdx);
	// [SB] dmIdx 최댓값 조회 
	Integer getMaxDmByDmFromDmTo(String dmFrom, String dmTo, int workspaceIdx);
}
