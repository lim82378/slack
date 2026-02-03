package com.slack.service;

import java.util.List;

import com.slack.dto.ChannelMsgInviteWorkspaceUsersDto;
import com.slack.dto.DmMentionDto;

public interface ActivityService {
	//[SB] 채널초대내역 조회
	List<ChannelMsgInviteWorkspaceUsersDto> selectListOfMyActivityByChannel(String userInvited, int workspaceIdx);
	//[SB] 멘션내역 조회
	List<DmMentionDto> selectListOfMyActivityByDmMention(String userMentioned, int workspaceIdx);
}
