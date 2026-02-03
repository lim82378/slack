package com.slack.dao;

import java.util.List;

import com.slack.dto.FileDmChannelDto;
import com.slack.dto.FileWorkspaceUsersDto;

public interface FileDao {
	//[SB] 디엠으로 파일목록조회
	List<FileDmChannelDto>selectListFileByDm(String searchKeyword, int workspaceIdx, String userId);
	//[SB] 채널로 파일목록조회 
	List<FileDmChannelDto>selectListFileByChannel(String searchKeyword, int workspaceIdx, String userId);
	//[SB] 파일삭제 
	void deleteFileName(int fileIdx);
	//[SB] 파일목록조회 
	List<FileWorkspaceUsersDto> selectFileListWithUsers(String userId);
	//[SB] 파일테이블에 추가 
	void insertFiles(String fileName, int workspaceIdx);
	//[SB] 파일권한 테이블에 추가 
	void insertFilesPermission(int fileIdx, String userId);
	//[SB] 파일이름으로 파일idx조회 
	int getFileIdxByName(String fileName);
}
