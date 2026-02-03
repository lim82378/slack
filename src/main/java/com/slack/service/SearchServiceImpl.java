package com.slack.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slack.dao.SearchDao;
import com.slack.dto.SearchDmChannelDto;
import com.slack.dto.SearchHistoryDto;

@Service
public class SearchServiceImpl implements SearchService{
	@Autowired
	SearchDao sDao;
	
	/** [SB] 검색내역조회 
	 * input : userId, searchKeyword, workspaceIdx
	 * output : searchHistoryIdx
	 */
	@Override
	@Transactional
	public int insertSearchContent(String userId, String searchKeyword, int workSpaceIdx) {
		return sDao.insertSearchHistory(userId, searchKeyword,workSpaceIdx);
	}
	
	/** [SB] 검색내역삭제
	 * input : searchHistoryIdx
	 * output :
	 */
	@Override
	@Transactional
	public void deleteSearchContent(int searchHistoryIdx) {
		sDao.deleteSearchHistory(searchHistoryIdx);
	}

	/** [SB] 검색내역조회
	 * input : userId, workspaceIdx
	 * output : List<SearchHistoryDto>
	 */
	@Override
	public List<SearchHistoryDto> selectSearchContent(String userId,  int workSpaceIdx) {
		return sDao.selectSearchHistory(userId, workSpaceIdx);
	}

	/** [SB]  전체검색조회 [디엠]
	 * input : dmFrom, dmTo, workspaceIdx, keyword
	 * output : List<SearchDmChannelDto>
	 */
	@Override
	public List<SearchDmChannelDto> selectAllSearchList(String userId, String dmFrom, String dmto, int workSpaceIdx, String keyword) {
		List<SearchDmChannelDto> totalList = new ArrayList<>();
		
		// 디엠
		List<SearchDmChannelDto> dmList = sDao.selectSearchAllDm(dmFrom, dmto, workSpaceIdx, keyword);
		if(dmList != null) {
			totalList.addAll(dmList);
		}
		// 채널
		List<SearchDmChannelDto> channelList = sDao.selectSearchAllChannel(userId, workSpaceIdx, keyword);
		if(channelList != null) {
			totalList.addAll(channelList);
		}
		return totalList;
	}
}
