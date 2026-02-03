package com.slack.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.slack.dao.ChannelDao;
import com.slack.dto.Channel5Dto;
import com.slack.dto.ChannelIdxNameDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class ChannelDaoTest {
	@Autowired
	ChannelDao cDao;
	//1. 준비(Given)
	//2. 실행(When)
	//3. 검증(Then)
	
	//[MJ-00b]홈 즐겨찾기 채널 리스트 조회
	@Test
	public void testFavoriteChannel() throws Exception {
		//1.Given
		int workspaceIdx = 1;
		String userId = "lim82378@gmail.com";
		//2.When
		List<ChannelIdxNameDto> listChannelIdx = cDao.favoriteChannel(workspaceIdx, userId);
		//3.Then
		assertNotNull("리스트는 null이 아니여야함", listChannelIdx);
		
		System.out.println("읽어온 레코드 개수 : " + listChannelIdx.size());
		for(ChannelIdxNameDto dto : listChannelIdx) {
			System.out.println(dto.getChannelName());
		}
	}
	
	/*
	//[MJ-00c] 홈 채널 리스트 조회
	@Test
	public void testMyChannelName() throws Exception {
		//1.Given
		int workspaceIdx = 1;
		String userId = "lim82378@gmail.com";
		//2.When
		List<ChannelIdxNameDto> listChannelIdx = cDao.myChannelName(workspaceIdx, userId);
		//3.Then
		assertNotNull("리스트는 null이 아니여야함", listChannelIdx);
		
		System.out.println("읽어온 레코드 개수 : " + listChannelIdx.size());
		for(ChannelIdxNameDto dto : listChannelIdx) {
			System.out.println(dto.getChannelName());
		}
	}
	
	//[MJ-09] 채널 메세지 목록 조회
	@Test
	public void testChannelMsgSelect() throws Exception {
		//1.Given
		int channelIdx = 1;
		int workspaceIdx= 1;
		//2.When
		List<Channel5Dto> listChannelIdx = cDao.channelMsgSelect(channelIdx,workspaceIdx);
		//3.Then
		assertNotNull(listChannelIdx);
		System.out.println("읽어온 레코드 개수 : " + listChannelIdx.size());
		for(Channel5Dto dto : listChannelIdx) {
			System.out.println(dto.getContent());
		}
	}
	
	//[MJ-11a]채널 중복체크
	@Test
	public void testcheckFavorite() throws Exception {
		int channelIdx = 1;
		String userId = "lim82378@gmail.com";
		int num = cDao.checkFavorite(channelIdx, userId);
		System.out.println(num);
	}
	//[MJ-11] 채널 즐겨찾기 추가
	@Test
	public void testFavoriteInsert() throws Exception {
		//1. 준비(Given)
		String userId = "lim82378@gmail.com";
		int channelIdx = 1;
		//2. 실행(When)
		cDao.favoriteInsert(channelIdx, userId);
		//3. 검증(Then)
	}
	
	//[MJ-12] 채널 즐겨찾기 제거
	@Test
	@Transactional
	public void favoriteDelete() throws Exception {
		//1. 준비(Given)
		String userId = "lim82378@gmail.com";
		int channelIdx = 1;
		//2. 실행(When)
		cDao.favoriteDelete(channelIdx, userId);
		//3. 검증(Then)
	}
	//[MJ-14]채널에서 나가기
	@Test
	@Transactional
	public void testchannelDelete() throws Exception {
		//1. 준비(Given)
		int channelIdx = 7;
		String userId = "lim82378@gmail.com";
		//2. 실행(When)
		cDao.channelDelete(channelIdx, userId);
		//3. 검증(Then)
		assertNotNull(channelIdx);
		System.out.println(channelIdx+"삭제"+userId+"의 채널");
	}
	//[MJ-14b]채널 아무도 없는지 조회
	@Test
	public void testGhostChannelSelect() throws Exception {
		int channelIdx=21;
		cDao.ghostChannelSelect(channelIdx);
		System.out.println(channelIdx+"검색완료");
	}
	//[MJ-14c]채널 완전 삭제
	@Test
	@Transactional
	public void testGhostChannelDelete() throws Exception {
		int channelIdx=21;
		cDao.ghostChannelDelete(channelIdx);
		System.out.println(channelIdx+"삭제하기");
	}
	//[MJ-14a] 채널 생성
	@Test
	@Transactional
	public void testcreateChannel() throws Exception {
		//1. 준비(Given)
		int workspaceIdx = 1;
		String channelName = "test중인 채널이름";
		String channelManager = "test"; 
		String topic = "테스트중인 주제";
		String explanation = "테스트중인 공지";
		//2. 실행(When)
		cDao.createChannel(workspaceIdx, channelName, channelManager, topic, explanation);
		//3. 검증(Then)
		assertNotNull(workspaceIdx);
		System.out.println(workspaceIdx+"/"+channelName+"/"+channelManager+"/"+topic+"/"+explanation+"추가");
	}
	//[MJ-19]채널 멤버 조회
	@Test
	public void testChannelMemberSelect() throws Exception {
		//1. 준비(Given)
		int channelIdx = 1;
		//2. 실행(When)
		cDao.channelMemberSelect(channelIdx);
		//3. 검증(Then)
		System.out.println("조회완료");
	}
	//[MJ-20]멤버 검색
	@Test
	public void testSearchForMembers() throws Exception {
		//1. 준비(Given)
		int channelIdx = 1;
		String search = "재";
		//2. 실행(When)
		cDao.searchForMembers(channelIdx, search);
		//3. 검증(Then)
		System.out.println(search+"의 내용 검색 완료");
	}
	//[MJ-21]채널 멤버 사용자 추가
	@Test
	@Transactional
	public void testAddChannelMemberUsers() throws Exception {
		//1. 준비(Given)
		int channelIdx = 7;
		String userId = "seungbin4369@gmail.com";
		//2. 실행(When)
		cDao.addChannelMemberUsers(userId, channelIdx);
		//3. 검증(Then)
		System.out.println(userId+"추가완료했다 롤백");
	}
	//[MJ-21a]채널 멤버 추가를 위한 채널 검색
	@Test
	public void testAddChannelMemberSelect() throws Exception {
		//1. 준비(Given)
		int workspaceIdx = 1;
		//2. 실행(When)
		cDao.addChannelMemberSelect(workspaceIdx);
	}
	//[MJ-15a] 채널 매니저 이름 조회
	@Test
	public void testGetChannelManagerName() throws Exception {
		int channelIdx = 1;
		String manager = cDao.getChannelManagerName(channelIdx);
		System.out.println(manager+"조회 완료");
	}
	//[MJ-16]채널 이름 변경
	@Test
	@Transactional
	public void testChannelNewName() throws Exception {
		int channelIdx = 1;
		String channelName = "주제1";
		String channelManager = "seungbin4369@gmail.com";
		cDao.channelNewName(channelName,channelIdx,channelManager);
		System.out.println(channelName+" 으로 변경되었다 롤백");
	}
	//[MJ-16a]채널 이름 조회
	@Test
	public void testChannelNameSelect() throws Exception {
		int channelIdx = 1;
		String name = cDao.channelNameSelect(channelIdx);
		System.out.println(name+"/ 검색완료");
	}
	//[MJ-17]채널 주제 편집
	@Test
	@Transactional
	public void testChannelTopic() throws Exception {
		int channelIdx = 1;
		String newTopic = "주제1";
		String channelManager = "seungbin4369@gmail.com";
		cDao.channelTopic(newTopic, channelIdx, channelManager);
		System.out.println(newTopic+" 으로 변경되었다 롤백");
	}
	//[MJ-17a]채널 주제 조회
	@Test
	public void TestChannelTopicSelect() throws Exception {
		int channelIdx = 1;
		String name = cDao.channelTopicSelect(channelIdx);
		System.out.println(name+"/ 검색완료");
	}
	//[MJ-18]채널 설명 편집
	@Test
	@Transactional
	public void testChannelExplanation() throws Exception {
		int channelIdx = 1;
		String explanation = "주제1";
		String channelManager = "seungbin4369@gmail.com";
		cDao.channelExplanation(explanation, channelIdx,channelManager);
		System.out.println(explanation+" 으로 변경되었다 롤백");
	}
	//[MJ-18a]채널 설명 조회
	@Test
	public void testChannelExplanationSelect() throws Exception {
		int channelIdx = 1;
		String name = cDao.channelExplanationSelect(channelIdx);
		System.out.println(name+"/ 검색완료");
	}
	//[MJ-???]채널 멤버 닉네임 조회
	@Test
	public void testChannelMemberName() throws Exception {
		int channelIdx=1;
		int workspaceIdx=1;
		List<Map<String, Object>> name = cDao.channelMemberName(channelIdx, workspaceIdx);
		System.out.println("닉네임 개수 : " + name.size());
		for(Map<String, Object> list : name) {
			String nickname = (String)list.get("nickname");
			String profile = (String)list.get("profileImage");
			System.out.println(nickname+"/"+profile);
		}
	}
	//[MJ-23]채널 멤버 제거
	@Test
	@Transactional
	public void testRemoveChannelMember() throws Exception {
		String userId = "junkira95@gamil.com";
		int channelIdx = 2;
		cDao.removeChannelMember(userId, channelIdx);
		System.out.println("실행완료후 롤백됨");
	}
	*/
}
