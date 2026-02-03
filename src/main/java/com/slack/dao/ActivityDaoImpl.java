package com.slack.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slack.dto.ChannelMsgInviteWorkspaceUsersDto;
import com.slack.dto.DmMentionDto;

@Repository
public class ActivityDaoImpl implements ActivityDao {
	@Autowired
	SqlSession sqlSession;
	
	/** [SB]채널초대 내역조회
	 * input : userInvited,  workspaceIdx
	 * output : List<ChannelMsgInviteWorkspaceUsersDto>
	 */
	@Override
	public List<ChannelMsgInviteWorkspaceUsersDto> selectListOfActivityByChannelInvite(String userInvited, int workspaceIdx) {
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("userInvited", userInvited);
		map1.put("workspaceIdx", workspaceIdx);
		return sqlSession.selectList("ActivityMapper.selectActivityByChannel", map1);
	}
	
	/** [SB]멘션 내역조회
	 *  input : userMentioned,workspaceIdx
	 *  output : List<DmMentionDto>
	 */
	@Override
	public List<DmMentionDto> selectListOfActivityByDmMention(String userMentioned, int workspaceIdx) {
		HashMap<String, Object> map2 = new HashMap<>();
		map2.put("userMentioned", userMentioned);
		map2.put("workspaceIdx", workspaceIdx);
		return sqlSession.selectList("ActivityMapper.selectActivityListByMention", map2);
	}

	
}
