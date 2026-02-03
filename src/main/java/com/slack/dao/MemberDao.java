package com.slack.dao;

public interface MemberDao {
	//[MJ] 로그인 성공 여부
	int loginSelect(String userId, String userPw);
	//[MJ] 비밀번호 변경
	void setPw(String userPw, String userId);
	//[MJ] 비밀번호 조회
	String selectPw(String userId);
	//[MJ] 회원가입 
	void createId(String userId, String nickname, String name, String pw, String phoneNumber);
}
