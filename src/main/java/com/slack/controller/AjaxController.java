package com.slack.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.slack.dao.FileDao;
import com.slack.dto.Channel5Dto;
import com.slack.dto.DmContentDto;
import com.slack.dto.FileDmChannelDto;
import com.slack.dto.SearchDmChannelDto;
import com.slack.dto.SearchHistoryDto;
import com.slack.dto.Users3Dto;
import com.slack.dto.UsersDto;
import com.slack.dto.UsersProfileDto;
import com.slack.dto.WorkspaceUsersMentionDto;
import com.slack.service.ChannelService;
import com.slack.service.DirectoryService;
import com.slack.service.DmService;
import com.slack.service.FileService;
import com.slack.service.MemberService;
import com.slack.service.SearchService;
import com.slack.service.WorkspaceService;
import com.slack.util.MailSender;

@RestController
public class AjaxController {
	//채널 서비스
	@Autowired
	ChannelService cSvc;
	//디렉터리 서비스
	@Autowired
	DirectoryService dSvc;
	//워크스페이스 서비스
	@Autowired
	WorkspaceService wSvc;
	//멤버 서비스
	@Autowired
	MemberService mSvc;
	//파일 서비스
	@Autowired
	FileService fSvc;
	//DM 서비스
	@Autowired
	DmService dmSvc;
	//검색 서비스
	@Autowired
	SearchService sSvc;
	//파일 Dao
	@Autowired 
	FileDao fDao;
	
	/** [MJ] 웹소켓 채널 메세지 보내기
	 * 	파라미터 : channelIdx, userId, Msg, type, workspaceIdx
	 *  리턴 : userId(아이디), msg(메세지), channelIdx(채널_idx), workspaceIdx(워크스페이스_idx), 
	 *  profileImg(프로필 이미지 이름), currentTime(보낸 시간), nickname(닉네임), type(채널 타입)
	 */
	@RequestMapping("/channel_send_msg")
	public Map<String, Object> channelSendMsg(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		String userId = reqMap.get("UserId");
		String msg = reqMap.get("Msg");
		String type = reqMap.get("type");
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		//닉네임 조회
		UsersProfileDto nickname = dSvc.getProfileDetail(workspaceIdx, userId);
		//프로필 이미지 조회
		String profileImg = cSvc.userProfileImgSelect(userId,workspaceIdx);
		//메세지 DB 저장
		cSvc.sendChannelMsg(channelIdx, userId, msg, workspaceIdx);
		//실시간 표시
		String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		retMap.put("userId", userId);
		retMap.put("msg",msg);
		retMap.put("channelIdx",channelIdx);
		retMap.put("workspaceIdx",workspaceIdx);
		retMap.put("profileImg", profileImg);
		retMap.put("sentTime", currentTime);
		retMap.put("nickname", nickname.getNickname());
		retMap.put("channel", type);
		
		return retMap;
	}
	
	/** [MJ] 워크스페이스 이름 가져오기
	 * 	파라미터 : workspaceIdx(워크스페이스_idx)
	 *  리턴 : workspaceName(워크스페이스 이름)
	 */
	@RequestMapping("/workspace_name")
	public Map<String, String> workspaceName(@RequestBody Map<String, String> reqMap) {
		Map<String, String> retMap = new HashMap<String,String>();
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		//워크스페이스 이름 조회
		String name = wSvc.workspaceNameSelect(workspaceIdx);
		retMap.put("name", name);
		
		return retMap;
	}
	
	/** [MJ] 각 채널 이름 가져오기
	 * 	파라미터 : channelIdx(채널_idx), workspaceIdx(워크스페이스_idx)
	 *  리턴 : channelName(채널 이름), channelUsers(채널 인원수)
	 */
	@RequestMapping("/channel_name")
	public Map<String, Object> workspaceChannelName(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<String,Object>();
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		//채널 이름 조회
		String name = cSvc.channelNameSelect(channelIdx);
		//채널 인원수 조회
		int users = wSvc.workspaceUsersCount(workspaceIdx);
		
		retMap.put("name", name);
		retMap.put("users", users);
		
		return retMap;
	}
	
