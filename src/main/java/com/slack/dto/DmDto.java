package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DmDto {
	private int dmIdx; // 디엠idx 
	private String content; // 디엠 내용 
	private String dmFrom; // 디엠 보낸사람 
	private String dmTo; // 디엠 받을사람 
	private String sentTime;
}
