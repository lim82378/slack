package com.slack.dto;

public class FileDmDto {
	
	String fileName; // 파일테이블의 파일이름 
	int fileIdx; // 파일테이블의 파일idx 
	String dmFrom; // 디엠테이블의 보낸사람 
	String dmTo; // 디엠테이블의 받는사람 
	
	// 생성자 
	public FileDmDto(String fileName, int fileIdx, String dmFrom, String dmTo) {
		super();
		this.fileName = fileName;
		this.fileIdx = fileIdx;
		this.dmFrom = dmFrom;
		this.dmTo = dmTo;
	}
	// getter + setter 
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileIdx() {
		return fileIdx;
	}

	public void setFileIdx(int fileIdx) {
		this.fileIdx = fileIdx;
	}

	public String getDmFrom() {
		return dmFrom;
	}

	public void setDmFrom(String dmFrom) {
		this.dmFrom = dmFrom;
	}

	public String getDmTo() {
		return dmTo;
	}

	public void setDmTo(String dmTo) {
		this.dmTo = dmTo;
	}
}