	/** [MJ] 채널 메세지 내용 조회해서 뿌려주기
	 * 	파라미터 : channelIdx(채널_idx), workspaceIdx(워크스페이스_idx)
	 *  리턴 : Channel5Dto_list(채널 메세지 리스트), channelName(채널 이름), 
	 *  createdTime(만들어진 시간), channelMembers(채널 인원수)
	 */
	@RequestMapping("/channel_div_setting")
	public Map<String, Object> channelMsgList(@RequestBody Map<String, String> reqMap, HttpSession session) { 
		Map<String, Object> retMap = new HashMap<String,Object>();
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		//채널 메세지 리스트
		List<Channel5Dto> list = cSvc.channelMsgSelect(channelIdx, workspaceIdx);
		//채널 정보
		Channel5Dto dto = cSvc.channelroomSelect(channelIdx);
		//채널 인원수 조회
		int members = cSvc.ghostChannelSelect(channelIdx);
		
		retMap.put("list", list);
		retMap.put("channelName", dto.getChannelName());
		retMap.put("createdtime",dto.getCreatedtime());
		retMap.put("members",members);
		
		return retMap;
	}
	
	/** [MJ] 프로필 팝업창 리스트
	 * 	파라미터 : userId(아이디), workspaceIdx(워크스페이스_idx)
	 *  리턴 : name(이름), status, condition(상태), profileImage(프로필 이미지), userId(아이디)
	 */
	@RequestMapping("/user_profile")
	public Map<String, String> userProfile(Model model, @RequestBody Map<String, String> reqMap) {
		Map<String, String> retMap = new HashMap<String,String>();
		String userId = reqMap.get("UserId");
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		//프로필 이미지 리스트
		UsersProfileDto usersProfileDto = dSvc.getProfileDetail(workspaceIdx, userId);
		//유저 정보 리스트
		Users3Dto users3Dto = dSvc.getWorkspaceMyImg(workspaceIdx, userId);
		
		retMap.put("name", usersProfileDto.getName());
		retMap.put("status", usersProfileDto.getStatus());
		retMap.put("condition", usersProfileDto.getCondition());
		retMap.put("profileImg", users3Dto.getProfileImage()); 
		retMap.put("useremail", usersProfileDto.getUserId());
		
		return retMap;
	}
	
	/** [MJ] 즐겨찾기 추가/제거 버튼 기능
	 * 	파라미터 : userId(아이디), channelIdx(채널_idx)
	 *  리턴 : message(메세지), status(실행 상태)
	 */
	@RequestMapping("/user_favorite")
	//@Transactional
	public Map<String, String> userFavoriteChannel(Model model, @RequestBody Map<String, String> reqMap) {
		Map<String, String> retMap = new HashMap<String, String>();
		String userId = reqMap.get("UserId");
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		//중복체크
		int count = cSvc.checkFavorite(channelIdx, userId);
		//즐겨찾기 리스트에 있으면 삭제 없으면 추가
		if(count > 0) {
			//즐겨찾기 제거
			cSvc.favoriteDelete(channelIdx, userId);
			retMap.put("status", "deleted");
			retMap.put("message", "즐겨찾기가 해제되었습니다.");
		}else {
			//즐겨찾기 추가
			cSvc.favoriteInsert(channelIdx, userId);
			retMap.put("status", "inserted");
			retMap.put("message", "즐겨찾기에 추가되었습니다.");
		}
		
		return retMap;
	}
	
	/** [MJ] 채널 나가기 버튼 기능
	 * 	파라미터 : userId(아이디), channelIdx(채널_idx)
	 *  리턴 : message(메세지), status(실행 상태)
	 */
	@RequestMapping("/channel_delete")
	public Map<String, String> channelDelete(Model model, @RequestBody Map<String, String> reqMap) {
		Map<String, String> retMap = new HashMap<String, String>();
		String userId = reqMap.get("UserId");
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		//채널 삭제
		cSvc.channelDelete(channelIdx, userId);
		//채널 인원수 조회
		int ghostChannel = cSvc.ghostChannelSelect(channelIdx);
		if(ghostChannel == 0) {
			//채널 완전 삭제
			cSvc.ghostChannelDelete(channelIdx);
		}
		retMap.put("status", "deleted");
		retMap.put("message", "채널이 삭제되었습니다.");
		
		return retMap;
	}
	
	/** [MJ] 채널 생성 및 사용자 추가 1
	 * 	파라미터 : workspaceIdx(워크스페이스_idx), text(채널 이름), userId(아이디), topic(주제), explanation(설명)
	 *  리턴 : channelIdx(채널_idx), message(메세지)
	 */
	@RequestMapping("/channel_create")
	public Map<String, Object> channelCreate(Model model, @RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		String text = reqMap.get("Text");
		String userId = reqMap.get("UserId");
		String topic = reqMap.get("Topic");
		String explanation = reqMap.get("Explanation");
		
		//채널 생성
		cSvc.createChannel(workspaceIdx, text, userId, topic, explanation);
		///채널 번호 조회
		int channelIdx = cSvc.addChannelMemberSelect(workspaceIdx);
		//생성자를 채널 멤버로 추가
		cSvc.addChannelMemberUsers(userId, channelIdx);
		//생성된 채널 번호를 결과 맵에 담습니다.
		retMap.put("status", "success");
	    retMap.put("channelIdx", channelIdx); 
	    retMap.put("message", "채널이 생성되었습니다.");
		
		return retMap;
	}
	
