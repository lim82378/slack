package com.slack.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slack.dto.Channel5Dto;
import com.slack.dto.ChannelIdxNameDto;
import com.slack.dto.ChannelViewDto;

@Repository
public class ChannelDaoImpl implements ChannelDao{
	@Autowired
	SqlSession sqlSession;
	
	/** [MJ] 홈 즐겨찾기 채널 리스트 조회
	 *	input : workspaceIdx, userId
	 *	output : List<ChannelIdxNameDto>
	 */
	@Override
	public List<ChannelIdxNameDto> favoriteChannel(int workspaceIdx, String userId) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("workspaceIdx", workspaceIdx);
		map1.put("userId", userId);
		return sqlSession.selectList("ChannelMapper.favoriteChannel",map1);
	}
	
	/** [MJ] 홈 채널 리스트 조회
	 *	input : workspaceIdx, userId
	 *	output : List<ChannelIdxNameDto>
	 */
	@Override
	public List<ChannelIdxNameDto> myChannelName(int workspaceIdx, String userId) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("workspaceIdx", workspaceIdx);
		map1.put("userId", userId);
		return sqlSession.selectList("ChannelMapper.myChannelName",map1);
	}
	
	/** [MJ] 채널 메세지 목록 조회
	 *	input : channelIdx, workspaceIdx
	 *	output : List<Channel5Dto>
	 */
	@Override
	public List<Channel5Dto> channelMsgSelect(int channelIdx, int workspaceIdx) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("channelIdx", channelIdx);
		map.put("workspaceIdx", workspaceIdx);
		return sqlSession.selectList("ChannelMapper.channelMsgSelect",map);
	}
	
	/** [MJ] 채널 메세지 보내기
	 *	input : channelIdx, userId, content, workspaceIdx
	 *	output : -
	 */
	@Override
	public void sendChannelMsg(int channelIdx, String userId, String content, int workspaceIdx) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("channelIdx", channelIdx);
		map.put("userId", userId);
		map.put("content", content);
		map.put("workspaceIdx", workspaceIdx);
		sqlSession.insert("ChannelMapper.sendChannelMsg", map);
		
	}
	
	/** [MJ] 홈 즐겨찾기 채널 리스트 조회
	 *	input : channelIdx, userId, content, workspaceIdx
	 *	output : Channel5Dto
	 */
	@Override
	public Channel5Dto channelroomSelect(int channelIdx) {
		return sqlSession.selectOne("ChannelMapper.channelroomSelect",channelIdx);
	}
	
	/** [MJ] 홈 즐겨찾기 채널 리스트 조회
	 *	input : channelIdx, userId, content, workspaceIdx
	 *	output : ProfileImg
	 */
	@Override
	public String userProfileImgSelect(String userId, int workspaceIdx) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("userId",userId);
		map.put("workspaceIdx", workspaceIdx);
		return sqlSession.selectOne("ChannelMapper.userProfileImgSelect", map);
	}
	
	/** [MJ] 채널 중복체크
	 *	input : channelIdx, userId
	 *	output : channelCount
	 */
	@Override
	public int checkFavorite(int channelIdx, String userId) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("channelIdx",channelIdx);
		map1.put("userId", userId);
		int channelCount = sqlSession.selectOne("ChannelMapper.checkFavorite", map1);
		return channelCount;
	}
	
	/** [MJ] 채널 즐겨찾기 추가
	 *	input : channelIdx, userId
	 *	output : -
	 */
	@Override
	public void favoriteInsert(int channelIdx, String userId) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("channelIdx", channelIdx);
		map1.put("userId", userId);
		sqlSession.insert("ChannelMapper.favoriteInsert",map1);
	}
	
	/** [MJ] 채널 즐겨찾기 제거
	 *	input : channelIdx, userId
	 *	output : -
	 */
	@Override
	public void favoriteDelete(int channelIdx, String userId) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("channelIdx", channelIdx);
		map1.put("userId", userId);
		sqlSession.delete("ChannelMapper.favoriteDelete",map1);
	}
	
	/** [MJ] 채널에서 나가기
	 *	input : channelIdx, userId
	 *	output : -
	 */
	@Override
	public void channelDelete(int channelIdx, String userId) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("channelIdx", channelIdx);
		map1.put("userId", userId);
		sqlSession.delete("ChannelMapper.channelDelete", map1);
	}
	
	/** [MJ] 채널 아무도 없는지 조회
	 *	input : channelIdx
	 *	output : channelIdx
	 */
	@Override
	public int ghostChannelSelect(int channelIdx) {
		return sqlSession.selectOne("ChannelMapper.ghostChannelSelect",channelIdx);
	}
	
	/** [MJ] 채널 완전 삭제
	 *	input : channelIdx
	 *	output : -
	 */
	@Override
	public void ghostChannelDelete(int channelIdx) {
		sqlSession.delete("ChannelMapper.ghostChannelDelete",channelIdx);
	}	
	
	/** [MJ] 채널 생성
	 *	input : channelIdx, channeName, channelManager, topic, explanation
	 *	output : -
	 */
	@Override
	public void createChannel(int workspaceIdx, String channelName, String channelManager, String topic, String explanation) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("workspaceIdx", workspaceIdx);
		map1.put("channelName", channelName);
		map1.put("channelManager", channelManager);
		map1.put("topic", topic);
		map1.put("explanation", explanation);
		sqlSession.insert("ChannelMapper.createChannel", map1);
	}
	
	/** [MJ] 채널 멤버 조회
	 *	input : channelIdx
	 *	output : List<String>
	 */
	@Override
	public List<String> channelMemberSelect(int channelIdx) {
		return sqlSession.selectList("ChannelMapper.channelMemberSelect", channelIdx);
	}
	
	/** [MJ]멤버 검색
	 *	input : channelIdx, search
	 *	output : List<String>
	 */
	@Override
	public List<String> searchForMembers(int channelIdx, String search) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("channelIdx", channelIdx);
		map1.put("search", search);
		return sqlSession.selectList("ChannelMapper.searchForMembers",map1);
	}
	
	/** [MJ] 채널 멤버 사용자 추가
	 *	input : channelIdx, userId
	 *	output : -
	 */
	@Override
	public void addChannelMemberUsers(String userId, int channelIdx) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("userId", userId);
		map1.put("channelIdx", channelIdx);
		sqlSession.insert("ChannelMapper.addChannelMemberUsers",map1);
	}
	
	/** [MJ] 채널 멤버 추가를 위한 채널 검색
	 *	input : workspaceIdx
	 *	output : channelIdx
	 */
	@Override
	public int addChannelMemberSelect(int workspaceIdx) {
		return sqlSession.selectOne("ChannelMapper.addChannelMemberSelect",workspaceIdx);
	}
	
	/** [MJ] 채널 매니저 이름 조회
	 *	input : channelIdx
	 *	output : userId
	 */
	@Override
	public String getChannelManagerName(int channelIdx) {
		return sqlSession.selectOne("ChannelMapper.getChannelManagerName",channelIdx);
	}	
	
	/** [MJ] 채널 이름 변경
	 *	input : channelName, channelIdx, channelManager
	 *	output : -
	 */
	@Override
	public void channelNewName(String channelName, int channelIdx, String channelManager) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("channelName", channelName);
		map1.put("channelIdx", channelIdx);
		map1.put("channelManager", channelManager);
		sqlSession.update("ChannelMapper.channelNewName", map1);
	}
	
	/** [MJ] 채널 이름 조회
	 *	input : channelIdx
	 *	output : channelName
	 */
	@Override
	public String channelNameSelect(int channelIdx) {
		return sqlSession.selectOne("ChannelMapper.channelNameSelect",channelIdx);
	}
	
	/** [MJ] 채널 주제 편집
	 *	input : newTopic, channelIdx, channelManager
	 *	output : -
	 */
	@Override
	public void channelTopic(String newTopic, int channelIdx, String channelManager) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("newTopic", newTopic);
		map1.put("channelIdx", channelIdx);
		map1.put("channelManager", channelManager);
		sqlSession.update("ChannelMapper.channelTopic", map1);
	}
	
	/** [MJ] 채널 주제 조회
	 *	input : channelIdx
	 *	output : topic
	 */
	@Override
	public String channelTopicSelect(int channelIdx) {
		return sqlSession.selectOne("ChannelMapper.channelTopicSelect",channelIdx);
	}
	
	/** [MJ] 채널 설명 편집
	 *	input : explanation, channelIdx, channelManager
	 *	output : -
	 */
	@Override
	public void channelExplanation(String explanation, int channelIdx, String channelManager) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("explanation", explanation);
		map1.put("channelIdx", channelIdx);
		map1.put("channelManager", channelManager);
		sqlSession.update("ChannelMapper.channelExplanation", map1);
	}
	
	/** [MJ] 채널 멤버 닉네임 조회
	 *	input : channelIdx, workspaceIdx
	 *	output : List<Map<String, Object>>
	 */
	@Override
	public List<Map<String, Object>> channelMemberName(int channelIdx, int workspaceIdx) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("channelIdx", channelIdx);
		map1.put("workspaceIdx", workspaceIdx);
		return sqlSession.selectList("ChannelMapper.channelMemberName",map1);
	}
	
	/** [MJ] 채널 설명 조회
	 *	input : channelIdx
	 *	output : explanation
	 */
	@Override
	public String channelExplanationSelect(int channelIdx) {
		return sqlSession.selectOne("ChannelMapper.channelExplanationSelect",channelIdx);
	}
	
	/** [MJ] 홈 즐겨찾기 채널 리스트 조회
	 *	input : channelIdx, workspaceIdx
	 *	output : List<Channel5Dto>
	 */
	@Override
	public List<Channel5Dto> channelMsgSelectAI(int channelIdx, int workspaceIdx) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("channelIdx",channelIdx);
		map1.put("workspaceIdx", workspaceIdx);
		return sqlSession.selectList("ChannelMapper.channelMsgSelectAI",map1);
	}
	
	/** [MJ] 채널 멤버 제거
	 *	input : userId, channelIdx
	 *	output : -
	 */
	@Override
	public void removeChannelMember(String userId, int channelIdx) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("userId",userId);
		map.put("channelIdx", channelIdx);
		sqlSession.delete("ChannelMapper.removeChannelMember", map);
	}
}
