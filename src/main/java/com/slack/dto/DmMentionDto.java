package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DmMentionDto {
	private String userId; 
	private String userMentioned;
	private String content;
	private String channelName; 
	private String sentTime;
	private String profileImage;
	private String nickname;
	private String mentionNickname;
	private String type;      // INVITE 또는 MENTION
	private int dmIdx;        //
	private int channelIdx;   //
	public String getContent() { return this.content; }
	public String getType() { return this.type; }
	public int getChannelIdx() { return this.channelIdx; }
}