	/** [MJ] 채널 생성 및 사용자 추가 2
	 * 	파라미터 : ChannelIdx(채널_idx), inviteAll(전체 초대), workspaceIdx(워크스페이스_idx), userId(아이디)
	 *  리턴 : status(실행 상태)
	 */
	@RequestMapping("/add_channel_member")
	@ResponseBody
	public Map<String, Object> addMember(@RequestBody Map<String, Object> reqMap) {
	    Map<String, Object> retMap = new HashMap<>();

	    try {
	    	// 1. JS에서 보낸 데이터 꺼내기
	    	int channelIdx = Integer.parseInt(String.valueOf(reqMap.get("ChannelIdx")));
	    	boolean isInviteAll = (boolean) reqMap.get("InviteAll");
	    	int workspaceIdx = Integer.parseInt(String.valueOf(reqMap.get("WorkspaceIdx")));
	    	String invitedUserId = (String) reqMap.get("UserId");
	        if (isInviteAll) {
	            //전체 멤버 추가 
	        	cSvc.addAllChannelMember(invitedUserId, channelIdx, workspaceIdx);
	        } else {
	            if (invitedUserId != null && !invitedUserId.isEmpty()) {
	            	//특정 인원 추가
	                cSvc.addChannelMemberUsers(invitedUserId, channelIdx);
	            }
	        }
	        retMap.put("status", "success");
	    } catch (Exception e) {
	        retMap.put("status", "error");
	        retMap.put("message", e.getMessage());
	    }
	    
	    return retMap;
	}

	/** [MJ] 채널 상세보기
	 * 	파라미터 : channelIdx(채널_idx)
	 *  리턴 : managerName(채널 만든 아이디), channelName(채널 이름), topic(주제), explanation(설명)
	 */
	@RequestMapping("/information_Info")
	public Map<String, Object> channelManagerName(Model model, @RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<>();
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		//채널 만든 아이디 이름 조회
		String managerName = cSvc.getChannelManagerName(channelIdx);
		//채널 이름 조회
		String channelName = cSvc.channelNameSelect(channelIdx);
		//채널 주제 조회
		String topic = cSvc.channelTopicSelect(channelIdx);
		//채널 설명 조회
		String explanation = cSvc.channelExplanationSelect(channelIdx);
		
		retMap.put("managerName", managerName);
		retMap.put("channelName",channelName);
		retMap.put("topic", topic);
		retMap.put("explanation", explanation);
		
		
		return retMap;
	}
	
	/** [MJ] 채널 이름 수정
	 * 	파라미터 : channelIdx(채널_idx), channelName(채널 이름), userId(아이디)
	 *  리턴 : channelManager(채널 만든 아이디)
	 */
	@RequestMapping("/update_channel_name")
	public Map<String, Object> channelNewName(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<>();
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		String channelName = reqMap.get("ChannelName");
		String channelManager = reqMap.get("UserId");		
		//채널 이름 변경
		cSvc.channelNewName(channelName, channelIdx, channelManager);
		//채널 매니저가 누구인지 판단해서 매니저만 실행되게 로직 변경 못함.
		
		retMap.put("message", "update 되었습니다.(userId가 같지 않으면 수정되지 않습니다.");
		retMap.put("UserId", channelManager);
		
		return retMap;
	}
	
	/** [MJ] 채널 주제 수정
	 * 	파라미터 : channelIdx(채널_idx), topic(주제), userId(아이디)
	 *  리턴 : channelManager(채널 만든 아이디), newTopic(주제)
	 */
	@RequestMapping("/update_topic")
	public Map<String, Object> channelTopic(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<>();
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		String newTopic = reqMap.get("Topic");
		String channelManager = reqMap.get("UserId");		
		//채널 주제 변경
		cSvc.channelTopic(newTopic, channelIdx, channelManager);
		//채널 매니저가 누구인지 판단해서 매니저만 실행되게 로직 변경 못함.
		retMap.put("message", "update 되었습니다.(userId가 같지 않으면 수정되지 않습니다.");
		retMap.put("UserId", channelManager);
		retMap.put("topic", newTopic);
		
		return retMap;
	}
	
