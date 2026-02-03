package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentionDto {
	private String userId;
	private int dmIdx;
	private String sentTime;
	private String userMentioned;
	private int workspaceIdx;
}
