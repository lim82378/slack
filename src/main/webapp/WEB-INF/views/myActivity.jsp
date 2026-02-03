<%@page import="com.slack.dao.*"%>
<%@page import="com.slack.dto.*"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<%
	List<Object> totalList = (List<Object>)request.getAttribute("totalList");
	String loginId = (String)session.getAttribute("userId");
	List<SearchHistoryDto> historyList = (List<SearchHistoryDto>) request.getAttribute("historyList");
%>
<html>
<head>
<meta charset="UTF-8">
<title>내활동</title>
	<script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
	<link rel="stylesheet" href="resources/css/myactivity.css"/>
	<link rel="icon" href="resources/img/slackLogo.png"/>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100;300;400;500;700;900&display=swap" rel="stylesheet">
	<script>
		const globalWorkspaceIdx = <%=request.getParameter("workspace_idx")%>;
		const loginId = "<%= loginId %>";
	</script>
	<script>
	 	$(function(){
	 		$(".move_home_btn").click(function(){
	 			location.href="homeDirectory?workspace_idx="+ globalWorkspaceIdx;
	 		});
	 		$(".move_dm_btn").click(function(){
	 			location.href="dm?workspace_idx="+globalWorkspaceIdx;
	 		});
	 		$(".move_myactivity_btn").click(function(){
	 			location.href="myActivity?workspace_idx="+globalWorkspaceIdx;
	 		});
	 		$(".move_file_btn").click(function(){
	 			location.href="file?workspace_idx="+globalWorkspaceIdx;
	 		});
	 		$(".workspace_list_btn").click(function(){
	 			location.href="workspaceList";
	 		});
			$(".content_from_user").click(function(){
				let dmIdx = $(this).data("dmIdx");
				let type = $(this).data("type");
				let channelIdx = $(this).data("channelIdx");
				
				if(type===("MENTION")){
					location.href = "dm?dmIdx=" + dmIdx +"&workspace_idx=" + globalWorkspaceIdx;
				}else
					location.href = "homeDirectory?workspace_idx=" + globalWorkspaceIdx;

				/* $("#myactivity_content").css("display","none"); 
				$(".content_from_user").removeClass("on");
				$(this).addClass("on"); */
			});
			$(".x_btn").click(function(){
				$(this).closest(".popup_window").css("display","none");
				$("#myactivity_content").css("display","flex");
				$(".content_from_user").removeClass("on");
			});
		});
	 	
 	</script>
