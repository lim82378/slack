package com.slack.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.slack.dto.Channel5Dto;
import com.slack.dto.ChannelIdxNameDto;
import com.slack.dto.ChannelMsgInviteWorkspaceUsersDto;
import com.slack.dto.DmDetailListDto;
import com.slack.dto.DmLeftListDto;
import com.slack.dto.DmListDto;
import com.slack.dto.DmMentionDto;
import com.slack.dto.DmUsersDto;
import com.slack.dto.FileWorkspaceUsersDto;
import com.slack.dto.SearchDmChannelDto;
import com.slack.dto.SearchHistoryDto;
import com.slack.dto.Users3Dto;
import com.slack.dto.UsersProfileDto;
import com.slack.dto.WorkspaceDto;
import com.slack.service.ActivityService;
import com.slack.service.ChannelService;
import com.slack.service.DirectoryService;
import com.slack.service.DmService;
import com.slack.service.FileService;
import com.slack.service.MemberService;
import com.slack.service.SearchService;
import com.slack.service.WorkspaceService;

@Controller
public class HomeController {
	//채널 서비스
	@Autowired
	ChannelService cSvc;
	//디렉터리 서비스
	@Autowired
	DirectoryService dSvc;
	//멤버 서비스
	@Autowired
	MemberService mSvc;
	//DM 서비스
	@Autowired
	DmService dmSvc;
	//워크스페이스 서비스
	@Autowired
	WorkspaceService wSvc;
	//파일 서비스
	@Autowired
	FileService fSvc;
	//내 활동 서비스
	@Autowired
	ActivityService aSvc;
	//검색 서비스
	@Autowired
	SearchService sSvc;
	//파일 검색 서비스
	@Autowired
    private ServletContext servletContext;
	
	/** [MJ] 홈(디렉토리, 채널)
	 * 	파라미터 : workspaceIdx, userId
	 * 
	 */
	@RequestMapping("/homeDirectory")
	public String home(Model model, HttpSession session, @RequestParam("workspace_idx") int workspaceIdx) {
		String userId = (String)session.getAttribute("userId");
		String orderByType = "참여순"; //기본
		//채널 내용 리스트
		List<SearchHistoryDto> historyList = sSvc.selectSearchContent(userId, workspaceIdx);
		model.addAttribute("historyList",historyList);
		//즐겨찾기 리스트
		List<ChannelIdxNameDto> listFavoriteChannel =  cSvc.favoriteChannel(workspaceIdx, userId);
		model.addAttribute("listFavoriteChannel", listFavoriteChannel);
		//내 채널 이름 리스트
		List<ChannelIdxNameDto> listMyChannelName =  cSvc.myChannelName(workspaceIdx, userId);
		model.addAttribute("listMyChannelName", listMyChannelName);
		//워크스페이스 멤버 리스트
		List<Users3Dto> listGetWorkspaceMembers = dSvc.getWorkspaceMembers(workspaceIdx, orderByType);
		model.addAttribute("listGetWorkspaceMembers", listGetWorkspaceMembers);
		//내 프로필 이미지 조회
		Users3Dto getWorkspaceMyImg = dSvc.getWorkspaceMyImg(workspaceIdx, userId);
		model.addAttribute("getWorkspaceMyImg",getWorkspaceMyImg);
		//모든 멤버 프로필 이미지 리스트
		List<UsersProfileDto> listAllProfileSelect = dSvc.getProfileListWithWorkspaceIdx(workspaceIdx);
		model.addAttribute("listAllProfileSelect",listAllProfileSelect);
		//워크스페이스_idx
		model.addAttribute("workspace_idx",workspaceIdx);
		
		return "homeDirectory";
	}
	//로그인 페이지로 이동
	@RequestMapping("/")
	public String login() {
		return "Login";
	}
	
	//회원가입 페이지
	@RequestMapping("/CreateId")
	public String CreateId() {
		return "CreateId";
	}
	
