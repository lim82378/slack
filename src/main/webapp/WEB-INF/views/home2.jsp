<%@page import="com.slack.dao.*"%>
<%@page import="com.slack.dto.*"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<%
List<SearchHistoryDto> historyList = (List<SearchHistoryDto>) request.getAttribute("historyList");
List<SearchDmChannelDto> allSearchList = (List<SearchDmChannelDto>) request.getAttribute("allSearchList");
System.out.println("allSearchList사이즈는 : " + allSearchList.size());
String loginId = (String)session.getAttribute("userId");

%>
<html>
<head>
	<link rel="icon" href="resources/img/slackLogo.png"/>
	<script src="https://code.jquery.com/jquery-3.7.1.js"
		integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4="
		crossorigin="anonymous"></script>
	<link rel="stylesheet" href="resources/css/home2.css" />
	<link
		href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100;300;400;500;700;900&display=swap"
		rel="stylesheet">
	<meta charset="UTF-8">
	<title>홈2 전체검색</title>
	<script>
		const globalWorkspaceIdx = <%=request.getParameter("workspace_idx")%>;
		const loginId = "<%= loginId %>";
	</script>
<script>
	 	$(function(){
	 		$(".search_all_btn").click(function(){
	 			$(".header_search_history").css("display","block");
	 		});
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
			$(".workspace_list_btn").click(function(){
				location.href="workspaceList";
			});$(".search_all_btn").click(function(){
				$(".all_search_history").css("display","block");
				$("#home2_clear_filter").css("display","block");
			});
			$(".x_btn").click(function(){
				$(this).closest(".popup_window").css("display","none"); // 블랙필터는 왜 안꺼지는지
				$("#home2_black_filter").css("display","none");
				$("#home2_clear_filter").css("display","none");
			});
			$(".home2_filter_btn").click(function(){
				$("#home2_black_filter").css("display","block");
				$("#home2_popup_filter").css("display","block");
			});
			$(".home2_popup_filter_list_date").click(function(){
				if($(".home2_popup_filter_list_date_choice").css("display")=="none") {
					$(".home2_popup_filter_list_date_choice").css("display","block");
				}else {
					$(".home2_popup_filter_list_date_choice").css("display","none");
				}
				$("#home2_clear_filter").css("display","block");
				$(".choice_from").css("display","none");
				$(".choice_to").css("display","none");
				$(".choice_who").css("display","none");
			});
			$(".from").click(function(){
				if($(".choice_from").css("display")=="none") {
					$(".choice_from").css("display","block");
				}else {
					$(".choice_from").css("display","none");
				}
				$("#home2_clear_filter").css("display","block");
				$(".choice_who").css("display","none");
				$(".choice_to").css("display","none");
				$(".home2_popup_filter_list_date_choice").css("display","none");
			});
			$(".to").click(function(){
				if($(".choice_to").css("display")=="none") {
					$(".choice_to").css("display","block");
				}else {
					$(".choice_to").css("display","none");
				}
				$("#home2_clear_filter").css("display","block");
				$(".choice_from").css("display","none");
				$(".choice_who").css("display","none");
				$(".home2_popup_filter_list_date_choice").css("display","none");
			});
			$(".who").click(function(){
				if($(".choice_who").css("display")=="none") {
					$(".choice_who").css("display","block");
				}else {
					$(".choice_who").css("display","none");
				}
				$("#home2_clear_filter").css("display","block");
				$(".choice_from").css("display","none");
				$(".choice_to").css("display","none");
				$(".home2_popup_filter_list_date_choice").css("display","none");
			});
			$("#home2_clear_filter").click(function(){ // 팝업 켜져있을 때 바탕화면 클릭하면 꺼지게 하는 버튼
				$("#home2_clear_filter").css("display","none");
				$(".all_search_history").css("display","none");
				$(".choice_who").css("display","none");
				$(".choice_to").css("display","none");
				$(".choice_from").css("display","none");
				$(".home2_popup_filter_list_date_choice").css("display","none");
			});
			$(".home2_popup_choice_container").click(function(){
				let txt = $(this).text().trim();
				$(".home2_popup_filter_list_date > div").text(txt);
				$(".home2_popup_filter_list_date_choice").css("display","none");
			});
			$(".home2_popup_filter_list_date > div").click(function(){   //svg랑 on했을 때 컬러 변경 안됨. 질문하기 
				$(".home2_popup_choice_container > svg").remove();
				$(".home2_popup_choice_container > span").removeClass("on");
				let svgCheck = '<svg style="width: 16px; height: 16px; position: absolute; "data-xdq="true" data-qa="menu_item_checkmark" aria-hidden="true" viewBox="0 0 20 20" class="is-inline"><path fill="currentColor" fill-rule="evenodd" d="M17.234 3.677a.75.75 0 0 1 .089 1.057l-9.72 11.5a.75.75 0 0 1-1.19-.058L2.633 10.7a.75.75 0 0 1 1.234-.852l3.223 4.669 9.087-10.751a.75.75 0 0 1 1.057-.089" clip-rule="evenodd"></path></svg>';
				let txtSelected = $("home2_popup_choice_container").text().trim();
				$(".home2_popup_choice_container > span").each(function(idx, item){
					if($(item).text() != txtSelected ) return;
					$(item).parent().prepend(svgCheck);
					$(item).addClass("on");
				});
			});
	 	});
 	</script>
