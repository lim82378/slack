package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users3Dto {
	private String userId; //유저아이디
	private String nickname; //닉네임
	private String profileImage; //프로필 이미지
}
