package com.slack.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slack.dto.SearchDmChannelDto;
import com.slack.dto.SearchHistoryDto;

@Repository
public class SearchDaoImpl implements SearchDao{
	@Autowired
	SqlSession sqlSession;
	
	/** [SB] 검색내역조회 
	 * input : userId, searchKeyword, workspaceIdx
	 * output : searchHistoryIdx
	 */
	@Override
	public int insertSearchHistory(String userId, String searchKeyword, int workspaceIdx) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("userId", userId);
		map1.put("searchKeyword", searchKeyword);
		map1.put("workspaceIdx", workspaceIdx);
		sqlSession.insert("SearchMapper.insertNewSearchHistory", map1);
		
		return (Integer)map1.get("searchHistoryIdx");
	}
	/** [SB] 검색내역삭제
	 * input : searchHistoryIdx
	 * output :
	 */
	@Override
	public void deleteSearchHistory(int searchHistoryIdx) {
		sqlSession.delete("SearchMapper.deleteSearchHistory", searchHistoryIdx);
	}
	
	/** [SB] 검색내역조회
	 * input : userId, workspaceIdx
	 * output : List<SearchHistoryDto>
	 */
	@Override
	public List<SearchHistoryDto> selectSearchHistory(String userId, int workspaceIdx) {
		HashMap<String, Object>map2 = new HashMap<>();
		map2.put("userId",userId);
		map2.put("workspaceIdx",workspaceIdx);
		return sqlSession.selectList("SearchMapper.selectSearchHistory", map2);
	}
	
	/** [SB]  전체검색조회 [디엠]
	 * input : dmFrom, dmTo, workspaceIdx, keyword
	 * output : List<SearchDmChannelDto>
	 */
	@Override
	public List<SearchDmChannelDto> selectSearchAllDm(String dmFrom, String dmTo, int workspaceIdx, String keyword) {
		HashMap<String, Object>map2 = new HashMap<>();
		map2.put("dmFrom",dmFrom);
		map2.put("dmTo",dmTo);
		map2.put("workspaceIdx",workspaceIdx);
		map2.put("keyword",keyword);
		return sqlSession.selectList("SearchMapper.selectSearchAllDm", map2);
	}
	
	/** [SB] 전체검색조회 [채널]
	 * input : userId, workspaceIdx, keyword
	 * output : List<SearchDmChannelDto>
	 */
	@Override
	public List<SearchDmChannelDto> selectSearchAllChannel(String userId, int workspaceIdx, String keyword) {
		HashMap<String, Object>map2 = new HashMap<>();
		map2.put("userId",userId);
		map2.put("workspaceIdx",workspaceIdx);
		map2.put("keyword",keyword);
		return sqlSession.selectList("SearchMapper.selectSearchAllChannel", map2);
	}
}
	
