<%@page import="com.slack.dao.*"%>
<%@page import="com.slack.dto.*"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<%
	List<FileWorkspaceUsersDto> list1 = (List<FileWorkspaceUsersDto>)request.getAttribute("fileList");
	String loginId = (String)session.getAttribute("userId");
	List<SearchHistoryDto> historyList = (List<SearchHistoryDto>) request.getAttribute("historyList");
%>
<html>
<head>
	<link rel="icon" href="resources/img/slackLogo.png"/>
<script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100;300;400;500;700;900&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="resources/css/file.css"/>
	<meta charset="UTF-8">
<title>파일</title>
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
			$("#search_file_input").on('keyup', function(e){
				if(e.key === 'Enter'){
					let keyword = $(this).val();
					searchFiles(keyword);
				}
			});
			$("#file_main_orderby_btn").click(function(){
				if ($(".file_five_style").css("display")=="none") {
				$(".file_five_style").css("display","block"); 
				}else {
					$(".file_five_style").css("display","none");//디스플레이 넌일때 누르면 블락, 아니면 넌 
				}
				$("#clean_filter").css("display","block")
			});
			$("#clean_filter").click(function(){
				$(".file_five_style").css("display","none");   // 팝업창 띄운 후에 공백화면 누를시 창 닫아주는 역할
				$("#clean_filter").css("display","none");
			});
			$(".file_filter_popup_list").click(function(){
				let txt = $(this).text().trim(); // 해당 요소 자식의 컨텐트 (텍스트) 를 가지고오는것. 트림으로 띄어쓰기?삭제해줌
				$(".file_filter_popup_date > div").text(txt); // 가져온 txt를 날짜 > <div>기간</div> 안에 div에 넣어줌
				$(".file_filter_popup_list_date_container").css("display","none"); // 넣어주고나면 소팝업창은 디스플레이 넌 
			});
			$(".file_filter_popup_date > div").click(function(){ // 날짜 > <div>기간</div> 을 클릭하면 
				if($(".file_filter_popup_list_date_container").css("display")=="none"){   // 날짜의 인풋처럼 생긴 div를 누르면 컨테이너가 넌이면 블락, 블락이면 넌 
					$(".file_filter_popup_list_date_container").css("display","block");
				}else {
					$(".file_filter_popup_list_date_container").css("display","none");
				}                                                                      
				$(".file_filter_popup_list > svg").remove(); // 날짜 한 줄 div 안에 체크 svg 를 삭제 
				$(".file_filter_popup_list > span").removeClass("on"); // 기본 체크박스 상태인 < v 모든기간>의 클래스 온을 삭제
				
				let svgCheck = '<svg style="width: 16px; height: 16px; margin-left: 2px; position: absolute; top: 8px; left: 5px; "data-i0m="true" data-qa="menu_item_checkmark" aria-hidden="true" viewBox="0 0 20 20" class="is-inline" style="--s: 16px;"><path fill="currentColor" fill-rule="evenodd" d="M17.234 3.677a.75.75 0 0 1 .089 1.057l-9.72 11.5a.75.75 0 0 1-1.19-.058L2.633 10.7a.75.75 0 0 1 1.234-.852l3.223 4.669 9.087-10.751a.75.75 0 0 1 1.057-.089" clip-rule="evenodd"></path></svg>';
				// svgCheck 라는 변수에다가 체크모양 svg 넣어줌
				let txtSelected = $(".file_filter_popup_date > div").text().trim();
				// txtSelected 라는 변수에다가 내가 클릭한 기간 한줄 div 에 텍스트 ( 모든기간, 7일 전 등등 )을 넣어줌
				$(".file_filter_popup_list > span").each(function(idx, item){ // span태그에 반복문
					if($(item).text() != txtSelected) return; // 내가 클릭한 텍스트가 각각의 반복문에 적용된 텍스트랑 다르면 그냥 리턴  
					$(item).parent().prepend(svgCheck); // 요소의 부모(리스트 div)에 prepend(맨앞)에 svg 추가 
					$(item).addClass("on");// 그 요소에 클래스 on 추가      
				});
			})
			$(".filter_next_btn").click(function(){
				$("#file_filter_popup_container").css("display","none");
				$("#file_black_filter").css("display","none");
			});
			$(".x_btn").click(function(){
				$(this).closest(".popup_window").css("display","none");
				$("#file_black_filter").css("display","none");
			});
			$(".file_filter_btn").click(function(){ // 필터 버튼 누르면 팝업창 디스플레이 블락 
				$("#file_filter_popup_container").css("display","block");
				$("#file_black_filter").css("display","block");
			});
			$(".trash_can").click(function(){
				if(confirm("정말로 삭제하시겠습니까?")){
					let fileIdx = $(this).data("file_idx");
					$.ajax({
						type:'post',
						url: 'file/delete',
						data: JSON.stringify({
							"fileIdx" : fileIdx,
						}),
						contentType: "application/json; charset=utf-8",
						dataType: "json",
						success: function(data){
							location.href = "file?file_idx=" + fileIdx+"&workspace_idx="+globalWorkspaceIdx;
						},
						error: function(request, status, error){
							alert("[에러] code : " + request.status
									+ "/nmessage:" + request.responseText
									+ "/nerror:" + error);
						}
					});
				}
			});
	 	});
	 	function searchFiles(keyword){
	 		alert("호출");
	 		alert(globalWorkspaceIdx);
			$.ajax({
				type:'post',
				url: 'file/search',
				data: JSON.stringify({
					"keyword" : keyword,
					"workspaceIdx" : globalWorkspaceIdx
				}),
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				success: function(data){
					console.log(data);
					/* alert("ajax success!!"); */
					let list = data.searchList;
					useSearchList(list);
				},
				error: function(request, status, error){
					alert("[에러] code : " + request.status
							+ "/nmessage:" + request.response.responseText
							+ "/nerror:" + error);
				}
			});
		}
	 	function useSearchList(list){
			let $searchFileList = $(".file_main_list_container");
			
			$searchFileList.parent().empty();
			if(list.length == 0){
				$searchFileList.parent().append('<div>검색결과가 없습니다.</div>');
				return
			}
			let html = "";
			for(let i = 0; i < list.length; i++){
				let item = list[i];
				html += "<div class=\"file_main_list_container\">"
				html += "<div>";
		        html += "    <div class='fl'>";
		        html += "        <img src='resources/img/slackLogo.png'/>";
		        html += "    </div>";
		        html += "    <div class='fl'>";
		        html += "        <span>" + item.fileName + "</span>"; 
		        html += "        <span>" + item.nickname + " · " + item.sentTime + "</span>";
		        html += "    </div>";
		        html += "    <div class='fr'>";
		        html += "        <div class='trash_can fr' data-file_idx='" + item.fileIdx + "'>";
		        html += "            <svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 512 512' id='trash-can'><path fill=''#231f20' d='M309.36 428.79H202.64c-19.9 0-36.51-14.3-37.82-32.56l-14.39-200.45a8 8 0 0 1 16-1.15l14.39 200.45c.71 9.93 10.31 17.71 21.86 17.71h106.68c11.55 0 21.15-7.78 21.86-17.7l14.39-200.46a8 8 0 0 1 16 1.15l-14.43 200.45c-1.31 18.26-17.92 32.56-37.82 32.56Z'></path><path fill='#231f20' d='M371.28 203.2h-17.69a8 8 0 0 1 0-16h17.69c3.19 0 6.76-4.88 6.76-11.41V158c0-6.53-3.57-11.41-6.76-11.41H140.72c-3.19 0-6.76 4.88-6.76 11.41v17.76c0 6.53 3.57 11.41 6.76 11.41h17.69a8 8 0 1 1 0 16h-17.69c-12.76 0-22.76-12-22.76-27.41V158c0-15.37 10-27.41 22.76-27.41h230.56c12.76 0 22.76 12 22.76 27.41v17.76c-.04 15.4-10.04 27.44-22.76 27.44Z'></path><path fill='#231f20' d='M295.76 146.62h-80.29a8 8 0 0 1-8-8v-45.7a9.73 9.73 0 0 1 9.72-9.71H294a9.73 9.73 0 0 1 9.72 9.71v45.7a8 8 0 0 1-7.96 8zm-72.29-16h64.29V99.21h-64.29zM304.26 196h-97.91a8 8 0 0 1 0-16h97.91a8 8 0 0 1 0 16zM256 378.71a8 8 0 0 1-8-8V253a8 8 0 0 1 16 0v117.71a8 8 0 0 1-8 8zm-40.54 0a8 8 0 0 1-7.94-7.1l-13.31-117.77a8 8 0 0 1 15.9-1.79l13.31 117.76a8 8 0 0 1-7 8.85c-.35.03-.66.05-.96.05zm80.54 0h-.91a8 8 0 0 1-7.05-8.85l13.31-117.76a8 8 0 0 1 15.9 1.79L304 371.61a8 8 0 0 1-8 7.1z'></path></svg>"; 
		        html += "        </div>";
		        html += "    </div>";
		        html += "    <div style='clear:both;'></div>";
		        html += "</div>";
		        html += "</div>";
			}
			$(".file_main_list").append(html);
 		}
 	</script>