	/** [MJ] 채널 설명 수정
	 * 	파라미터 : channelIdx(채널_idx), explanation(설명), userId(아이디)
	 *  리턴 : channelManager(채널 만든 아이디), explanation(설명)
	 */
	@RequestMapping("/update_explanation")
	public Map<String, Object> channelExplanation(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<>();
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		String explanation = reqMap.get("Explanation");
		String channelManager = reqMap.get("UserId");		
		//채널 설명 변경
		cSvc.channelExplanation(explanation, channelIdx, channelManager);
		//채널 매니저가 누구인지 판단해서 매니저만 실행되게 로직 변경 못함.
		retMap.put("message", "update 되었습니다.(userId가 같지 않으면 수정되지 않습니다.");
		retMap.put("UserId", channelManager);
		retMap.put("explanation", explanation);
		
		return retMap;
	}
	
	/** [MJ] 채널 생성 시 유저 검색
	 * 	파라미터 : workspaceIdx(워크스페이스_idx), search(검색어)
	 *  리턴 : userList(검색 유저 리스트), count(검색수)
	 */
	@RequestMapping("/search_users")
	@ResponseBody
	public Map<String, Object> workspaceSearchUsers(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<>();
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		String search = reqMap.get("Search");
		//유저 리스트
		List<String> userId = cSvc.channelMemberSelect(workspaceIdx);
		//검색 유저 리스트
		List<Map<String, Object>> userList = wSvc.workspaceUserSearch(workspaceIdx, search);
		
		retMap.put("userList",userList);
		retMap.put("count", userList.size());
		
		return retMap;
	}
	
	/** [MJ] 채널 상세보기(멤버) 멤버 검색 이거 다시 만들어야함. 01.30
	 * 	파라미터 : 
	 *  리턴 : 
	 */
	@RequestMapping("/search_all_member")
	public Map<String, Object> searchAllMember(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<>();
		
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		String search = reqMap.get("Search");
		List<String> userId = cSvc.channelMemberSelect(workspaceIdx);
		List<Map<String, Object>> userList = wSvc.workspaceUserSearch(workspaceIdx, search);
		
		
		retMap.put("userList", userList);
		retMap.put("userId", userId);
		retMap.put("search", search);
		
		return retMap;
	}
	
	/** [MJ] 채널 멤버 조회
	 * 	파라미터 : workspaceIdx(워크스페이스_idx), channelIdx(채널_idx)
	 *  리턴 : list(채널 멤버 리스트)
	 */
	@RequestMapping("/channel_member_select")
	public Map<String, Object> channelMemberSelect(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<>();
		
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		//채널 멤버 리스트
		List<Map<String, Object>> users = cSvc.channelMemberName(channelIdx, workspaceIdx);
		retMap.put("list", users);
		
		return retMap;
	}
	
	/** [MJ] 채널 멤버 제거
	 * 	파라미터 : userId(아이디), channelIdx(채널_idx)
	 *  리턴 : userId(아이디)
	 */
	@RequestMapping("/remove_channel_member")
	public Map<String, Object> removeChannelMember(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<>();
		
		String userId = reqMap.get("UserId");
		int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx"));
		//채널 멤버 제거
		cSvc.removeChannelMember(userId, channelIdx);
		
		retMap.put("userid", userId);
		
		return retMap;
	}
	
	/** [MJ] 워크스페이스 사용자 추가
	 * 	파라미터 : userId(아이디), workspaceIdx(워크스페이스_idx)
	 *  리턴 : nickname(닉네임), name(이름), profileImg(프로필 이미지)
	 */
	@RequestMapping("/invite_users")
	public Map<String, Object> inviteUsers(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<>();
		
		String userId = reqMap.get("UserId");
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		UsersDto member = wSvc.usersSelect(userId);
		String nickname = ((UsersDto) member).getNickname();
		String name = ((UsersDto) member).getName();
		//난수 발생으로 프로필 이미지 추가
		int rd = (int)(Math.random()*10)+1;
		String randomImg = rd+"";
		//워크스페이스 사용자 추가
		wSvc.workspaceInsert(workspaceIdx, userId, nickname, randomImg);
		
		retMap.put("nickname", nickname);
		retMap.put("name", name);
		retMap.put("profileImg", randomImg);
		
		return retMap;
	}
	
