package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileWorkspaceUsersDto {
	int workspaceIdx;
	int fileIdx;
	String fileName;
	String nickname;
	String uploadTime;
	String profileImage;
}
