package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DmContentDto {
	private int dmIdx;
	private String content;
	private String dmFrom;
	private String dmTo;
	private String profileImage;
	private String nickname;
	

//public DmContentDto() {}
//public DmContentDto(int dmIdx, String content, String dmFrom, String dmTo) {
//	this.dmIdx = dmIdx;
//	this.content = content;
//	this.dmFrom = dmFrom;
//	this.dmTo = dmTo;
//}
//public int getDmIdx() {
//	return dmIdx;
//}
//public void setDmIdx(int dmIdx) {
//	this.dmIdx = dmIdx;
//}
//public String getContent() {
//	return content;
//}
//public void setContent(String content) {
//	this.content = content;
//}
//public String getDmFrom() {
//	return dmFrom;
//}
//public void setDmFrom(String dmFrom) {
//	this.dmFrom = dmFrom;
//}
//public String getDmTo() {
//	return dmTo;
//}
//public void setDmTo(String dmTo) {
//	this.dmTo = dmTo;
//}

}

