package com.slack.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slack.dao.ChannelDao;
import com.slack.dao.DirectoryDao;
import com.slack.dto.Channel5Dto;
import com.slack.dto.ChannelIdxNameDto;
import com.slack.dto.Users3Dto;

@Service
public class ChannelServiceImpl implements ChannelService {
	@Autowired
	ChannelDao cDao;
	@Autowired
	DirectoryDao dDao;
	
	/** [MJ] 홈 즐겨찾기 채널 리스트 조회
	 *	input : workspaceIdx, userId
	 *	output : List<ChannelIdxNameDto>
	 */
	@Override
	public List<ChannelIdxNameDto> favoriteChannel(int workspaceIdx, String userId) {
		return cDao.favoriteChannel(workspaceIdx, userId);
	}
	
	/** [MJ] 홈 채널 리스트 조회
	 *	input : workspaceIdx, userId
	 *	output : List<ChannelIdxNameDto>
	 */
	@Override
	public List<ChannelIdxNameDto> myChannelName(int workspaceIdx, String userId) {
		return cDao.myChannelName(workspaceIdx, userId);
	}
	
	/** [MJ] 채널 메세지 목록 조회
	 *	input : channelIdx, workspaceIdx
	 *	output : List<Channel5Dto>
	 */
	@Override
	public List<Channel5Dto> channelMsgSelect(int channelIdx, int workspaceIdx) {
		return cDao.channelMsgSelect(channelIdx,workspaceIdx);
	}
	
	/** [MJ] 채널 메세지 보내기
	 *	input : channelIdx, userId, content, workspaceIdx
	 *	output : -
	 */
	@Override
	public void sendChannelMsg(int channelIdx, String userId, String content, int workspaceIdx) {
		cDao.sendChannelMsg(channelIdx, userId, content, workspaceIdx);
	}
	
	/** [MJ] 홈 즐겨찾기 채널 리스트 조회
	 *	input : channelIdx, userId, content, workspaceIdx
	 *	output : Channel5Dto
	 */
	@Override
	public Channel5Dto channelroomSelect(int channelIdx) {
		return cDao.channelroomSelect(channelIdx);
	}
	
	/** [MJ] 홈 즐겨찾기 채널 리스트 조회
	 *	input : channelIdx, userId, content, workspaceIdx
	 *	output : ProfileImg
	 */
	@Override
	public String userProfileImgSelect(String userId, int workspaceIdx) {
		return cDao.userProfileImgSelect(userId, workspaceIdx);
	}
	
	/** [MJ] 채널 중복체크
	 *	input : channelIdx, userId
	 *	output : channelCount
	 */
	@Override
	public int checkFavorite(int channelIdx, String userId) {
		return cDao.checkFavorite(channelIdx, userId);
	}
	
	/** [MJ] 채널 즐겨찾기 추가
	 *	input : channelIdx, userId
	 *	output : -
	 */
	@Override
	public void favoriteInsert(int channelIdx, String userId) {
		cDao.favoriteInsert(channelIdx, userId);
	}
	
	/** [MJ] 채널 즐겨찾기 제거
	 *	input : channelIdx, userId
	 *	output : -
	 */
	@Override
	public void favoriteDelete(int channelIdx, String userId) {
		cDao.favoriteDelete(channelIdx, userId);
	}
	
	/** [MJ] 채널에서 나가기
	 *	input : channelIdx, userId
	 *	output : -
	 */
	@Override
	public void channelDelete(int channelIdx, String userId) {
		cDao.channelDelete(channelIdx, userId);
		cDao.favoriteDelete(channelIdx, userId);
	}
	
	/** [MJ] 채널 아무도 없는지 조회
	 *	input : channelIdx
	 *	output : channelIdx
	 */
	@Override
	public int ghostChannelSelect(int channelIdx) {
		return cDao.ghostChannelSelect(channelIdx);
	}
	
	/** [MJ] 채널 완전 삭제
	 *	input : channelIdx
	 *	output : -
	 */
	@Override
	public void ghostChannelDelete(int channelIdx) {
		cDao.ghostChannelDelete(channelIdx);
	}
	
	/** [MJ] 채널 생성
	 *	input : channelIdx, channeName, channelManager, topic, explanation
	 *	output : -
	 */
	@Override
	public void createChannel(int workspaceIdx, String channelName, String channelManager, String topic, String explanation) {
		cDao.createChannel(workspaceIdx, channelName, channelManager, topic, explanation);
	}

