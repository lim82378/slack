package com.slack.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDaoImpl implements MemberDao {
	@Autowired
	SqlSession sqlSession;
	
	/** [MJ] 로그인 성공 여부
	 * 	input : userId, userPw
	 * 	output : count
	 */
	@Override
	public int loginSelect(String userId, String userPw) {
		HashMap<String, String> map = new HashMap<>();
		map.put("userId", userId);
		map.put("userPw", userPw);
		return sqlSession.selectOne("MemberMapper.loginSelect",map);
	}
	
	/** [MJ] 비밀번호 변경
	 * 	input : userId, userPw
	 * 	output : -
	 */
	@Override
	public void setPw(String userPw, String userId) {
		HashMap<String, String> map = new HashMap<>();
		map.put("userPw", userPw);
		map.put("userId", userId);
		sqlSession.update("MemberMapper.setPw",map);
	}
	
	/** [MJ] 비밀번호 조회
	 * 	input : userId
	 * 	output : userPw
	 */
	@Override
	public String selectPw(String userId) {
		return sqlSession.selectOne("MemberMapper.selectPw",userId);
	}
	
	/** [MJ] 회원가입
	 * 	input : userId, userPw
	 * 	output : -
	 */
	@Override
	public void createId(String userId, String nickname, String name, String pw, String phoneNumber) {
		Map<String, String> map = new HashMap<>();
		map.put("userId", userId);
		map.put("nickname", nickname);
		map.put("name",name);
		map.put("pw", pw);
		map.put("phoneNumber", phoneNumber);
		sqlSession.insert("MemberMapper.createId",map);
	}

}
