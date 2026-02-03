package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DmLeftListDto {
	private int dmIdx;
	private String dmTo;
	private String profileImage;
	private String nickname;
	private String content;
	private String sentTime;
}