	/** [MJ] 워크스페이스 사용자 검색
	 * 	파라미터 : workspaceIdx(워크스페이스_idx), Search(검색어)
	 *  리턴 : list(검색 리스트)
	 */
	@RequestMapping("/search_directory_member")
	public Map<String, Object> searchDirectoryMember(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<>();
		
		String search = reqMap.get("Search");
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		//검색한 리스트
		List<Map<String, Object>> list = dSvc.workspaceUsersSearch(workspaceIdx ,search);
		
		retMap.put("list", list);
		
		return retMap;
	}
	
	/** [MJ] 비밀번호 변경
	 * 	파라미터 : userId(아이디), userPw(비밀번호), newPw(새 비밀번호), newPwCheck(새 비밀번호 확인)
	 *  리턴 : nowPw(새 비밀번호)
	 */
	@RequestMapping("/set_password")
	public Map<String, Object> setMyPw(@RequestBody Map<String, String> reqMap) { 
		Map<String, Object> retMap = new HashMap<>();
		
		String userId = reqMap.get("UserId");
		String currentPw = reqMap.get("UserPw");
		String newPw = reqMap.get("NewPw");
		String newPwCheck = reqMap.get("NewPwCheck");
		String nowPw = mSvc.selectPw(userId);
		
		if(nowPw.equals(currentPw) && newPw.equals(newPwCheck)) {
			//비밀번호 변경
			mSvc.setPw(newPw, userId);			
		}
		retMap.put("nowPw", nowPw);
		
		return retMap;
	}
	
	/** [MJ] 내 상태 설정
	 * 	파라미터 : condition(상태), workspaceIdx(워크스페이스_idx), userId(아이디)
	 *  리턴 : condition(상태)
	 */
	@RequestMapping("/set_condition")
	public Map<String, Object> setCondition(@RequestBody Map<String, String> reqMap) { 
		Map<String, Object> retMap = new HashMap<>();
		
		String condition = reqMap.get("Condition");
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		String userId = reqMap.get("UserId");
		//내 상태 업데이트
		dSvc.updateSetMyCondition(condition, userId);
		//프로필 리스트
		UsersProfileDto list = dSvc.getProfileDetail(workspaceIdx, userId);
		String nowcondition = list.getCondition();
		
		retMap.put("condition", nowcondition);
		
		return retMap;
	}
	
	/** [MJ] 내 프로필 편집
	 * 	파라미터 : workspaceIdx(워크스페이스_idx), userId(아이디), name(이름), nickname(닉네임), title(직급)
	 *  리턴 : name(이름), nickname(닉네임)
	 */
	@RequestMapping("/myProfileUpdate")
	public Map<String, Object> myProfileUpdate(@RequestBody Map<String, String> reqMap) {
		Map<String, Object> retMap = new HashMap<>();
		
		int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
		String userId = reqMap.get("UserId");
		String name = reqMap.get("Name");
		String nickname = reqMap.get("Nickname");
		String title = reqMap.get("Title");
		//직함 업데이트
		dSvc.setMyProfileNameAndTitle(name, title, userId);
		//닉네임 업데이트
		dSvc.setMyProfileNickname(nickname, userId, workspaceIdx);
		
		retMap.put("name", name);
		retMap.put("nickname", nickname);
		
		return retMap;
	}
	
	/** [MJ] 파일업로드
	 * 	파라미터 : file(파일 이미지), userId(아이디), workspaceIdx(워크스페이스_idx)
	 *  리턴 : saveFilename(파일 이미지)
	 */
	@RequestMapping("/file_upload")
	@ResponseBody
	public String fileUpload(MultipartFile file, String userId, int workspaceIdx) throws Exception {
		String path = "/opt/upload";
		File f = new File(path); //파일시스템 관련 처리 --> file 객체
		if(!f.exists())
			f.mkdirs(); //해당 폴더를 생성함(필요시 상위폴더까지 한 번에)
		
		if(!file.isEmpty()) {
			String filename = file.getOriginalFilename(); //원본파일명
			String extension = filename.substring(filename.lastIndexOf("."));
			
			String savedFilename = UUID.randomUUID().toString()+ extension;
			
			File savedFile = new File(path, savedFilename);
														//저장할 파일명을 직접 지정함
			file.transferTo(savedFile);// 실제로 파일을 서버에 저장함.
			//파일 이미지 업데이트
			wSvc.profileUpload(savedFilename, userId, workspaceIdx);
			
			return savedFilename;
		}
		return "fail";
	}
	
