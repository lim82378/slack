<%@page import="com.slack.dao.*"%>
<%@page import="com.slack.dto.*"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% 
	String loginId = (String)session.getAttribute("userId");
	List<WorkspaceDto> workspaceInfoList = (List<WorkspaceDto>)request.getAttribute("workspaceInfoList");
%>
<!DOCTYPE html>
<html>
<head>
	<script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100;300;400;500;700;900&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="resources/css/workspacelist.css"/>
	<link rel="icon" href="resources/img/slackLogo.png"/>
	<meta charset="UTF-8">
<title>워크스페이스목록</title>
	<script>
		$(function(){
			$(".workspace_name").click(function(){
				let workspaceIdx = $(this).data("workspace_idx");
				location.href="homeDirectory?workspace_idx="+workspaceIdx;
			});
			$(".new_workspace_btn").click(function(){
				location.href="workspace";
			});
		});
	</script>
</head>
<body>
	<div id="workspace_list_header">
 		<div>
 	 		<img src="https://a.slack-edge.com/38f0e7c/marketing/img/nav/slack-salesforce-logo-nav-white.png" srcset="https://a.slack-edge.com/38f0e7c/marketing/img/nav/slack-salesforce-logo-nav-white.png 1x, https://a.slack-edge.com/38f0e7c/marketing/img/nav/slack-salesforce-logo-nav-white@2x.png 2x" alt="Slack" class="c-slacklogo--white">
	 	</div>
	</div>
	<div id="workspace_list_header2">
		<h1>
			<div>
				<img src="https://a.slack-edge.com/70d4c04/marketing/img/homepage/signed-in-users/waving-hand.png" srcset="https://a.slack-edge.com/70d4c04/marketing/img/homepage/signed-in-users/waving-hand.png 1x, https://a.slack-edge.com/70d4c04/marketing/img/homepage/signed-in-users/waving-hand@2x.png 2x" alt="" height="56" width="52">
			</div>
			<span>또 만나게 되어 반가워요</span>
		</h1>
	</div>
	<div id="workspace_list_main">
		<div>
			<div>
				<span><%=loginId%>의 워크스페이스</span>
			</div>
			<div class="workspace_name_list">
				<%
					for(WorkspaceDto dto : workspaceInfoList) {
				%>
				<div class="workspace_name" data-workspace_idx = "<%= dto.getWorkspaceIdx()%>">
					<div>
						<div class="fl">
							<img class="ss-c-workspace-detail__icon" src="https://a.slack-edge.com/80588/img/avatars-teams/ava_0024-88.png" alt="" height="75" width="75">
						</div>
						<div class="name_content fl">
							<div>
								<%=dto.getWorkspaceName() %>
							</div>	
						</div>
						<div class="start_slack fr">
							<button class="select_workspace_btn_first">
								<span>									
									SLACK 실행하기
								</span>
							</button>
						</div>		
					</div>
				</div>
				<% } %>
			</div>
		</div>
	</div>
	<div class="make_new_slack">
		<div class="fl">
			<img class="ss-c-workspaces__create-image" src="https://a.slack-edge.com/0eacde0/marketing/img/homepage/signed-in-users/create-new-workspace-module/woman-with-laptop-color-background-new.png" srcset="https://a.slack-edge.com/0eacde0/marketing/img/homepage/signed-in-users/create-new-workspace-module/woman-with-laptop-color-background-new.png 1x, https://a.slack-edge.com/0eacde0/marketing/img/homepage/signed-in-users/create-new-workspace-module/woman-with-laptop-color-background-new@2x.png 2x" alt="" height="121" width="200">
		</div>
		<div style="clear:both;"></div>
		<span>다른 팀과 slack을 사용하고 싶으세요?</span>
		<button class="new_workspace_btn fr">
			새 워크스페이스 개설
		</button>	  
	</div>
</body>
</html>