	/** [MJ] 로그인 체크
	 * 	input : id, pw
	 *  output : workspaceInfoList, 로그인 성공 시 workspace / 실패 시 Login
	 */
	@RequestMapping("/login_check")
	public String loginCheck(Model model, String id, String pw, HttpSession session) {
		int count = mSvc.loginSelect(id, pw);
		String setCondition = "";
		if(count == 1) {
			session.setAttribute("userId",id);
			//내 상태 초기화
			dSvc.updateSetMyCondition(setCondition, id);
			int workspaceCount = wSvc.workspaceListCheck(id);
			if(workspaceCount > 0) {
				//워크스페이스 정보 리스트
				List<WorkspaceDto> workspaceInfoList = wSvc.selectAllWorkspaceInfo(id);
				model.addAttribute("workspaceInfoList",workspaceInfoList);
				return "workspaceList";
			}else  {
				return "redirect: workspace";
			}
		}else {
			model.addAttribute("msg","잘못된 로그인 정보입니다.");
			return "Login";
		}
	}
	
	//로그아웃
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("userId");
		
		return "redirect:/";
	}
	
	/** 파일 업로드
	 * 	input : file
	 *  output : map
	 */
	@RequestMapping("/uploadImage.do")
    @ResponseBody
    public Map<String, Object> uploadImage(@RequestParam("imgFile") MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        
        // 1. 저장 경로 설정 (실제 물리적 경로)
        String savePath = "/opt/upload/"; 
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + extension;

        try {
            // 2. 파일 저장
            File target = new File(savePath, savedName);
            file.transferTo(target);

            // 3. 리소스 매핑 경로를 반환
            map.put("url", "/upload/" + savedName);
            map.put("status", "success");
        } catch (Exception e) {
            map.put("status", "error");
        }
        return map;
    }
	
	/** [SB] 전체검색화면 (home2)으로 이동 
	 *  파라미터 : searchKeyword(검색어), workspaceIdx(워크스페이스idx), userId(아이디)
	 */
	@RequestMapping("/home2")
	public String home2(Model model, @RequestParam("searchKeyword") String searchKeyword, HttpSession session, @RequestParam("workspace_idx") int workspaceIdx) {
		String userId = (String)session.getAttribute("userId"); 
		model.addAttribute("workspace_idx",workspaceIdx);
		// 아이디와 워크스페이스idx로 검색내역리스트 가져옴 
		List<SearchHistoryDto> historyList = sSvc.selectSearchContent(userId, workspaceIdx);
		//아이디와 워크스페이스idx, 검색어로 전체검색결과 가져옴 
		List<SearchDmChannelDto> allSearchList = sSvc.selectAllSearchList(userId, userId, userId, workspaceIdx, searchKeyword);
		//검색내역과 전체검색결과, 키워드를 모델에 담아 반환 
		model.addAttribute("historyList",historyList);
		model.addAttribute("allSearchList", allSearchList);
		model.addAttribute("searchKeyword", searchKeyword);
		return "home2";
	}
	
	/** [SB] 디엠 페이지로 이동  
	 *  파라미터 : dmIdx(디엠idx), dmTo(디엠 상대방), workspaceIdx(워크스페이스idx), userId(아이디)
	 */
	@RequestMapping("/dm")
	public String dm(@RequestParam(value="dm_idx" ,required=false) Integer dmIdx, 
					@RequestParam(value="dm_to", required=false) String dmTo, 
					Model model, HttpSession session, 
					@RequestParam("workspace_idx") int workspaceIdx) {
		String userId = (String)session.getAttribute("userId");
		model.addAttribute("workspace_idx",workspaceIdx);
		//아이디와 워크스페이스idx로 왼쪽에 뿌려줄 디엠목록 가져옴
		List<DmLeftListDto> listLeft = dmSvc.getLeftList(userId, workspaceIdx);
		// 아이디와 워크스페이스idx로 디엠사용자리스트 가져옴
		List<DmUsersDto> userList = dmSvc.getUsersNameForNewDm(userId, workspaceIdx);
		// 아이디와 워크스페이스idx로 검색내역리스트 가져옴 
		List<SearchHistoryDto> historyList = sSvc.selectSearchContent(userId, workspaceIdx);

		//상대방아이디 불러오는 쿼리문 서비스 String
		String getDmTo = null;
		List<DmListDto> listRight = null;
		DmDetailListDto dmFromToDto = null;
		///////////// /dm 최초 화면...? dm_idx가 null일 때...?
		if(dmIdx == null && dmTo != null) {
			// dmTo, dmFrom(userId) -------> max(dm_idx)
			model.addAttribute("dmTo", dmTo);
			dmIdx = dmSvc.getMaxDmByDmFromDmTo(userId, dmTo, workspaceIdx);
			if(dmIdx != null) {
				return "forward:/dm?dm_idx=" + dmIdx;
			}
		} else if(dmIdx == null) {
			listRight = null;
			getDmTo = dmTo;
		} 
		 else {
			listRight = dmSvc.getDmListDetail(dmIdx, userId);
			dmFromToDto = dmSvc.getFromToByDmIdx(dmIdx);
		}
		// 검색내역과 왼쪽에 뿌려줄 디엠목록, 오른쪽에 뿌려줄 상세내용을 모델에 담아 반환 
		model.addAttribute("historyList",historyList);
		model.addAttribute("listLeft", listLeft);
		model.addAttribute("listRight", listRight);
		model.addAttribute("userList", userList);
		// 상대방아이디를 모델에 담아 반환 
		model.addAttribute("dmFromToDto", dmFromToDto);
		
		return "dm";
	}

	/** [SB] 워크스페이스 리스트 페이지 이동 
	 *  파라미터 : userId(아이디)
	 */
	@RequestMapping("/workspaceList")
	public String workspaceList(Model model, HttpSession session) {
		String userId = (String)session.getAttribute("userId");
		// 아이디를 통해 워크스페이스 정보 리스트를 뽑아옴 
		List<WorkspaceDto> workspaceInfoList = wSvc.selectAllWorkspaceInfo(userId);
		// 아이디를 통해 뽑아온 워크스페이스 정보 리스트를 모델에 담아 반환 
		model.addAttribute("workspaceInfoList", workspaceInfoList);
		model.addAttribute("userId",userId);
		
		return "workspaceList";
	}

	/** [SB] 파일 페이지로 이동 
	 *  파라미터 : workspaceIdx, userId
	 */
	@RequestMapping("/file")
	public String file(Model model, HttpSession session, @RequestParam("workspace_idx") int workspaceIdx) {
		String userId = (String)session.getAttribute("userId");
		List<FileWorkspaceUsersDto> fileList = fSvc.GetFileListFromUsers(userId);
		List<SearchHistoryDto> historyList = sSvc.selectSearchContent(userId, workspaceIdx);
		model.addAttribute("historyList",historyList);
		model.addAttribute("fileList", fileList);
		model.addAttribute("workspace_idx",workspaceIdx);
		return "file";
	}
	
	/** [SB] 내 활동 페이지로 이동 
	 *  파라미터 : workspaceIdx(워크스페이스idx), userId(아이디) 
	 */
	@RequestMapping("/myActivity")
	public String myActivity(Model model, HttpSession session, @RequestParam("workspace_idx") int workspaceIdx) {
		String userId = (String)session.getAttribute("userId");
		String userInvited = userId;
		String userMentioned = userId;
		model.addAttribute("workspace_idx",workspaceIdx);
		
		//초대받은사람, 워크스페이스idx로 초대리스트 뽑아옴 
		List<ChannelMsgInviteWorkspaceUsersDto> inviteList = aSvc.selectListOfMyActivityByChannel(userInvited, workspaceIdx);
		// 멘션당한 사용자 , 워크스페이스idx로 멘션리스트 뽑아옴 
		List<DmMentionDto> mentionList = aSvc.selectListOfMyActivityByDmMention(userMentioned, workspaceIdx);
		// 아이디와 워크스페이스idx로 검색내역 뽑아옴 
		List<SearchHistoryDto> historyList = sSvc.selectSearchContent(userId, workspaceIdx);
		model.addAttribute("historyList",historyList);
		// 전체리스트라는 객체를 생성하여 멘션과 채널초대의 내용을 정렬하여 넣어줌 
		List<Object> totalList = new ArrayList<>();
		totalList.addAll(inviteList);
		totalList.addAll(mentionList);
		Collections.sort(totalList, new Comparator<Object>() {
		    @Override
		    public int compare(Object o1, Object o2) {
		        String time1 = "";
		        String time2 = "";

		        // 첫 번째 객체 시간 추출
		        if (o1 instanceof ChannelMsgInviteWorkspaceUsersDto) {
		            time1 = ((ChannelMsgInviteWorkspaceUsersDto) o1).getInviteTime();
		        } else if (o1 instanceof DmMentionDto) {
		            time1 = ((DmMentionDto) o1).getSentTime();
		        }

		        // 두 번째 객체 시간 추출
		        if (o2 instanceof ChannelMsgInviteWorkspaceUsersDto) {
		            time2 = ((ChannelMsgInviteWorkspaceUsersDto) o2).getInviteTime();
		        } else if (o2 instanceof DmMentionDto) {
		            time2 = ((DmMentionDto) o2).getSentTime();
		        }

		        // 최신순(내림차순) 정렬: time2와 time1의 순서를 바꾸면 과거순(오름차순)이 됩니다.
		        return time2.compareTo(time1); 
		    }
		});
		System.out.println(inviteList.size());
		System.out.println(mentionList.size());
		System.out.println(totalList.size());
		// 전체 리스트를 모델에 담아 반환 
		model.addAttribute("totalList", totalList);
		return "myActivity";
	}
	
	//워크스페이스 페이지
	@RequestMapping("/workspace")
	public String workspace(HttpSession session) {
		String userId = (String)session.getAttribute("userId");
		return "workspace";
	}
	
	//워크스페이스 인증 페이지
	@RequestMapping("/inviteCode")
	public String inviteCode() {
		return "inviteCode";
	}
	
	/** [SB] 초대코드 폼 
	 *  파라미터 : userId(아이디), inviteCode(초대코드)
	 */
	@RequestMapping("/inviteCodeAction")
	public String inviteCodeCheck(HttpServletRequest Request, HttpSession session, Model model) {
		String userId = (String)session.getAttribute("userId");
		//아이디로 워크스페이스내용 리스트를 가져옴
		List<WorkspaceDto> workspaceInfoList = wSvc.selectAllWorkspaceInfo(userId);
		String inviteCode = (String)session.getAttribute("inviteCode");
		System.out.println(inviteCode);
		String a = Request.getParameter("a");
		String b = Request.getParameter("b");
		String c = Request.getParameter("c");
		String d = Request.getParameter("d");
		String e = Request.getParameter("e");
		String f = Request.getParameter("f");
		String allInput = a+b+c+d+e+f;
		// 사용자 입력값과 일치하면 워크스페이스 목록으로 / 아니면 다시 초대코드 화면으로 이동 
		if(allInput.equals(inviteCode)) {
			model.addAttribute("workspaceInfoList",workspaceInfoList);
			return "workspaceList";
		}else
			return "inviteCode";
	}
	
	//웹소켓 
	@RequestMapping("/webSocket")
	public String webSocket() {
		return "webSocket";
	}
	
	/** [MJ] 프로필 이미지 조회 및 이름 생성
	 * 	파라미터 : filename
	 */
	@GetMapping("/display")
	@ResponseBody
	public ResponseEntity<Resource> display(String filename) throws Exception {
		if (filename == null || filename.equals("null") || filename.isEmpty()) {
	        filename = "ProfileImg1.png"; 
	    }
	    String path = "/opt/upload/";
	    // 파일명이 ProfileImg로 시작하면 프로젝트 내부 resources 경로를 사용
	    if (filename.startsWith("ProfileImg")) {
	        // 실제 서버 내 resources 경로를 찾아야 함 (ServletContext 활용)
	        path = servletContext.getRealPath("/resources/img/profile/");
	    } else {
	        // 업로드된 파일은 외부 경로 사용
	        path = "/opt/upload/";
	    }
	    // 1. 파일 경로에서 리소스를 읽어옴
	    Resource resource = new FileSystemResource(path + filename);
	    
	    // 2. 헤더 설정 (브라우저에게 이미지임을 알려줌)
	    HttpHeaders header = new HttpHeaders();
	    Path filePath = Paths.get(path + filename);
	    header.add("Content-Type", Files.probeContentType(filePath));
	    
	    // 3. 파일 데이터와 함께 응답
	    return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
	}
}
