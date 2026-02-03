package com.slack.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.slack.dto.WorkspaceDto;
import com.slack.service.WorkspaceService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class WorkspaceServiceTest {
	
	@Autowired
	WorkspaceService wSvc;
	//1. 준비(Given)
	//2. 실행(When)
	//3. 검증(Then)
	
	//워크스페이스 이름 조회
	@Test
	public void testworkspaceNameSelect() throws Exception {
		//1. 준비(Given)
		int workspaceIdx = 1;
		//2. 실행(When)
		wSvc.workspaceNameSelect(workspaceIdx);
		//3. 검증(Then)
	}
	
	/*
	//워크스페이스 구성원 수 조회
	@Test
	public void testWorkspaceUsersCount() throws Exception {
		//1. 준비(Given)
		int workspaceIdx = 1;
		//2. 실행(When)
		wSvc.workspaceUsersCount(workspaceIdx);
		//3. 검증(Then)
	}
	//워크스페이스 구성원 이메일, 이름 검색시
	@Test
	public void workspaceUserSearch() throws Exception {
		int workspaceIdx =1;
		String search = "김";
		wSvc.workspaceUserSearch(workspaceIdx, search);
		System.out.println("검색완료됨");
	}
	//워크스페이스에 초대(조회 및 추가)
	@Test
	public void testUsersSelect() throws Exception {
		String userId = "test1";
		wSvc.usersSelect(userId);
		System.out.println("찾음 :" + userId);
	}
	@Test
	@Transactional
	public void testWorkspaceInsert() throws Exception {
		int workspaceIdx = 1;
		String userId = "test1";
		String nickname = "테스트1";
		String profileImage = "5";
		wSvc.workspaceInsert(workspaceIdx, userId, nickname, profileImage);
		System.out.println("삽입되었다가 롤백됨");
	}
	@Test
	@Transactional
	public void testProfileUpload() throws Exception {
		String profileImage = "강아지1";
		String userId = "test";
		int workspaceIdx = 1;
		wSvc.profileUpload(profileImage, userId, workspaceIdx);
		System.out.println("삽입되었다가 롤백됨");
	}
	@Test
	public void testSelectAllWorkspaceInfo() throws Exception {
		String userId="lim82378@gmail.com";
		List<WorkspaceDto> list =  wSvc.selectAllWorkspaceInfo(userId);
		for(WorkspaceDto dto : list) {
			System.out.println(dto.getWorkspaceName()+dto.getWorkspaceIdx());
		}
	}
	@Test
	public void testgetworkspaceListCheck() throws Exception {
		String userId = "testJJ";
		int count = wSvc.workspaceListCheck(userId);
		System.out.println("워크스페이스 가입 수 : " + count );
	}
	@Test
	public void testWorkspaceUserProfile() throws Exception {
		String userId="test";
		int workspaceIdx = 1;
		String profileName = wSvc.workspaceProfileNameSelect(workspaceIdx, userId);
		System.out.println("프로필 파일 이름 :" + profileName );
	}
	*/
}
