package com.slack.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.slack.dto.Channel5Dto;
import com.slack.dto.ChannelIdxNameDto;
import com.slack.dto.ChannelViewDto;

public interface ChannelDao {
	//[MJ] 홈 즐겨찾기 채널 리스트 조회
	List<ChannelIdxNameDto> favoriteChannel(int workspaceIdx, String userId);
	//[MJ] 홈 채널 리스트 조회
	List<ChannelIdxNameDto> myChannelName(int workspaceIdx, String userId);
	//[MJ] 채널 메세지 목록 조회
	List<Channel5Dto> channelMsgSelect(int channelIdx, int workspaceIdx);
	//[MJ] 채널 메세지 보내기
	void sendChannelMsg(int channelIdx, String userId, String content, int workspaceIdx);
	//[MJ] 채널 등등 조회
	Channel5Dto channelroomSelect(int channelIdx);
	//[MJ] 프로필 사진 조회
	String userProfileImgSelect(String userId, int workspaceIdx);
	//[MJ] 채널 중복체크
	int checkFavorite(int channelIdx, String userId);
	//[MJ] 채널 즐겨찾기 추가
	void favoriteInsert(int channelIdx, String userId);
	//[MJ] 채널 즐겨찾기 제거
	void favoriteDelete(int channelIdx, String userId);
	//[MJ] 채널에서 나가기
	void channelDelete(int channelIdx, String userId);
	//[MJ] 채널 아무도 없는지 조회
	int ghostChannelSelect(int channelIdx);
	//[MJ] 채널 완전 삭제
	void ghostChannelDelete(int channelIdx);
	//[MJ] 채널 생성
	void createChannel(int workspaceIdx, String channelName, String channelManager, String topic, String explanation);
	//[MJ] 채널 멤버 조회
	List<String> channelMemberSelect(int channelIdx);
	//[MJ] 멤버 검색
	List<String> searchForMembers(int channelIdx, String search);
	//[MJ] 채널 멤버 사용자 추가
	void addChannelMemberUsers(String userId, int channelIdx);
	//[MJ] 채널 멤버 추가를 위한 채널 검색
	int addChannelMemberSelect(int workspaceIdx);
	//[MJ] 채널 매니저 이름 조회
	String getChannelManagerName(int channelIdx);
	//[MJ] 채널 이름 변경
	void channelNewName(@Param("channelName") String channelName, @Param("channelIdx") int channelIdx, @Param("channelManager") String channelManager);
	//[MJ] 채널 이름 조회
	String channelNameSelect(int channelIdx);
	//[MJ] 채널 주제 편집
	void channelTopic( String newTopic, int channelIdx, String channelManager);
	//[MJ] 채널 주제 조회
	String channelTopicSelect(int channelIdx);
	//[MJ] 채널 설명 편집
	void channelExplanation(String explanation, int channelIdx, String channelManager);
	//[MJ] 채널 설명 조회
	String channelExplanationSelect(int channelIdx);
	//[MJ] 채널 멤버 닉네임 조회
	List<Map<String, Object>> channelMemberName(int channelIdx, int workspaceIdx);
	//[MJ] 채팅내역조회(AI)
	List<Channel5Dto> channelMsgSelectAI(int channelIdx, int workspaceIdx);
	//[MJ] 채널 멤버 제거
	void removeChannelMember(String userId, int channelIdx);
}
