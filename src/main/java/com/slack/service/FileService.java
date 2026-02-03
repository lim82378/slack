package com.slack.service;

import java.util.List;

import com.slack.dto.FileChannelDto;
import com.slack.dto.FileDmChannelDto;
import com.slack.dto.FileDmDto;
import com.slack.dto.FileWithFilePermissionDto;
import com.slack.dto.FileWorkspaceUsersDto;

public interface FileService {
	//[SB] 디엠에서 파일목록조회 
	List<FileDmChannelDto> selectFileListByDm(String searchKeyword, int workspaceIdx, String userId);
	//[SB] 채널에서 파일목록조회 
	List<FileDmChannelDto> selectFileListByChannel (String searchKeyword, int workspaceIdx, String userId);
	//[SB] 모든파일목록조회 
	List<FileDmChannelDto> selectAllFileList(String keyword, String userId, int workspaceIdx);
	//[SB] 파일삭제 
	void deleteFileIdx (int fileIdx);
	//[SB] 사용자 파일리스트조회 
	List<FileWorkspaceUsersDto>GetFileListFromUsers(String userId);
	//[SB] 모든파일목록추가 
	void addTotalFilsList(FileWithFilePermissionDto dto, String myId, String otherId);
}
