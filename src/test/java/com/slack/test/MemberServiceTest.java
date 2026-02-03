package com.slack.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.slack.service.MemberService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class MemberServiceTest {
	@Autowired
	MemberService mSvc;
	
	//1. 준비(Given)
	//2. 실행(When)
	//3. 검증(Then)
	
	//[JJ-01] 로그인 성공 여부
		@Test
		public void testLoginSelect() throws Exception {
			String userId = "lim82378@gmail.com";
			String userPw = "qwer";
			int count = mSvc.loginSelect(userId, userPw);
			System.out.println("1이면 로그인 성공! 0이면 실패 : "+count);
		}
		
	/*
		//[JJ-04] 비밀번호 변경
		@Test
		@Transactional
		public void testSetPw() throws Exception {
			String userPw = "1234";
			String userId = "lim82378@gmail.com";
			mSvc.setPw(userPw, userId);
			System.out.println(userPw+"으로 변경되었다 롤백");
		}
		//[JJ-04a] 비밀번호 조회
		@Test
		public void testSelectPw() throws Exception {
			String userId = "test";
			String myPw = mSvc.selectPw(userId);
			System.out.println("현재비밀번호는 : " + myPw);
		}
		//[JJ-05] 회원가입
		@Test
		@Transactional
		public void testCreateId() throws Exception {
			String userId = "testJJ";
			String nickname = "JJ";
			String name = "무야호";
			String pw = "123456";
			String phoneNumber="01012345678";
			mSvc.createId(userId, nickname, name, pw, phoneNumber);
			
		}
	*/
}
