package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {
	private String userId;
	private String name;
	private String nickname;
	private String status;
	private String profileImage;
}
