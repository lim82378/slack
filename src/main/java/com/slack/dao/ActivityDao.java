package com.slack.dao;

import java.util.List;

import com.slack.dto.ChannelMsgInviteWorkspaceUsersDto;
import com.slack.dto.DmMentionDto;

public interface ActivityDao {
	
	//[SB] 채널 초대 목록조회
	List<ChannelMsgInviteWorkspaceUsersDto>selectListOfActivityByChannelInvite(String userInvited, int workSpaceIdx);
	//[SB] 멘션 목록조회
	List<DmMentionDto>selectListOfActivityByDmMention(String userMentioned, int workSpaceIdx);
}