	/** [MJ] 회원가입
	 * 	파라미터 : userId(아이디), nickname(별명), name(이름), password(비밀번호), phone(전화번호)
	 *  리턴 : Map(회원정보)
	 */
	@RequestMapping("/createId")
	public Map<String, Object> createId(@RequestBody Map<String, String> reqMap) { 
		Map<String, Object> retMap = new HashMap<>();
		
		String email = reqMap.get("UserId");
		String nickname = reqMap.get("Nickname");
		String name = reqMap.get("Name");
		String password = reqMap.get("Password");
		String phone = reqMap.get("Phone");
		//회원 가입
		mSvc.createId(email, nickname, name, password, phone);
		
		return retMap;
	}
	
	/** [SB] 워크스페이스 생성
	 *  파라미터 :  userId(아이디), workspaceName(워크스페이스 이름), nickname(별명),profileImage(프로필이미지)
	 *  리턴 : workspaceIdx(워크스페이스 idx)
	 */
	@RequestMapping("/workspace/data")
	public Map<String, Integer> ajax1(@RequestBody Map<String, String> reqMap){
		Map<String, Integer> retMap = new HashMap<>();
		// response , 응답 객체를 맵에 담아서 ( Dto에 담아줘도 됨 )
		String loginId = reqMap.get("UserId");
		String nickname = wSvc.selectUsersNickname(loginId); //임시 불러봐야함.
		String workspaceName = reqMap.get("workspaceName");
		String profileImage = (int)(Math.random() * 10) + 1 + "";
		// 프로필이미지, 별명, 워크스페이스 이름, 아이디로 새로운 워크스페이스 idx 생성 
		int workspaceIdx = wSvc.insertNewWorkspaceAndUsers(workspaceName, loginId, nickname, profileImage);
		retMap.put("workspaceIdx", workspaceIdx);
		return retMap;
	}
	
	/** [SB] 입력된 이메일 조회 
	 *  파라미터 : emailTo(사용자가 입력한 이메일), workspaceIdx(워크스페이스idx)
	 *  리턴 : inviteCode(초대코드)
	 */
	@RequestMapping("/invite")
	public Map<String, String> ajaxInvite(@RequestBody Map<String, String> reqMap, HttpSession session) {
		Map<String, String> retMap = new HashMap<>();
		int workspaceIdx = Integer.parseInt(reqMap.get("workspaceIdx"));
		String emailTo = reqMap.get("emailTo");
		
		session.setAttribute("emailTo", emailTo); // inviteCode.jsp로 이메일 주소 보내줌 
		
		String strChars = "abcdefghijklmnopqrstuvwxyz0123456789";
		String inviteCode = "";
		while(inviteCode.length() < 6) {
			int len = strChars.length();
			int idx = (int)(Math.random() * len);
			inviteCode += strChars.charAt(idx);
		}
		
		wSvc.insertInviteCode(workspaceIdx, emailTo, inviteCode); //이메일,초대코드,워크스페이스idx를 db에 저장
		session.setAttribute("inviteCode", inviteCode);
		MailSender.sendMail(emailTo, "인증코드", "인증코드는 " + inviteCode);// 사용자가 입력한 이메일로 이메일 전송 
		retMap.put("result", "success");
		return retMap;
	}
	
	/** [SB] 파일idx로 파일삭제 
	 *  파라미터 : fileIdx(파일idx)
	 *  리턴 : retMap(결과값을 넘겨줌)
	 */
	@RequestMapping("/file/delete")
	public Map<String, String> ajaxFileDelete(@RequestBody Map<String, String> reqMap){
		Map<String, String> retMap = new HashMap<>();
		String fileIdx = reqMap.get("fileIdx");
		fSvc.deleteFileIdx(Integer.parseInt(fileIdx));// 파일idx로 해당 파일 삭제 
		retMap.put("result","success");
		return retMap;
	}
	
	/** [SB] 파일검색 
	 *  파라미터 : userId(아이디), searchKeyword(검색어), workspaceIdx(워크스페이스idx)
	 *  리턴 : List<FileDmChannelDto>(검색한 내용의 dto)
	 */
	@RequestMapping("/file/search")
	public Map<String, Object> ajaxFileSearch(@RequestBody Map<String, String> reqMap, HttpSession session){
		Map<String, Object>retMap = new HashMap<>();
		String userId = (String)session.getAttribute("userId"); 
		String searchKeyword = reqMap.get("keyword");
		int workspaceIdx = Integer.parseInt(reqMap.get("workspaceIdx"));
		// 검색어, 아이디, 워크스페이스idx를 기반으로한 내용의 dto 
		List<FileDmChannelDto> searchList = fSvc.selectAllFileList(searchKeyword, userId, workspaceIdx);
		// 그 list를 맵에 담아 반환 
		retMap.put("searchList",searchList);
		return retMap;
	}
	
