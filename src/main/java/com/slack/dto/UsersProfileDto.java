package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersProfileDto {
	private String userId;
	private String pw;
	private String nickname;
	private String name;
	private String phoneNumber;
	private String condition;
	private String status;
	private String resetPwCode;
	private String title;
	private String department;
}