</head>
<body>
	<jsp:include page="searchAll.jsp" />
	<div id="myactivity_header" class="search_btn">
		<div>
			<h3 class="test_text" style="color: red;">전체 검색-></h3>
			<svg data-i0m="true" data-qa="search" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M9 3a6 6 0 1 0 0 12A6 6 0 0 0 9 3M1.5 9a7.5 7.5 0 1 1 13.307 4.746l3.473 3.474a.75.75 0 1 1-1.06 1.06l-3.473-3.473A7.5 7.5 0 0 1 1.5 9" clip-rule="evenodd"></path></svg>
			<span>전체검색</span>
		</div>
	</div>	
	<div id="myactivity_sidebar" class="fl">
		<div class="workspace_list_btn"><img src="resources/img/slackLogo.png" style="width: 36px;"/></div>
		<div>
			<div class="move_home_btn">
				<svg data-i0m="true" data-qa="home" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M10.14 3.001a.25.25 0 0 0-.28 0L4.5 6.631v8.12A2.25 2.25 0 0 0 6.75 17h6.5a2.25 2.25 0 0 0 2.25-2.25V6.63zm-7.47 4.87L3 7.648v7.102a3.75 3.75 0 0 0 3.75 3.75h6.5A3.75 3.75 0 0 0 17 14.75V7.648l.33.223a.75.75 0 0 0 .84-1.242l-7.189-4.87a1.75 1.75 0 0 0-1.962 0l-7.19 4.87a.75.75 0 1 0 .842 1.242m9.33 2.13a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1h1a1 1 0 0 0 1-1v-1a1 1 0 0 0-1-1z" clip-rule="evenodd"></path></svg>
			</div>
			<div>홈</div>
		</div>
		<div>
			<div class="move_dm_btn">
				<svg data-i0m="true" data-qa="direct-messages" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M7.675 6.468a4.75 4.75 0 1 1 8.807 3.441.75.75 0 0 0-.067.489l.379 1.896-1.896-.38a.75.75 0 0 0-.489.068 5 5 0 0 1-.648.273.75.75 0 1 0 .478 1.422q.314-.105.611-.242l2.753.55a.75.75 0 0 0 .882-.882l-.55-2.753A6.25 6.25 0 1 0 6.23 6.064a.75.75 0 1 0 1.445.404M6.5 8.5a5 5 0 0 0-4.57 7.03l-.415 2.073a.75.75 0 0 0 .882.882l2.074-.414A5 5 0 1 0 6.5 8.5m-3.5 5a3.5 3.5 0 1 1 1.91 3.119.75.75 0 0 0-.49-.068l-1.214.243.243-1.215a.75.75 0 0 0-.068-.488A3.5 3.5 0 0 1 3 13.5" clip-rule="evenodd"></path></svg>
			</div>
			<div>DM</div>
		</div>
		<div class="on">
			<div class="move_myactivity_btn">
				<svg data-i0m="true" data-qa="notifications-filled" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M10 1.5c-.689 0-1.31.246-1.767.762-.331.374-.54.85-.65 1.383-1.21.369-2.103 1.137-2.685 2.357-.604 1.266-.859 2.989-.894 5.185l-1.88 1.886-.012.013c-.636.7-.806 1.59-.373 2.342.407.705 1.224 1.072 2.104 1.072h3.388c.13.391.34.777.646 1.107.498.537 1.219.893 2.137.893.911 0 1.626-.358 2.119-.896.302-.33.51-.714.638-1.104h3.384c.88 0 1.697-.367 2.103-1.072.434-.752.264-1.642-.372-2.342l-.011-.013-1.878-1.886c-.035-2.196-.29-3.919-.894-5.185-.582-1.22-1.476-1.988-2.685-2.357-.112-.533-.32-1.009-.651-1.383C11.311 1.746 10.689 1.5 10 1.5" clip-rule="evenodd"></path></svg>
			</div>
			<div>내 활동</div>
		</div>
		<div>
			<div class="move_file_btn">
				<svg data-i0m="true" data-qa="canvas-browser" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M4.836 3A1.836 1.836 0 0 0 3 4.836v7.328c0 .9.646 1.647 1.5 1.805V7.836A3.336 3.336 0 0 1 7.836 4.5h6.133A1.84 1.84 0 0 0 12.164 3zM1.5 12.164a3.337 3.337 0 0 0 3.015 3.32A3.337 3.337 0 0 0 7.836 18.5h3.968c.73 0 1.43-.29 1.945-.805l3.946-3.946a2.75 2.75 0 0 0 .805-1.945V7.836a3.337 3.337 0 0 0-3.015-3.32A3.337 3.337 0 0 0 12.164 1.5H4.836A3.336 3.336 0 0 0 1.5 4.836zM7.836 6A1.836 1.836 0 0 0 6 7.836v7.328C6 16.178 6.822 17 7.836 17H11.5v-4a1.5 1.5 0 0 1 1.5-1.5h4V7.836A1.836 1.836 0 0 0 15.164 6zm8.486 7H13v3.322z" clip-rule="evenodd"></path></svg>
			</div>
			<div>파일</div>
		</div>	
	</div>
	<div id="myactivity_side_menu" class="fl">
		<div>
			<span>내 활동</span>
		</div>	
		<div class="side_menu_content">
	<%
	for(Object obj : totalList){
			
			String nickname = "";
			String content = "";
			String type = "";
			String profileImage = "";
			String channelName= "";
			String sentTime = "";
			String inviteTime = "";
			String userMentioned= "";
			int dmIdx = 0;
			int channelIdx = 0;
			
		if( obj instanceof ChannelMsgInviteWorkspaceUsersDto){
			ChannelMsgInviteWorkspaceUsersDto d = (ChannelMsgInviteWorkspaceUsersDto)obj;
			nickname = d.getNickname();
			content = d.getContent();
			dmIdx = d.getDmIdx();
			channelIdx = d.getChannelIdx();
			type = d.getType();
			channelName = d.getChannelName();
			profileImage = d.getProfileImage();
			sentTime = d.getInviteTime();
			int month = Integer.parseInt(sentTime.substring(5, 7));
			int day = Integer.parseInt(sentTime.substring(8, 10));
			sentTime = month + "월 " + day + "일";
		}else if(obj instanceof DmMentionDto) {
			DmMentionDto d = (DmMentionDto)obj;
			nickname = d.getNickname();
			dmIdx = d.getDmIdx();
			channelIdx = d.getChannelIdx();
			content = d.getContent();
			profileImage = d.getProfileImage();
			type = d.getType();
			sentTime = d.getSentTime();
			int month = Integer.parseInt(sentTime.substring(5, 7));
			int day = Integer.parseInt(sentTime.substring(8, 10));
			sentTime = month + "월 " + day + "일";
			userMentioned = d.getMentionNickname();
		}
		
		if(type.equals("MENTION")){
	%>
	
			<div class="content_from_user" data-dm_idx="<%=dmIdx%>" data-channel_idx="<%=channelIdx%>" data-type="<%=type%>">
				<div>
					<span>
						<svg data-i0m="true" data-qa="mentions" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M2.5 10a7.5 7.5 0 1 1 15 0v.645c0 1.024-.83 1.855-1.855 1.855a1.145 1.145 0 0 1-1.145-1.145V6.75a.75.75 0 0 0-1.494-.098 4.5 4.5 0 1 0 .465 6.212A2.64 2.64 0 0 0 15.646 14 3.355 3.355 0 0 0 19 10.645V10a9 9 0 1 0-3.815 7.357.75.75 0 1 0-.865-1.225A7.5 7.5 0 0 1 2.5 10m7.5 3a3 3 0 1 0 0-6 3 3 0 0 0 0 6" clip-rule="evenodd"></path></svg>
					</span>
					<span class="side_menu_content_where">멘션</span><span><%=sentTime%></span>
					<div>
						<div class="side_menu_content_profile fl">
							<%
								String newProfileImage = profileImage;
								int defaultProfileImage = -1;
								try {
									defaultProfileImage = Integer.parseInt(newProfileImage);
								} catch(Exception e) { }
								if(defaultProfileImage>=1 && defaultProfileImage<=10) { 
							%>
								<img src="display?filename=ProfileImg<%=defaultProfileImage%>.png" style="width: 36px;"/>
							<% }else { %>
								<img src="display?filename=<%=newProfileImage%>" style="width: 36px;"/>
							<% } %>
						</div>
						<div class="side_menu_content_profile_name">
							<div><%=nickname%></div>
							<span><%=userMentioned%></span>
							<span><%=content%></span>
						</div>
					</div>
				</div>
			</div>
		
		<% 
			}else {
		%>		
		
			<div class="content_from_user" data-dm_idx="<%=dmIdx%>" data-channel_idx="<%=channelIdx%>" data-type="<%=type%>">
				<div>
					<span>
						<svg data-i0m="true" data-qa="channel" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M9.74 3.878a.75.75 0 1 0-1.48-.255L7.68 7H3.75a.75.75 0 0 0 0 1.5h3.67L6.472 14H2.75a.75.75 0 0 0 0 1.5h3.463l-.452 2.623a.75.75 0 0 0 1.478.255l.496-2.878h3.228l-.452 2.623a.75.75 0 0 0 1.478.255l.496-2.878h3.765a.75.75 0 0 0 0-1.5h-3.506l.948-5.5h3.558a.75.75 0 0 0 0-1.5h-3.3l.54-3.122a.75.75 0 0 0-1.48-.255L12.43 7H9.2zM11.221 14l.948-5.5H8.942L7.994 14z" clip-rule="evenodd"></path></svg>
					</span>
					<span class="side_menu_content_where">채널 초대</span><span><%=sentTime%></span>
					<div>
						<div class="side_menu_content_profile fl">
							<%
								String newProfileImage = profileImage;
								int defaultProfileImage = -1;
								try {
									defaultProfileImage = Integer.parseInt(newProfileImage);
								} catch(Exception e) { }
								if(defaultProfileImage>=1 && defaultProfileImage<=10) { 
							%>
								<img src="display?filename=ProfileImg<%=defaultProfileImage%>.png" style="width: 36px;"/>
							<% }else { %>
								<img src="display?filename=<%=newProfileImage%>" style="width: 36px;"/>
							<% } %>
						</div>
						<div class="side_menu_content_profile_name">
							<div><%=nickname%></div>
							<span><%=channelName%><span>에추 가함</span></span>
						</div>
					</div>
				</div>
			</div>
	<%
			}
		}
	%>
		</div>
	</div>
	<div id="myactivity_content" class="fl">
		<img class="p-empty_page__illustration__img" alt="@ 상징 이미지" src="https://a.slack-edge.com/bv1-13-br/empty-activity-light-3165716.svg">
	</div>
	
</body>
</html>