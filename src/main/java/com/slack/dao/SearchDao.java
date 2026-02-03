package com.slack.dao;

import java.util.List;

import com.slack.dto.SearchDmChannelDto;
import com.slack.dto.SearchHistoryDto;

public interface SearchDao {
	
	// [SB]검색내역추가
	int insertSearchHistory(String userId, String searchKeyword, int workSpaceIdx);
	// [SB]검색내역삭제
	void deleteSearchHistory(int searchHistoryIdx);
	// [SB]검색내역조회
	List<SearchHistoryDto>selectSearchHistory(String userId, int workSpaceIdx);
	// [SB]디엠으로 전체검색조회
	List<SearchDmChannelDto>selectSearchAllDm(String dmFrom, String dmTo, int workSpaceIdx, String keyword);
	// [SB]채널로 전체검색조회
	List<SearchDmChannelDto>selectSearchAllChannel(String userId, int workSpaceIdx, String keyword);
}
