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

import com.slack.dto.Channel5Dto;
import com.slack.dto.ChannelIdxNameDto;
import com.slack.service.ChannelService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class ChannelServiceTest {
	@Autowired
	ChannelService cSvc;
	//1. 준비(Given)
	//2. 실행(When)
	//3. 검증(Then)
	//[MJ-00b]홈 즐겨찾기 채널 리스트 조회
	@Test
	public void testFavoriteChannel() throws Exception {
		//1.Given
		int workspaceIdx = 1; //임시
		String userId = "lim82378@gmail.com"; //임시
		//2.When
		List<ChannelIdxNameDto> listChannelIdx = cSvc.favoriteChannel(workspaceIdx, userId);
		//3.Then
		assertNotNull("리스트는 null이 아니여야함", listChannelIdx);
		
		System.out.println("읽어온 레코드 개수 : " + listChannelIdx.size());
		for(ChannelIdxNameDto dto : listChannelIdx) {
			System.out.println(dto.getChannelName());
		}
	}
	
	/*
	//[MJ-00c]홈 채널 리스트 조회
	@Test
	public void testMyChannelName() throws Exception {
		//1.Given
		int workspaceIdx = 1; //임시
		String userId = "lim82378@gmail.com"; //임시
		//2.When
		List<ChannelIdxNameDto> listChannelIdx = cSvc.myChannelName(workspaceIdx, userId);
		//3.Then
		assertNotNull("리스트는 null이 아니여야함", listChannelIdx);
		
		System.out.println("읽어온 레코드 개수 : " + listChannelIdx.size());
		for(ChannelIdxNameDto dto : listChannelIdx) {
			System.out.println(dto.getChannelName());
		}
	}
	//[MJ-09]채널 메세지 목록 조회
	@Test
	public void testchannellist() throws Exception {
		int channelIdx = 1;
		int workspaceIdx = 1;
		List<Channel5Dto> list = cSvc.channelMsgSelect(channelIdx, workspaceIdx);
		System.out.println("읽어온 레코드 개수 : "+ list.size());
		for(Channel5Dto dto : list) {
			System.out.println(dto.getChannelName()+dto.getContent()+dto.getSentTime()+dto.getUserId()+dto.getProfileImage());
		}
	}
	//[MJ-09a] 채널 메세지 보내기
	@Test
	public void testSendChannelMsg() throws Exception {
		int channelIdx = 1;
		int workspaceidx = 1;
		String userId = "test";
		String content = "테스트 중인 메세지 보내버리기~";
		cSvc.sendChannelMsg(channelIdx, userId, content, workspaceidx);
		System.out.println("삽입 되었습니다.");
	}
	//[MJ-09b]채널 등등 조회
	@Test
	public void testchannelroom() throws Exception {
		int channelIdx = 1;
		cSvc.channelroomSelect(channelIdx);
		System.out.println("검색완료");
	}
	@Test
	public void testUserProfile() throws Exception {
		String userId = "test";
		int workspaceIdx = 1;
		String profileImg = cSvc.userProfileImgSelect(userId,workspaceIdx);
		System.out.println(profileImg);
	}
	
	@Test
	public void testcheckFavorite() throws Exception {
		int channelIdx = 1;
		String userId = "lim82378@gmail.com";
		cSvc.checkFavorite(channelIdx, userId);
	}	
	//[MJ-11] 채널 즐겨찾기 추가
	@Test
	public void testFavoriteInsert() throws Exception {
		//1. 준비(Given)
		String userId = "lim82378@gmail.com";
		int channelIdx = 1;
		//2. 실행(When)
		cSvc.favoriteInsert(channelIdx, userId);
		//3. 검증(Then)
	}
	//[MJ-12] 채널 즐겨찾기 제거
	@Test
	public void favoriteDelete() throws Exception {
		//1. 준비(Given)
		String userId = "lim82378@gmail.com";
		int channelIdx = 1;
		//2. 실행(When)
		cSvc.favoriteDelete(channelIdx, userId);
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
		cSvc.channelDelete(channelIdx, userId);
		//3. 검증(Then)
		assertNotNull(channelIdx);
		System.out.println(channelIdx+"삭제"+userId+"의 채널");
	}
	//[MJ-14b]채널 아무도 없는지 조회
		@Test
		public void testGhostChannelSelect() throws Exception {
			int channelIdx=21;
			cSvc.ghostChannelSelect(channelIdx);
			System.out.println(channelIdx+"검색완료");
		}
		//[MJ-14c]채널 완전 삭제
		@Test
		@Transactional
		public void testGhostChannelDelete() throws Exception {
			int channelIdx=21;
			cSvc.ghostChannelDelete(channelIdx);
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
		cSvc.createChannel(workspaceIdx, channelName, channelManager, topic, explanation);
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
		cSvc.channelMemberSelect(channelIdx);
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
		cSvc.searchForMembers(channelIdx, search);
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
		cSvc.addChannelMemberUsers(userId, channelIdx);
		//3. 검증(Then)
		System.out.println(userId+"추가완료했다 롤백");
	}
	//채널 생성과 동시에 채널 만든 사용자 추가
	@Test
	@Transactional
	public void testChannelcreateOrInsertMember() throws Exception {
		//1. 준비(Given)
		int workspaceIdx = 1;
		String channelName = "test중인 채널이름";
		String channelManager = "test"; 
		String topic = "테스트중인 주제";
		String explanation = "테스트중인 공지";
		String userId = "lim82378@gmail.com";
		int channelIdx = cSvc.addChannelMemberSelect(workspaceIdx);
		//2. 실행(When)
		cSvc.createChannel(workspaceIdx, channelName, channelManager, topic, explanation);
		cSvc.addChannelMemberUsers(userId, channelIdx);
		
		//3. 검증(Then)
		assertNotNull(workspaceIdx);
		System.out.println(workspaceIdx+"/"+channelName+"/"+channelManager+"/"+topic+"/"+explanation+"추가");
		System.out.println("새 채널의 idx값:"+channelIdx+"채널 만든이: "+userId);
	}
	//[MJ-15a] 채널 매니저 이름 조회
	@Test
	public void testGetChannelManagerName() throws Exception {
		int channelIdx = 1;
		String manager = cSvc.getChannelManagerName(channelIdx);
		System.out.println(manager+"조회 완료");
	}
	//[MJ-16]채널 이름 변경
		@Test
		@Transactional
		public void testChannelNewName() throws Exception {
			int channelIdx = 1;
			String channelName = "주제1";
			String channelManager = "seungbin4369@gmail.com";
			cSvc.channelNewName(channelName, channelIdx, channelManager);
			System.out.println(channelName+" 으로 변경되었다 롤백");
		}
		//[MJ-16a]채널 이름 조회
		@Test
		public void testChannelNameSelect() throws Exception {
			int channelIdx = 1;
			String name = cSvc.channelNameSelect(channelIdx);
			System.out.println(name+"/ 검색완료");
		}
		//[MJ-17]채널 주제 편집
		@Test
		@Transactional
		public void testChannelTopic() throws Exception {
			int channelIdx = 1;
			String newTopic = "주제1";
			String channelManager = "seungbin4369@gmail.com";
			cSvc.channelTopic(newTopic, channelIdx, channelManager);
			System.out.println(newTopic+" 으로 변경되었다 롤백");
		}
		//[MJ-17a]채널 주제 조회
		@Test
		public void TestChannelTopicSelect() throws Exception {
			int channelIdx = 1;
			String name = cSvc.channelTopicSelect(channelIdx);
			System.out.println(name+"/ 검색완료");
		}
		//[MJ-18]채널 설명 편집
		@Test
		@Transactional
		public void testChannelExplanation() throws Exception {
			int channelIdx = 1;
			String explanation = "주제1";
			String channelManager = "seungbin4369@gmail.com";
			cSvc.channelExplanation(explanation, channelIdx, channelManager);
			System.out.println(explanation+" 으로 변경되었다 롤백");
		}
		//[MJ-18a]채널 설명 조회
		@Test
		public void testChannelExplanationSelect() throws Exception {
			int channelIdx = 1;
			String name = cSvc.channelExplanationSelect(channelIdx);
			System.out.println(name+"/ 검색완료");
		}
		//[MJ-???]채널 멤버 닉네임 조회
		@Test
		public void testchannelMemberName() throws Exception {
			int channelIdx=1;
			int workspaceIdx=1;
			List<Map<String, Object>> name = cSvc.channelMemberName(channelIdx, workspaceIdx);
			System.out.println("닉네임 개수 : " + name.size());
			for(Map<String, Object> list : name) {
				String nickname = (String)list.get("NICKNAME");
				String profile = (String)list.get("PROFILEIMAGE");
				System.out.println(nickname+"/"+profile);
				assertNotNull(nickname);
			}
		}
		//[MJ-23]채널 멤버 제거
		@Test
		@Transactional
		public void testRemoveChannelMember() throws Exception {
			String userId = "junkira95@gamil.com";
			int channelIdx = 2;
			cSvc.removeChannelMember(userId, channelIdx);
			System.out.println("실행완료후 롤백됨");
		}
		//[MJ-??]채널 전체 멤버 추가
		@Test
		public void testAddAllChannelMember() throws Exception {
			String userId = "";
			int channelIDx = 2;
			int workspaceIdx = 1;
			cSvc.addAllChannelMember(userId, channelIDx, workspaceIdx);
			System.out.println("");
		}
		@Test
		public void testChannelMsgSelectAI() throws Exception {
			int channelIdx = 1;
			int workspaceIdx = 1;
			List<Channel5Dto> dto = cSvc.channelMsgSelectAI(channelIdx,workspaceIdx);
			for(Channel5Dto list : dto) {
				System.out.println(list.getNickname()+"/"+list.getContent());
			}
		}
	*/
}
