package com.slack.dto;

public class FileChannelDto {
	private int fileIdx; // 파일idx 
	private String fileName; // 파일이름 
	private String userId; // 사용자 
	private String sentTime; // 보낸시간 
	
	public FileChannelDto() {}
	// 생성자 
	public FileChannelDto(int fileIdx, String fileName, String userId, String sentTime) {
		super();
		this.fileIdx = fileIdx;
		this.fileName = fileName;
		this.userId = userId;
		this.sentTime = sentTime;
	}
	
	// getter + setter 
	public int getFileIdx() {
		return fileIdx;
	}

	public void setFileIdx(int fileIdx) {
		this.fileIdx = fileIdx;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSentTime() {
		return sentTime;
	}

	public void setSentTime(String sentTime) {
		this.sentTime = sentTime;
	}
	
}

