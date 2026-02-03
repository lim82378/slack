package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelMsgInviteWorkspaceUsersDto {
	private String content; // 채널 메시지 내용 
	private String inviteTime; // 채널 메시지 보낸시간 
	private String userInvite;
	private String userInvited;
	private String profileImage;
	private String channelName;
	private String nickname;
	private String type;      // INVITE 또는 MENTION
	private int dmIdx;        //
	private int channelIdx;   //
}