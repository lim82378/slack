package com.slack.test;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.slack.dao.WorkspaceDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class WorkspaceDaoTest {
	@Autowired
	WorkspaceDao wDao;
	//1. 준비(Given)
	//2. 실행(When)
	//3. 검증(Then)
	//워크스페이스 이름 조회
	@Test
	public void testworkspaceNameSelect() throws Exception {
		//1. 준비(Given)
		int workspaceIdx = 3;
		//2. 실행(When)
		String workspaceName = wDao.workspaceNameSelect(workspaceIdx);
		//3. 검증(Then)
		System.out.println(workspaceName);
	}
	
	/*
	//워크스페이스 구성원 수 조회
	@Test
	public void testWorkspaceUsersCount() throws Exception {
		//1. 준비(Given)
		int workspaceIdx = 1;
		//2. 실행(When)
		wDao.workspaceUsersCount(workspaceIdx);
		//3. 검증(Then)
	}
	//워크스페이스 구성원 이메일, 이름 검색시
	@Test
	public void testWorkspaceUserSearch() throws Exception {
		int workspaceIdx = 1;
		String search = "김";
		List<Map<String, Object>> list1 = wDao.workspaceUserSearch(workspaceIdx, search);
		System.out.println(list1.size());
		System.out.println("검색완료됨");
	}
	//워크스페이스에 초대(조회 및 추가)
	@Test
	public void testUsersSelect() throws Exception {
		String userId = "test1";
		wDao.usersSelect(userId);
		System.out.println("찾음 :" + userId);
	}
	@Test
	@Transactional
	public void testWorkspaceInsert() throws Exception {
		int workspaceIdx = 1;
		String userId = "test1";
		String nickname = "테스트1";
		String profileImage = "5";
		wDao.workspaceInsert(workspaceIdx, userId, nickname, profileImage);
		System.out.println("삽입되었다가 롤백됨");
	}
	*/
}
