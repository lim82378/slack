package com.slack.service;

import java.util.List;

import com.slack.dto.SearchDmChannelDto;
import com.slack.dto.SearchHistoryDto;

public interface SearchService {
	//[SB] 검색내역추가
	int insertSearchContent(String userId, String searchKeyword, int workSpaceIdx);
	//[SB] 검색내역삭제
	void deleteSearchContent(int searchHistoryIdx);
	//[SB] 검색내역조회 
	List<SearchHistoryDto>selectSearchContent(String userId,int workSpaceIdx);
	//[SB] 전체검색조회 
	List<SearchDmChannelDto>selectAllSearchList(String userId, String dmFrom, String dmto, int workSpaceIdx, String keyword);
}