	/** [SB] keyword로 디엠목록검색 
	 *  파라미터 : keyword(검색어), userId(아이디), workspaceIdx(워크스페이스idx) 
	 *  리턴 : List<DmContentDto>(내용이 담긴 dto)
	 */
	@RequestMapping("/dm/data")
	public Map<String,Object> ajaxDm (@RequestBody Map<String, String> reqMap, HttpSession session, Model model){
		Map<String, Object> retMap = new HashMap<>();
		int workspaceIdx = Integer.parseInt(reqMap.get("workspaceIdx"));
		String userId = (String)session.getAttribute("userId");
		String keyword = reqMap.get("keyword");
		// 아이디와 워크스페이스idx로 검색내역 리스트를db에 저장 
		List<SearchHistoryDto>historyList = sSvc.selectSearchContent(userId, workspaceIdx);
		// 저장된 list를 맵으로 반환
		retMap.put("historyList", historyList);
		if(keyword != null) {
			List<DmContentDto>dmList = dmSvc.selectDmListByContent(userId, keyword, workspaceIdx);
			retMap.put("dmList", dmList);
		}	
		return retMap;
	}
	
	/** [SB] 전체검색내역 삭제 
	 *  파라미터 : searchIdx(검색idx)
	 *  리턴 : retMap
	 */
	@RequestMapping("/dm/searchDelete")
	public Map<String,Object> ajaxDmSearchDelete (@RequestBody Map<String, String> reqMap){
		Map<String, Object> retMap = new HashMap<>();
		int searchIdx = Integer.parseInt(reqMap.get("searchIdx"));
		//검색idx를 통해 검색내역삭제 
		sSvc.deleteSearchContent(searchIdx);
		return retMap;
	}
	
	/** [SB] 전체검색 
	 *  파라미터 : userId(아이디), searchKeyword(검색어), workspaceIdx(워크스페이스idx) 
	 *  리턴 : List<SearchDmChannelDto>(내용이 담긴 dto)
	 */
	@RequestMapping("/searchAll")
	public Map<String,Object> ajaxSearchAll (@RequestBody Map<String, String> reqMap, HttpSession session){
		Map<String, Object> retMap = new HashMap<>();
		int workspaceIdx = Integer.parseInt(reqMap.get("workspaceIdx"));
		String userId = (String)session.getAttribute("userId");
		String searchKeyword = reqMap.get("searchKeyword");
		// 아이디, 검색어, 워크스페이스idx로 검색내역idx 생성
		int searchHistoryIdx = sSvc.insertSearchContent(userId, searchKeyword, workspaceIdx);
		// 아이디,워크스페이스idx,검색내역idx로 전체검색 list 생성
		List<SearchDmChannelDto> searchListAll = sSvc.selectAllSearchList(userId, userId, userId, workspaceIdx, searchKeyword);
		// 맵에 담아 반환 
		retMap.put("searchHistoryIdx", searchHistoryIdx);
		retMap.put("searchListAll", searchListAll);
		return retMap;
	}
	
	/** [SB] 유저아이디로 디엠idx가져오기 
	 *  파라미터 : userId(아이디), workspaceIdx(워크스페이스idx)
	 *  리턴 : dmIdx(디엠idx)
	 */
	@RequestMapping("/dm/getDmIdxByUserId")
	public Map<String, Object> ajaxGetDmIdxByUserId(@RequestBody Map<String, String> reqMap, HttpSession session) {
		String userId = (String)session.getAttribute("userId");
		
		int workspaceIdx = Integer.parseInt(reqMap.get("workspaceIdx"));
		String targetId = reqMap.get("targetId");
		//아이디, 상대방아이디, 워크스페이스idx로 디엠idx를 찾아냄
		Integer dmIdxNew = dmSvc.getMaxDmByDmFromDmTo(userId, targetId, workspaceIdx);
		
		Map<String, Object> retMap = new HashMap<>();
		//맵에 담아 반환 
		retMap.put("dmIdxNew", dmIdxNew);
		return retMap;		
	}
	
