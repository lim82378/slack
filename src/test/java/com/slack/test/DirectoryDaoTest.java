package com.slack.test;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.slack.dao.DirectoryDao;
import com.slack.dto.Users3Dto;
import com.slack.dto.UsersDto;
import com.slack.dto.UsersProfileDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class DirectoryDaoTest {
	@Autowired
	DirectoryDao dDao;
	
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
		List<Users3Dto> listUsers = dDao.getWorkspaceMembers(workspaceIdx, orderByType);
		//3.Then
		assertNotNull("리스트는 null이 아니여야함",listUsers);
		System.out.println(listUsers.size());
		for(Users3Dto dto : listUsers) {
			System.out.println(dto.getNickname()+"/"+dto.getUserId());
		}
	}

	/*
	//[MJ-03]프로필 상세 조회
	@Test
	public void testAllProfileSelect() throws Exception {
		//1.Given
		String userId= "lim82378@gmail.com"; //임시
		int workspaceIdx = 1; //임시
		//2.When
		List<UsersProfileDto> listUsers = dDao.getProfileListWithWorkspaceIdx(workspaceIdx);
		UsersProfileDto list = dDao.getProfileDetail(workspaceIdx, userId);
		String condition = list.getCondition();
		//3.Then
		assertNotNull("리스트는 null이 아니여야함",listUsers);
		System.out.println("현재 내 컨디션 : "+condition);
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
		dDao.updateSetMyCondition(condition, userId);
		//3.Then
	}
	//워크스페이스 사용자 검색
	@Test
	public void testworkspaceUsersSearch() throws Exception {
		String search = "재";
		//dDao.workspaceUsersSearch(search);
	}
	*/
}
