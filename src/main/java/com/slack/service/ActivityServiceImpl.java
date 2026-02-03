package com.slack.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slack.dao.ActivityDao;
import com.slack.dto.ChannelMsgInviteWorkspaceUsersDto;
import com.slack.dto.DmMentionDto;

@Service
public class ActivityServiceImpl implements ActivityService {
	@Autowired
	ActivityDao aDao;
	/** [SB] 채널초대내역조회
	 * input : userInvited, wokrspaceIdx
	 * output : List<channelMsgInviteWorkspaceUsersDto>
	 */
	@Override
	public List<ChannelMsgInviteWorkspaceUsersDto> selectListOfMyActivityByChannel(String userInvited,
			int workSpaceIdx) {
		return aDao.selectListOfActivityByChannelInvite(userInvited, workSpaceIdx);
	}
	/** [SB] 멘션내역조회 
	 * input : userMentioned, workspaceIdx 
	 * output : List<DmMentionDto>
	 */
	@Override
	public List<DmMentionDto> selectListOfMyActivityByDmMention(String userMentioned, int workSpaceIdx) {
		return aDao.selectListOfActivityByDmMention(userMentioned, workSpaceIdx);
	}
}
