package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDmChannelDto {
	private String fileName; // 파일테이블의 파일이름 
	private int fileIdx; // 파일테이블의 파일idx 
	private String dmFrom; // 디엠테이블의 보낸사람 
	private String dmTo;
	private String userId; // 사용자 
	private String sentTime;
	private String nickname;
}
