package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileWithFilePermissionDto {
	int fileIdx;
	String fileName;
	int workspaceIdx;
	String uploadTime;
	String userId;
}
