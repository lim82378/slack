package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Channel5Dto {
	private int channelIdx;
	private String channelName;
	private String nickname;
	private String userId;
	private String content;
	private String sentTime;
	private int fileIdx;
	private String createdtime;
	private String profileImage;
}
