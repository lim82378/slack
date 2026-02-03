package com.slack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slack.dao.MemberDao;

@Service
public class MemberServiceImpl implements MemberService {
	@Autowired
	MemberDao mDao;
	
	/** [MJ] 로그인 성공 여부
	 * 	input : userId, userPw
	 * 	output : count
	 */
	@Override
	public int loginSelect(String userId, String userPw) {
		return mDao.loginSelect(userId, userPw);
	}
	
	/** [MJ] 비밀번호 변경
	 * 	input : userId, userPw
	 * 	output : -
	 */
	@Override
	public void setPw(String userPw, String userId) {
		mDao.setPw(userPw, userId);
	}
	
	/** [MJ] 비밀번호 조회
	 * 	input : userId
	 * 	output : userPw
	 */
	@Override
	public String selectPw(String userId) {
		return mDao.selectPw(userId);
	}
	
	/** [MJ] 회원가입
	 * 	input : userId, userPw
	 * 	output : -
	 */
	@Override
	public void createId(String userId, String nickname, String name, String pw, String phoneNumber) {
		mDao.createId(userId, nickname, name, pw, phoneNumber);
	}

}
