package com.slack.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slack.dao.FileDao;
import com.slack.dto.FileDmChannelDto;
import com.slack.dto.FileWithFilePermissionDto;
import com.slack.dto.FileWorkspaceUsersDto;

@Service
public class FileServiceImpl implements FileService{
	@Autowired
	FileDao fDao;
	
	/** [SB] 디엠에서 파일목록조회
	 * input : searchKeyword, workspaceIdx, userId
	 *  output : List<FileDmChannelDto>
	 */
	@Override
	public List<FileDmChannelDto> selectFileListByDm(String searchKeyword, int workspaceIdx, String userId) {
		return fDao.selectListFileByDm(searchKeyword, workspaceIdx, userId);
	}
	
	/** [SB] 채널에서 파일목록조회
	 * input : searchkeyword, workspaceIdx, userId
	 * output : List<FileDmChannelDto>
	 */
	@Override
	public List<FileDmChannelDto> selectFileListByChannel(String searchKeyword, int workspaceIdx, String userId) {
		return fDao.selectListFileByChannel(searchKeyword, workspaceIdx, userId);
	}
	@Override       // 위에 조회기능 두개 합친 기능.
	@Transactional
	public List<FileDmChannelDto> selectAllFileList(String keyword, String userId, int workspaceIdx) {
		List<FileDmChannelDto> totalList = new ArrayList<>();
		
		//1. 디엠에서 조회한 리스트를 추가하기 
		List<FileDmChannelDto> listDm = fDao.selectListFileByDm(keyword, workspaceIdx, userId);
		if(listDm != null) {
			totalList.addAll(listDm);
		}
		List<FileDmChannelDto> listChannel = fDao.selectListFileByChannel(keyword, workspaceIdx, userId);
		if(listChannel != null) {
			totalList.addAll(listChannel);
		}
//		totalList.sort((f1, f2) -> f2.getSentTime().compareTo(f1.getSentTime()));
		return totalList;
	}
	/** [SB] 파일삭제
	 * input : fileIdx
	 * output : 
	 */
	@Override
	@Transactional
	public void deleteFileIdx(int fileIdx) {
		fDao.deleteFileName(fileIdx);
	}
	/** [SB] 파일리스트조회
	 * input : userId
	 * output : List<FileWorkspaceUsersDto>
	 */
	@Override
	public List<FileWorkspaceUsersDto> GetFileListFromUsers(String userId) {
		return fDao.selectFileListWithUsers(userId);
	}
	/** [SB] 파일테이블에 추가
	 * input : fileName, workspaceIdx
	 * output : 
	 */
	@Override
	@Transactional
	public void addTotalFilsList(FileWithFilePermissionDto dto, String myId, String otherId) {	
		int fileIdx = dto.getFileIdx();
		
		fDao.insertFilesPermission(fileIdx, myId);
		//내 파일권한 추가 
		
		fDao.insertFilesPermission(fileIdx, otherId);
		//상대방 파일권한 추가 
	}
}
