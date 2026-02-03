<%@page import="com.slack.dto.SearchHistoryDto"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<%
	String loginId = (String)session.getAttribute("userId");
	List<SearchHistoryDto> historyList = (List<SearchHistoryDto>) request.getAttribute("historyList");
%>
<html>
<head>
	<link rel="icon" href="resources/img/slackLogo.png"/>
	<meta charset="UTF-8">
	<title>전체검색</title>
	<style>
		.header_search_history { 
			width: 688px; 
			min-height: 300px; 
			border-radius: 6px; 
			border: 1px solid lightgrey;
			background-color: white; 
			z-index: 50;
			position: absolute;
			left: 391px;
			top: 5px;
		}
		.header_search_history > div:first-child { 
			width: 688px; 
			height: 45px; 
			display: flex;
			align-items: center;
			justify-content: space-between;
			border-bottom: 1px solid lightgrey;
		}
		.header_search_history > div:first-child > div:first-child > svg:first-child {
			width: 16px;
			height: 16px;
			margin-left: 21px;
			color: rgb(97, 96, 97);
			cursor: pointer;
		}
		.header_search_history > div:first-child > div:last-child > svg:last-child { /*X 버튼*/
			width: 20px;
			height: 20px;
			color: rgb(97, 96, 97);
			margin-right: 18px;
			cursor: pointer;
		 }
		 .header_search_history > div > div:nth-child(2) {
		 	width: 504px; 
		 	height: 22px;
		 	margin-bottom: 5px;
		 }
		 .header_search_history > div input[type='text'] {
		 	width: 500px;
		 	height: 22px;
		 	border: none;
		 	outline: none;
		 	margin-left: -40px;
		 	font-size: 15px;
		 }
		 .header_search_history > div input[type='text']::placeholder {
		 	font-size: 15px;
		 	font-weight: 400;
		 }
		 .header_search_history > div:nth-child(3) { /*최근검색*/
		 	font-size: 13px;
		 	color: rgb(97, 96, 97);
		 	margin-left: 20px;
		 	margin-top: 10px;
		 	width: 688px; 
			height: 45px;
		 }
		 .header_search_history_content {
		 	width: 687px;
		 	height:45px;
		 	display: flex;
		 	align-items: center;
		 	justify-content: space-between;
		 }
		 .header_search_history_content:hover {
		 	background-color: rgb(46, 99, 158);
		 	color: white;
		 }
		 .header_search_history_content > div:first-child {
		 	margin-left: 20px;
		 }
		 .header_search_history_content > div:first-child > svg {
		 	width: 18px; 
		 	height: 18px;
		 }
		 .header_search_history_content > div:nth-child(2) {
		 	margin-left: -70px;
		 	font-weight: 600;
		 }
		 .header_search_history_content > div:last-child {
		 	margin-right: 16px;
		 }
		 .header_search_history_content > div:last-child {
		 	border-radius: 4px;
		 	display: flex;
		 	justify-content: center;
		 	align-items: center;
		 }
		 .header_search_history_content > div:last-child:hover {
		 	background-color: rgb(84, 127, 176);
		 }
		 .header_search_history_content > div:last-child > svg { /*X버튼*/
		 	width: 20px;
		 	height: 20px;
		 	cursor: pointer;
		 }
	</style>
	<script>

		$(function(){
			
			$(".search_btn > div").click(function(){
					$(".all_search_history").css("display","block");
				});
			$(document).on("click", ".history_delete_btn", function(){
	 			let searchIdx = $(this).parent().data("search_history_idx");// searchIdx가 이 버튼보다 위에 코드에 있어서 this.parent로 해줘야한다.
	 			$.ajax({
					type:'post',
					url: 'dm/searchDelete',
					data: JSON.stringify({
						"searchIdx" : searchIdx,
						"workspaceIdx" : globalWorkspaceIdx
					}),
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					success: function(data){
						//alert("삭제한 idx : " + searchIdx);
						$(".header_search_history_content").each(function(){
							if($(this).data("search_history_idx")==searchIdx) {
								$(this).remove();
								return false;
							}
						});
						$("#search_history_input").val("");
					},
					error: function(request, status, error){
						alert("[에러] code : " + request.status
								+ "/nmessage:" + request.responseText
								+ "/nerror:" + error);
					}
				});
	 		});
	 		$(document).off("keydown", "#search_history_input").on("keydown", "#search_history_input", function(e){
	 			if (e.keyCode === 229) {
	 		        return;
	 		    }
	 			if(e.key === 'Enter'){
		 			let searchKeyword = $(this).val();
		 			$.ajax({
						type:'post',
						url: 'searchAll',
						data: JSON.stringify({
							"searchKeyword" : searchKeyword,
							"workspaceIdx" : globalWorkspaceIdx
						}),
						contentType: "application/json; charset=utf-8",
						dataType: "json",
						success: function(data){
							console.log(data);   // PK값(searchHistoryIdx)이 담겨있는 거 확인.
							let searchHistoryIdx = data.searchHistoryIdx;
							$("#search_history_input").val("");
							let str = `
								<div class="header_search_history_content" data-search_history_idx="\${searchHistoryIdx}">
								<div class="fl"><svg data-i0m="true" data-qa="entity-icon" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 18px;"><path fill="currentColor" fill-rule="evenodd" d="M2.5 10a7.5 7.5 0 1 1 15 0 7.5 7.5 0 0 1-15 0M10 1a9 9 0 1 0 0 18 9 9 0 0 0 0-18m.75 4.75a.75.75 0 0 0-1.5 0v4.75c0 .414.336.75.75.75h3.75a.75.75 0 0 0 0-1.5h-3z" clip-rule="evenodd"></path></svg></div>
								<div class="fl">\${searchKeyword}</div>
								<div class="history_delete_btn fl"><svg data-i0m="true" data-qa="close" aria-hidden="true" id="c-search_autocomplete__suggestion_toolbar_button_icon-1" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg></div>
							</div>
							`;
							
							$(".header_search_history > div:nth-child(3)").after(str);
							alert(searchKeyword);
							location.href="home2?searchKeyword=" + encodeURIComponent(searchKeyword) +"&workspace_idx=" + globalWorkspaceIdx ;
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
	</script>
</head>
<body>
	<div class="all_search_history popup_window" style="display: none;">
			<!-- 전체검색 -->
			<div class="header_search_history">
				<div>
					<div class="fl">
						<svg data-i0m="true" data-qa="search" aria-hidden="true"
							viewBox="0 0 20 20" class="">
							<path fill="currentColor" fill-rule="evenodd"
								d="M9 3a6 6 0 1 0 0 12A6 6 0 0 0 9 3M1.5 9a7.5 7.5 0 1 1 13.307 4.746l3.473 3.474a.75.75 0 1 1-1.06 1.06l-3.473-3.473A7.5 7.5 0 0 1 1.5 9"
								clip-rule="evenodd"></path></svg>
					</div>
					<div class="fl">
						<input type="text" id="search_history_input"
							placeholder="사람, 채널, 파일 등에서 검색" />
					</div>
					<div class="x_btn fl">
						<svg data-i0m="true" data-qa="close" aria-hidden="true"
							viewBox="0 0 20 20" class="" style="-s: 20px;">
							<path fill="currentColor" fill-rule="evenodd"
								d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0"
								clip-rule="evenodd"></path></svg>
					</div>
				</div>
				<div style="clear: both;"></div>
				<div>최근 검색</div>
			
				<%
    if(historyList != null && !historyList.isEmpty()){
        for (SearchHistoryDto dto : historyList) {
%>
        <div class="header_search_history_content"
            data-search_history_idx="<%=dto.getSearchHistoryIdx()%>">
            <div class="fl">
                <svg data-i0m="true" data-qa="entity-icon" aria-hidden="true"
                    viewBox="0 0 20 20" class="" style="-s: 18px;">
                    <path fill="currentColor" fill-rule="evenodd"
                        d="M2.5 10a7.5 7.5 0 1 1 15 0 7.5 7.5 0 0 1-15 0M10 1a9 9 0 1 0 0 18 9 9 0 0 0 0-18m.75 4.75a.75.75 0 0 0-1.5 0v4.75c0 .414.336.75.75.75h3.75a.75.75 0 0 0 0-1.5h-3z"
                        clip-rule="evenodd"></path></svg>
            </div>
            <div class="fl"><%=dto.getSearchKeyword()%></div>
            <div class="history_delete_btn fl">
                <svg data-i0m="true" data-qa="close" aria-hidden="true"
                    id="c-search_autocomplete__suggestion_toolbar_button_icon-1"
                    viewBox="0 0 20 20" class="" style="-s: 20px;">
                    <path fill="currentColor" fill-rule="evenodd"
                        d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0"
                        clip-rule="evenodd"></path></svg>
            </div>
        </div>
<%
        }
    } else {
%>
        <div style="padding: 20px; text-align: center; color: #999;">
            최근 검색 기록이 없습니다.
        </div>
<%
    } 
%>
    </div> </div>
		
</body>
</html>