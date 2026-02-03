package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DmUsersDto {
	private String ProfileImage;
	private String nickname;
	private String name;
	private String userId;
}