</head>
<body>
<jsp:include page="searchAll.jsp" />
<div id="file_black_filter" style="display:none;"></div>
<div id="clean_filter" style="display:none;"></div>
<div id="file_header" class="search_btn">
		<div>
			<h3 class="test_text" style="color: red;">전체 검색-></h3>
			<svg data-i0m="true" data-qa="search" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M9 3a6 6 0 1 0 0 12A6 6 0 0 0 9 3M1.5 9a7.5 7.5 0 1 1 13.307 4.746l3.473 3.474a.75.75 0 1 1-1.06 1.06l-3.473-3.473A7.5 7.5 0 0 1 1.5 9" clip-rule="evenodd"></path></svg>
			<span>전체검색</span>
		</div>
	</div>
	<div id="file_sidebar" class="fl">
		<!-- B2 -->
		<div class="workspace_list_btn"><img src="resources/img/slackLogo.png" style="width: 36px;"/></div>
		<!-- 홈 -->
		<div>
			<div class="move_home_btn">
				<svg data-i0m="true" data-qa="home" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M10.14 3.001a.25.25 0 0 0-.28 0L4.5 6.631v8.12A2.25 2.25 0 0 0 6.75 17h6.5a2.25 2.25 0 0 0 2.25-2.25V6.63zm-7.47 4.87L3 7.648v7.102a3.75 3.75 0 0 0 3.75 3.75h6.5A3.75 3.75 0 0 0 17 14.75V7.648l.33.223a.75.75 0 0 0 .84-1.242l-7.189-4.87a1.75 1.75 0 0 0-1.962 0l-7.19 4.87a.75.75 0 1 0 .842 1.242m9.33 2.13a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1h1a1 1 0 0 0 1-1v-1a1 1 0 0 0-1-1z" clip-rule="evenodd"></path></svg>
			</div>
			<div>홈</div>
		</div>
		<!-- DM -->
		<div>
			<div class="move_dm_btn">
				<svg data-i0m="true" data-qa="direct-messages" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M7.675 6.468a4.75 4.75 0 1 1 8.807 3.441.75.75 0 0 0-.067.489l.379 1.896-1.896-.38a.75.75 0 0 0-.489.068 5 5 0 0 1-.648.273.75.75 0 1 0 .478 1.422q.314-.105.611-.242l2.753.55a.75.75 0 0 0 .882-.882l-.55-2.753A6.25 6.25 0 1 0 6.23 6.064a.75.75 0 1 0 1.445.404M6.5 8.5a5 5 0 0 0-4.57 7.03l-.415 2.073a.75.75 0 0 0 .882.882l2.074-.414A5 5 0 1 0 6.5 8.5m-3.5 5a3.5 3.5 0 1 1 1.91 3.119.75.75 0 0 0-.49-.068l-1.214.243.243-1.215a.75.75 0 0 0-.068-.488A3.5 3.5 0 0 1 3 13.5" clip-rule="evenodd"></path></svg>
			</div>
			<div>DM</div>
		</div>
		<!-- 내 활동 -->
		<div>
			<div class="move_myactivity_btn">
				<svg data-i0m="true" data-qa="notifications" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M9.357 3.256c-.157.177-.31.504-.36 1.062l-.05.558-.55.11c-1.024.204-1.691.71-2.145 1.662-.485 1.016-.736 2.566-.752 4.857l-.002.307-.217.217-2.07 2.077c-.145.164-.193.293-.206.374a.3.3 0 0 0 .034.199c.069.12.304.321.804.321h4.665l.07.672c.034.327.17.668.4.915.214.232.536.413 1.036.413.486 0 .802-.178 1.013-.41.227-.247.362-.588.396-.916l.069-.674h4.663c.5 0 .735-.202.804-.321a.3.3 0 0 0 .034-.199c-.013-.08-.061-.21-.207-.374l-2.068-2.077-.216-.217-.002-.307c-.015-2.291-.265-3.841-.75-4.857-.455-.952-1.123-1.458-2.147-1.663l-.549-.11-.05-.557c-.052-.558-.204-.885-.36-1.062C10.503 3.1 10.31 3 10 3s-.505.1-.643.256m-1.124-.994C8.689 1.746 9.311 1.5 10 1.5s1.31.246 1.767.762c.331.374.54.85.65 1.383 1.21.369 2.104 1.136 2.686 2.357.604 1.266.859 2.989.894 5.185l1.866 1.874.012.012.011.013c.636.7.806 1.59.372 2.342-.406.705-1.223 1.072-2.103 1.072H12.77c-.128.39-.336.775-.638 1.104-.493.538-1.208.896-2.12.896-.917 0-1.638-.356-2.136-.893A3 3 0 0 1 7.23 16.5H3.843c-.88 0-1.697-.367-2.104-1.072-.433-.752-.263-1.642.373-2.342l.011-.013.012-.012 1.869-1.874c.035-2.196.29-3.919.894-5.185.582-1.22 1.475-1.988 2.684-2.357.112-.533.32-1.009.651-1.383" clip-rule="evenodd"></path></svg>
			</div>
			<div>내 활동</div>
		</div>
		<!-- 파일 -->
		<div  class="on">
			<div class="move_file_btn">
				<svg data-i0m="true" data-qa="canvas-browser-filled" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M3 4.836C3 3.822 3.822 3 4.836 3h7.328c.9 0 1.647.646 1.805 1.5H7.836A3.336 3.336 0 0 0 4.5 7.836v6.133A1.84 1.84 0 0 1 3 12.164zm1.515 10.649A3.337 3.337 0 0 1 1.5 12.164V4.836A3.336 3.336 0 0 1 4.836 1.5h7.328a3.337 3.337 0 0 1 3.32 3.015A3.337 3.337 0 0 1 18.5 7.836v3.968c0 .73-.29 1.43-.805 1.945l-3.946 3.946a2.75 2.75 0 0 1-1.945.805H7.836a3.337 3.337 0 0 1-3.32-3.015M11.5 13a1.5 1.5 0 0 1 1.5-1.5h2.75a.75.75 0 0 1 0 1.5H13v2.75a.75.75 0 0 1-1.5 0z" clip-rule="evenodd"></path></svg>
			</div>
			<div>파일</div>
		</div>
	</div> <!-- 사이드바 끝 -->
	<div id="file_side_menu" class="fl">
		<div>
			<span>파일</span>
		</div> 
	</div>
	<div id="file_main_content" class="fl">
		<div>
			<div>모든 파일</div>
		</div>
		<div class="file_main_input">
			<div>
				<svg data-i0m="true" data-qa="search" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M9 3a6 6 0 1 0 0 12A6 6 0 0 0 9 3M1.5 9a7.5 7.5 0 1 1 13.307 4.746l3.473 3.474a.75.75 0 1 1-1.06 1.06l-3.473-3.473A7.5 7.5 0 0 1 1.5 9" clip-rule="evenodd"></path></svg>
				<input type="text" id="search_file_input" placeholder="파일 검색"/>
			</div>
		</div>
		<div class="file_main_orderby">
			<div style="clear:both;"></div>	
		</div>
		<div class="file_main_list">