</head>
<body>
	<jsp:include page="searchAll.jsp" />
	<div id="home2_clear_filter" style="display: none;"></div>
	<div id="header" class="search_btn">
		<!-- 클린필터 만들어서 바깥 클릭 시 다 팝업 꺼지는거 해줘야함 -->
		<div class="search_all_btn">
			<svg data-i0m="true" data-qa="search" aria-hidden="true"
				viewBox="0 0 20 20" class="">
				<path fill="currentColor" fill-rule="evenodd"
					d="M9 3a6 6 0 1 0 0 12A6 6 0 0 0 9 3M1.5 9a7.5 7.5 0 1 1 13.307 4.746l3.473 3.474a.75.75 0 1 1-1.06 1.06l-3.473-3.473A7.5 7.5 0 0 1 1.5 9"
					clip-rule="evenodd"></path></svg>
			<span>전체검색</span>
		</div>
	</div>
	<div id="home2_sidebar" class="fl">
		<div class="workspace_list_btn">B2</div>
		<div>
			<div class="move_home_btn">
				<svg data-i0m="true" data-qa="home" aria-hidden="true"
					viewBox="0 0 20 20" class="" style="-s: 20px;">
					<path fill="currentColor" fill-rule="evenodd"
						d="M10.14 3.001a.25.25 0 0 0-.28 0L4.5 6.631v8.12A2.25 2.25 0 0 0 6.75 17h6.5a2.25 2.25 0 0 0 2.25-2.25V6.63zm-7.47 4.87L3 7.648v7.102a3.75 3.75 0 0 0 3.75 3.75h6.5A3.75 3.75 0 0 0 17 14.75V7.648l.33.223a.75.75 0 0 0 .84-1.242l-7.189-4.87a1.75 1.75 0 0 0-1.962 0l-7.19 4.87a.75.75 0 1 0 .842 1.242m9.33 2.13a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1h1a1 1 0 0 0 1-1v-1a1 1 0 0 0-1-1z"
						clip-rule="evenodd"></path></svg>
			</div>
			<div>홈</div>
		</div>
		<div>
			<div class="move_dm_btn">
				<svg data-i0m="true" data-qa="direct-messages" aria-hidden="true"
					viewBox="0 0 20 20" class="" style="-s: 20px;">
					<path fill="currentColor" fill-rule="evenodd"
						d="M7.675 6.468a4.75 4.75 0 1 1 8.807 3.441.75.75 0 0 0-.067.489l.379 1.896-1.896-.38a.75.75 0 0 0-.489.068 5 5 0 0 1-.648.273.75.75 0 1 0 .478 1.422q.314-.105.611-.242l2.753.55a.75.75 0 0 0 .882-.882l-.55-2.753A6.25 6.25 0 1 0 6.23 6.064a.75.75 0 1 0 1.445.404M6.5 8.5a5 5 0 0 0-4.57 7.03l-.415 2.073a.75.75 0 0 0 .882.882l2.074-.414A5 5 0 1 0 6.5 8.5m-3.5 5a3.5 3.5 0 1 1 1.91 3.119.75.75 0 0 0-.49-.068l-1.214.243.243-1.215a.75.75 0 0 0-.068-.488A3.5 3.5 0 0 1 3 13.5"
						clip-rule="evenodd"></path></svg>
			</div>
			<div>DM</div>
		</div>
		<div>
			<div class="move_myactivity_btn">
				<svg data-i0m="true" data-qa="notifications" aria-hidden="true"
					viewBox="0 0 20 20" class="" style="-s: 20px;">
					<path fill="currentColor" fill-rule="evenodd"
						d="M9.357 3.256c-.157.177-.31.504-.36 1.062l-.05.558-.55.11c-1.024.204-1.691.71-2.145 1.662-.485 1.016-.736 2.566-.752 4.857l-.002.307-.217.217-2.07 2.077c-.145.164-.193.293-.206.374a.3.3 0 0 0 .034.199c.069.12.304.321.804.321h4.665l.07.672c.034.327.17.668.4.915.214.232.536.413 1.036.413.486 0 .802-.178 1.013-.41.227-.247.362-.588.396-.916l.069-.674h4.663c.5 0 .735-.202.804-.321a.3.3 0 0 0 .034-.199c-.013-.08-.061-.21-.207-.374l-2.068-2.077-.216-.217-.002-.307c-.015-2.291-.265-3.841-.75-4.857-.455-.952-1.123-1.458-2.147-1.663l-.549-.11-.05-.557c-.052-.558-.204-.885-.36-1.062C10.503 3.1 10.31 3 10 3s-.505.1-.643.256m-1.124-.994C8.689 1.746 9.311 1.5 10 1.5s1.31.246 1.767.762c.331.374.54.85.65 1.383 1.21.369 2.104 1.136 2.686 2.357.604 1.266.859 2.989.894 5.185l1.866 1.874.012.012.011.013c.636.7.806 1.59.372 2.342-.406.705-1.223 1.072-2.103 1.072H12.77c-.128.39-.336.775-.638 1.104-.493.538-1.208.896-2.12.896-.917 0-1.638-.356-2.136-.893A3 3 0 0 1 7.23 16.5H3.843c-.88 0-1.697-.367-2.104-1.072-.433-.752-.263-1.642.373-2.342l.011-.013.012-.012 1.869-1.874c.035-2.196.29-3.919.894-5.185.582-1.22 1.475-1.988 2.684-2.357.112-.533.32-1.009.651-1.383"
						clip-rule="evenodd"></path></svg>
			</div>
			<div>내 활동</div>
		</div>
		<div>
			<div class="move_file_btn">
				<svg data-i0m="true" data-qa="canvas-browser" aria-hidden="true"
					viewBox="0 0 20 20" class="" style="-s: 20px;">
					<path fill="currentColor" fill-rule="evenodd"
						d="M4.836 3A1.836 1.836 0 0 0 3 4.836v7.328c0 .9.646 1.647 1.5 1.805V7.836A3.336 3.336 0 0 1 7.836 4.5h6.133A1.84 1.84 0 0 0 12.164 3zM1.5 12.164a3.337 3.337 0 0 0 3.015 3.32A3.337 3.337 0 0 0 7.836 18.5h3.968c.73 0 1.43-.29 1.945-.805l3.946-3.946a2.75 2.75 0 0 0 .805-1.945V7.836a3.337 3.337 0 0 0-3.015-3.32A3.337 3.337 0 0 0 12.164 1.5H4.836A3.336 3.336 0 0 0 1.5 4.836zM7.836 6A1.836 1.836 0 0 0 6 7.836v7.328C6 16.178 6.822 17 7.836 17H11.5v-4a1.5 1.5 0 0 1 1.5-1.5h4V7.836A1.836 1.836 0 0 0 15.164 6zm8.486 7H13v3.322z"
						clip-rule="evenodd"></path></svg>
			</div>
			<div>파일</div>
		</div>
	</div>
	<div class="all_search_main_content fl">
		<div>
			<div>
				<span>Search: "${searchKeyword}"
				</span>
			</div>
		</div>
		<div>
			검색결과 : <%=allSearchList.size()%>건
		</div>
		<div class="all_search_main_content_list">
		<%
		if (allSearchList != null) {
			for (SearchDmChannelDto dto : allSearchList) {
				String sentTime = dto.getSentTime();
				int month = Integer.parseInt(sentTime.substring(5, 7));
				int day = Integer.parseInt(sentTime.substring(8, 10));
				sentTime = month + "월 " + day + "일";
		%>
			<div>
				<div>
					<div class="fl">
						<img src="https://ca.slack-edge.com/T09J85U0SLV-U09K5T3DDFS-g5d848ae856f-48" srcset="https://ca.slack-edge.com/T09J85U0SLV-U09K5T3DDFS-g5d848ae856f-72 2x" class="c-base_icon c-base_icon--image" aria-hidden="true" role="img" alt="" style="width: 36px;">
					</div>
					<div class="fl">
						<div><%=dto.getNickname()%></div>
						<div><%=dto.getContent()%></div>
					</div>
					<div class="fr"><%=sentTime%></div>
				</div>
			</div>
			<%
				}
			}
			%>
		</div>
	</div>
</body>
</html>