	/** [SB] 프로필에서 이미지 업로드 
	 *  파라미터 : file(파일이름), workspaceIdx(워크스페이스idx)
	 *  리턴 : result
	 */
	@RequestMapping("/upload")
	public String uploadFile(MultipartFile file, int workspaceIdx, HttpSession session) throws Exception {
		//1. upload 폴더를 생성
		String path = "/opt/upload/"; // upload 폴더의 절대경로 ( C:\로 시작[맥은 다름]) 얻기 // tomcat modules에서 설정한 path값이랑 일치시키기
		System.out.println("절대경로 : " + path);
		
		//폴더가 없으면 생성 : 
		File f = new File(path); // 파일시스템 관련 처리 --> file 객체. 
		if(!f.exists())
			f.mkdirs(); // 해당 폴더를 생성함 ( 필요시 상위폴더까지 한 번에 )
		
		//2. 폴더 지정하고, 지정한 폴더에 저장 ----> 파일 정보 ( ex. 파일명 ) 을 얻음  
		if(!file.isEmpty()) {
			String filename = file.getOriginalFilename(); // 원본파일명
			File savedFile = new File(path, filename); // 스프링에서는 , "rename policy" 대신에 저장할 파일명을 직접 지정함 
			file.transferTo(savedFile); // 이때 실제로 파일을 서버에 저장 
			String userId = (String)session.getAttribute("userId"); 
			wSvc.changeProfileImage(workspaceIdx, userId, filename);
			return "success";
		}
		
		return "fail";
	}
	
	/** [SB] 채팅파일업로드 
	 *  파라미터 : otherId(상대방아이디), workspaceIdx(워크스페이스idx)
	 *  리턴 : result
	 */
	@RequestMapping ("/uploadChatFile")
	public Map<String, Object> ajaxGetUploadFile(MultipartHttpServletRequest request, String otherId, HttpSession session, @RequestParam("workspace_idx") int workspaceIdx) throws Exception {
		Map<String,Object>resultMap = new HashMap<>();
		MultipartFile file = request.getFile("uploadFile");
		
		String realSavedpath = "/opt/upload/"; // upload 폴더의 절대경로 ( C:\로 시작[맥은 다름]) 얻기
		System.out.println("절대경로 : " + realSavedpath);
		
		File f = new File(realSavedpath); // 파일시스템 관련 처리 --> file 객체. 
		if(!f.exists())
			f.mkdirs(); // 해당 폴더
		String originName = file.getOriginalFilename();

		String extension = ""; 

		if (originName != null && originName.contains(".")) {
		    
		    extension = originName.substring(originName.lastIndexOf("."));
		}
		
		String fileName = System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8) + extension;
		//실제 저장경로 설정
		File saveFile = new File(realSavedpath + fileName);
	    file.transferTo(saveFile);
	    // 맵에 경로 및 파일이름을 담아줌 
	    resultMap.put("url", "/upload/" + fileName);
	    resultMap.put("fileName", file.getOriginalFilename());
	    resultMap.put("isImage", file.getContentType().startsWith("image"));

	    String userId = (String)session.getAttribute("userId");
	    fDao.insertFiles(fileName, workspaceIdx); // 파일 테이블에 추가 

	    int newfileIdx = fDao.getFileIdxByName(fileName); // 추가된 파일 idx 가져오기

	    // 1. 파일 올린 본인에 대한 권한 추가 (userId는 세션에 있으므로 안전함)
	    fDao.insertFilesPermission(newfileIdx, userId); 

	    // 2. [수정 포인트] 상대방 권한 추가: otherId가 있을 때(1:1 채팅)만 실행
	    // 전체 채팅방의 경우 otherId가 null이거나 빈 문자열이므로 이 블록을 건너뜀
	    if (otherId != null && !otherId.trim().isEmpty()) {
	        fDao.insertFilesPermission(newfileIdx, otherId);
	        System.out.println("1:1 채팅 권한 추가 완료: " + otherId);
	    } else {
	       
	        System.out.println("전체 채팅방 업로드: 상대방 권한 추가 생략");
	    }

	    return resultMap;
	}
	
	/** [SB] 멘션기능
	 *  파라미터 : workspaceIdx(워크스페이스idx)
	 *  리턴 : userId(아이디), nickname(닉네임)
	 */
	@RequestMapping("/getMentionList")
	public List<Map<String, String>>ajaxGetUserIdAndNicknameforMention(@RequestBody Map<String, String> reqMap) throws Exception {
		List<Map<String,String>>retList = new ArrayList<>();
		
		int workspaceIdx = Integer.parseInt(reqMap.get("workspaceIdx"));
		//워크스페이스idx로 아이디와 닉네임의 리스트 가져옴
		List<WorkspaceUsersMentionDto>list1 = wSvc.getUserIdAndNickname(workspaceIdx);
		// 반복문을 통해 각각의 아이디와 닉네임을 맵에 담아준 후 반환 
		for(WorkspaceUsersMentionDto dto : list1 ) {
			Map<String,String>userMap = new HashMap<>();
			userMap.put("id", dto.getUserId());
			userMap.put("value", dto.getNickname());
			retList.add(userMap);
		}
		return retList;
	}
}