<%
		if(list1 != null) {		
		for(FileWorkspaceUsersDto dto : list1){
			String uploadTime = dto.getUploadTime();
			int month = Integer.parseInt(uploadTime.substring(5, 7));
			int day = Integer.parseInt(uploadTime.substring(8, 10));
			uploadTime = month + "월 " + day + "일";
%>
			<div class="file_main_list_container">
				<div>
					<div class="fl">
						<img src="resources/img/slackLogo.png"/>
					</div>
					<div class="fl">
						<span><%=dto.getFileName()%></span>
						<span><%=dto.getNickname()%> · <%=uploadTime%></span>
					</div>
					<div class="fr">
						<div class="trash_can fr"data-file_idx ="<%=dto.getFileIdx()%>">
							<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" id="trash-can"><path fill="#231f20" d="M309.36 428.79H202.64c-19.9 0-36.51-14.3-37.82-32.56l-14.39-200.45a8 8 0 0 1 16-1.15l14.39 200.45c.71 9.93 10.31 17.71 21.86 17.71h106.68c11.55 0 21.15-7.78 21.86-17.7l14.39-200.46a8 8 0 0 1 16 1.15l-14.43 200.45c-1.31 18.26-17.92 32.56-37.82 32.56Z"></path><path fill="#231f20" d="M371.28 203.2h-17.69a8 8 0 0 1 0-16h17.69c3.19 0 6.76-4.88 6.76-11.41V158c0-6.53-3.57-11.41-6.76-11.41H140.72c-3.19 0-6.76 4.88-6.76 11.41v17.76c0 6.53 3.57 11.41 6.76 11.41h17.69a8 8 0 1 1 0 16h-17.69c-12.76 0-22.76-12-22.76-27.41V158c0-15.37 10-27.41 22.76-27.41h230.56c12.76 0 22.76 12 22.76 27.41v17.76c-.04 15.4-10.04 27.44-22.76 27.44Z"></path><path fill="#231f20" d="M295.76 146.62h-80.29a8 8 0 0 1-8-8v-45.7a9.73 9.73 0 0 1 9.72-9.71H294a9.73 9.73 0 0 1 9.72 9.71v45.7a8 8 0 0 1-7.96 8zm-72.29-16h64.29V99.21h-64.29zM304.26 196h-97.91a8 8 0 0 1 0-16h97.91a8 8 0 0 1 0 16zM256 378.71a8 8 0 0 1-8-8V253a8 8 0 0 1 16 0v117.71a8 8 0 0 1-8 8zm-40.54 0a8 8 0 0 1-7.94-7.1l-13.31-117.77a8 8 0 0 1 15.9-1.79l13.31 117.76a8 8 0 0 1-7 8.85c-.35.03-.66.05-.96.05zm80.54 0h-.91a8 8 0 0 1-7.05-8.85l13.31-117.76a8 8 0 0 1 15.9 1.79L304 371.61a8 8 0 0 1-8 7.1z"></path></svg>
						</div>
					</div>
					<div style="clear:both;"></div>
				</div>
			</div>
			<%}  
			}%>
		</div>
	</div>
	<div style="clear:both;"></div>
</body>
</html>