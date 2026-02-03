package com.slack.test;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.slack.dto.Users3Dto;
import com.slack.dto.UsersProfileDto;
import com.slack.service.DirectoryService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class DirectoryServiceTest {
	@Autowired
	DirectoryService dSvc;
	
	
	//1. 준비(Given)
	//2. 실행(When)
	//3. 검증(Then)
	//[MJ-00a] 워크스페이스 구성원 조회
	@Test
	public void testGetWorkspaceMembers() throws Exception {
		//1.Given
		int workspaceIdx = 1;
		String orderByType = "참여순";
		//2.When
		List<Users3Dto> listUsers = dSvc.getWorkspaceMembers(workspaceIdx, orderByType);
		//3.Then
		assertNotNull("리스트는 null이 아니여야합니다.",listUsers);
		for(Users3Dto dto : listUsers) {
			System.out.println(dto.getNickname()+"/"+dto.getUserId());
		}
	}
	
	/*
	//[MJ-03]프로필 상세 조회
	@Test
	public void testAllProfileSelect() throws Exception {
		//1.Given
		String userId= "lim82378@gmail.com";
		int workspaceIdx = 1;
		//2.When
		List<UsersProfileDto> listUsers = dSvc.getProfileListWithWorkspaceIdx(workspaceIdx);
		//3.Then
		assertNotNull("리스트는 null이 아니여야함",listUsers);
		for(UsersProfileDto dto : listUsers) {
			System.out.println(dto.getNickname()+ "/"+dto.getName()+"/"+dto.getUserId());
		}		
	}
	//[MJ-06]내 프로필 상태설정
	@Test
	public void testupdateSetMyCondition() throws Exception {
		//1.Given
		String condition = "출근 중";
		String userId = "lim82378@gmail.com";
		//2.When
		dSvc.getUpdateSetMyCondition(userId, condition);;
		//3.Then
	}	
	//워크스페이스 사용자 검색
	@Test
	public void testworkspaceUsersSearch() throws Exception {
		String search = "재";
		//dSvc.workspaceUsersSearch(search);
	}
	@Test
	@Transactional
	public void testUpdateMyProfile() throws Exception {
		String userId = "test";
		String nickname="별명바뀜";
		String name = "이름변경";
		String title = "직함바뀜";
		int workspaceIdx = 1;
		dSvc.setMyProfileNameAndTitle(name, title, userId);
		dSvc.setMyProfileNickname(nickname, userId, workspaceIdx);
		System.out.println("업데이트되었다가 롤백됨");
	}
	*/
}