	/** [MJ] 채널 멤버 조회
	 *	input : channelIdx
	 *	output : List<String>
	 */
	@Override
	public List<String> channelMemberSelect(int channelIdx) {
		return cDao.channelMemberSelect(channelIdx);
	}
	
	/** [MJ]멤버 검색
	 *	input : channelIdx, search
	 *	output : List<String>
	 */
	@Override
	public List<String> searchForMembers(int channelIdx, String search) {
		return cDao.searchForMembers(channelIdx, search);
	}
	
	/** [MJ] 채널 멤버 사용자 추가
	 *	input : channelIdx, userId
	 *	output : -
	 */
	@Override
	public void addChannelMemberUsers(String userId, int channelIdx) {
		cDao.addChannelMemberUsers(userId, channelIdx);
	}
	
	/** [MJ] 채널 멤버 추가를 위한 채널 검색
	 *	input : workspaceIdx
	 *	output : channelIdx
	 */
	@Override
	public int addChannelMemberSelect(int workspaceIdx) {
		return cDao.addChannelMemberSelect(workspaceIdx);
	}
	
	/** [MJ] 채널 매니저 이름 조회
	 *	input : channelIdx
	 *	output : userId
	 */
	@Override
	public String getChannelManagerName(int channelIdx) {
		return cDao.getChannelManagerName(channelIdx);
	}
	
	/** [MJ] 채널 이름 변경
	 *	input : channelName, channelIdx, channelManager
	 *	output : -
	 */
	@Override
	public void channelNewName(String channelName, int channelIdx, String channelManager) {
		cDao.channelNewName(channelName, channelIdx, channelManager);
	}
	
	/** [MJ] 채널 이름 조회
	 *	input : channelIdx
	 *	output : channelName
	 */
	@Override
	public String channelNameSelect(int channelIdx) {
		return cDao.channelNameSelect(channelIdx);
	}
	
	/** [MJ] 채널 주제 편집
	 *	input : newTopic, channelIdx, channelManager
	 *	output : -
	 */
	@Override
	public void channelTopic( String newTopic, int channelIdx, String channelManager) {
		cDao.channelTopic(newTopic, channelIdx,channelManager);
	}
	
	/** [MJ] 채널 주제 조회
	 *	input : channelIdx
	 *	output : topic
	 */
	@Override
	public String channelTopicSelect(int channelIdx) {
		return cDao.channelTopicSelect(channelIdx);
	}
	
	/** [MJ] 채널 설명 편집
	 *	input : explanation, channelIdx, channelManager
	 *	output : -
	 */
	@Override
	public void channelExplanation( String explanation, int channelIdx, String channelManager) {
		cDao.channelExplanation(explanation, channelIdx, channelManager);
	}
	
	/** [MJ] 채널 설명 조회
	 *	input : channelIdx
	 *	output : explanation
	 */
	@Override
	public String channelExplanationSelect(int channelIdx) {
		return cDao.channelExplanationSelect(channelIdx);
	}
	
	/** [MJ] 채널 멤버 닉네임 조회
	 *	input : channelIdx, workspaceIdx
	 *	output : List<Map<String, Object>>
	 */
	@Override
	public List<Map<String, Object>> channelMemberName(int channelIdx, int workspaceIdx) {
		return cDao.channelMemberName(channelIdx, workspaceIdx);
	}
	
	/** [MJ] 채널 멤버 제거
	 *	input : userId, channelIdx
	 *	output : -
	 */
	@Override
	public void removeChannelMember(String userId, int channelIdx) {
		cDao.removeChannelMember(userId, channelIdx);
	}
	
	/** [MJ] 모든 채널 멤버 검색
	 *	input : userId, channelIdx
	 *	output : -
	 */
	@Override
	public void addAllChannelMember(String userId, int channelIdx, int workspaceIdx) {
		List<Users3Dto> list1 = dDao.getWorkspaceMembers(workspaceIdx, userId);
		List<String> list2 = cDao.channelMemberSelect(channelIdx);
		for(Users3Dto dto : list1) {
			String memberId = dto.getUserId();
			if(list2.contains(memberId)) {
				continue;
			}else {
				cDao.addChannelMemberUsers(memberId, channelIdx);
			}
		}
	}
	
	/** [MJ] 홈 즐겨찾기 채널 리스트 조회
	 *	input : channelIdx, workspaceIdx
	 *	output : List<Channel5Dto>
	 */
	@Override
	public List<Channel5Dto> channelMsgSelectAI(int channelIdx, int workspaceIdx) {
		return cDao.channelMsgSelectAI(channelIdx,workspaceIdx);
	}



	

}
