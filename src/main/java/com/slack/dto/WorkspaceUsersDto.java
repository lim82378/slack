package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class WorkspaceUsersDto {
	String profileImage;
	String nickname;
	String userId;
	int workSpaceIdx;
	String joinTime;
	

}

