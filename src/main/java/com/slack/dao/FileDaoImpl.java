package com.slack.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slack.dto.FileDmChannelDto;
import com.slack.dto.FileWorkspaceUsersDto;

@Repository
public class FileDaoImpl implements FileDao{
	@Autowired
	SqlSession sqlSession;
	
	/** [SB] 디엠으로 파일목록조회
	 * input : searchKeyword, workspaceIdx, userId
	 *  output : List<FileDmChannelDto>
	 */
	@Override
	public List<FileDmChannelDto> selectListFileByDm(String searchKeyword, int workspaceIdx, String userId) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("searchKeyword", searchKeyword);
		map1.put("workspaceIdx", workspaceIdx);
		map1.put("userId", userId);
		return sqlSession.selectList("FileMapper.selectListFileByDm", map1);
	}
	/** [SB] 채널내용으로 파일목록조회
	 * input : searchkeyword, workspaceIdx, userId
	 * output : List<FileDmChannelDto>
	 */
	@Override
	public List<FileDmChannelDto> selectListFileByChannel(String searchKeyword, int workspaceIdx, String userId) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("searchKeyword", searchKeyword);
		map1.put("workspaceIdx", workspaceIdx);
		map1.put("userId", userId);
		return sqlSession.selectList("FileMapper.selectListFileByChannel", map1);
	}
	/** [SB] 파일삭제
	 * input : fileIdx
	 * output : 
	 */
	@Override
	public void deleteFileName(int fileIdx) {
		sqlSession.delete("FileMapper.deleteFileName", fileIdx);
	}
	/** [SB] 파일권한으로 파일리스트 조회
	 * input : userId
	 * output : List<FileWorkspaceUsersDto>
	 */
	@Override
	public List<FileWorkspaceUsersDto> selectFileListWithUsers(String userId) {
		return sqlSession.selectList("FileMapper.selectFileListByPermission", userId);
	}
	/** [SB] 파일테이블에 추가
	 * input : fileName, workspaceIdx
	 * output : 
	 */
	@Override
	public void insertFiles(String fileName, int workspaceIdx) {
		Map<String, Object>map1 = new HashMap<>();
		map1.put("fileName", fileName);
		map1.put("workspaceIdx", workspaceIdx);
		sqlSession.insert("FileMapper.insertFiles",map1);
	}
	/** [SB] 파일권한테이블에 추가 
	 * input : fileIdx, userId 
	 * output : 
	 */
	@Override
	public void insertFilesPermission(int fileIdx, String userId) {
		Map<String, Object>map1 = new HashMap<>();
		map1.put("fileIdx", fileIdx);
		map1.put("userId", userId);
		sqlSession.insert("FileMapper.insertFilePermission",map1);
	}
	/** [SB] 파일idx 조회 
	 * input : fileName
	 * output : fileIdx	
	 */
	@Override
	public int getFileIdxByName(String fileName) {
		
		return sqlSession.selectOne("FileMapper.selectFileIdxByName",fileName);
	}
}
