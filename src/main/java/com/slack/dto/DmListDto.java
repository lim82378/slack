package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DmListDto {
	private int dmIdx; // 디엠idx
	private String dmFrom; //디엠보낸사람 
	private String dmTo; // 디엠받는사람 
	private String content; //디엠내용 
	private String revTime; //디엠예약시간 
	private String fileName; // 파일이름 
	private Integer fileIdx;
	private String unread; // 멘션확인여부 
	private String sentTime; // 보낸시간
	private String fromNickname;
	private String toNickname;
	private String dmToId; // 파일 업로드를 위해 필요한 진짜 dmTo 아이디값 
	private int workspaceIdx;
	private String type;
	private String toProfileImage;
	private String fromProfileImage;
	////생성자 
	//public DmListDto(int dmIdx, String dmFrom, String dmTo, String content, String revTime, String fileName, String unread) {
	//	super();
	//	this.dmIdx = dmIdx;
	//	this.dmFrom = dmFrom;
	//	this.dmTo = dmTo;
	//	this.content = content;
	//	this.revTime = revTime;
	//	this.fileName = fileName;
	//	this.unread = unread;
	//}
	//getter + setter 
	//public int getDmIdx() {
	//	return dmIdx;
	//}
	//public void setDmIdx(int dmIdx) {
	//	this.dmIdx = dmIdx;
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
	//public String getContent() {
	//	return content;
	//}
	//public void setContent(String content) {
	//	this.content = content;
	//}
	//public String getRevTime() {
	//	return revTime;
	//}
	//public void setRevTime(String revTime) {
	//	this.revTime = revTime;
	//}
	//public String getFileName() {
	//	return fileName;
	//}
	//public void setFileName(String fileName) {
	//	this.fileName = fileName;
	//}
	//public String getUnread() {
	//	return unread;
	//}
	//public void setUnread(String unread) {
	//	this.unread = unread;
	//}
}

