<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<% 
	String emailTo = (String)session.getAttribute("emailTo");
%>
<html>
<head>
	<link rel="icon" href="resources/img/slackLogo.png"/>
	<script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
	<meta charset="UTF-8">
	<title>초대코드화면</title>
	<link rel="stylesheet" href="resources/css/invitecode.css"/>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100;300;400;500;700;900&display=swap" rel="stylesheet">
	<script>
		$(function(){
			$('#btn_6').keyup(function(e){
				$("form").submit();
			})
		})
	</script>
</head>
<body>
	  <div id="invite_code_header">
	 	 <div>
	 		 <img alt="Slack" src="https://a.slack-edge.com/bv1-13/slack_logo-e971fd7.svg" height="26" title="Slack">
	 	 </div>
	  </div>
	  <div id="invite_code_content">
		 <h1>코드를 이메일로 보내드렸습니다</h1>
		 <div>
			 <p><strong>${emailTo}</strong> (으)로 이메일을 보냈습니다. 여기에 코드를 입력하거나 이메일에 있는 버튼을 탭하여 계속하세요.
			 </p>
			 <p>이메일이 보이지 않으면 스팸 또는 정크 폴더를 확인하세요.</p>
		</div>	
		<%--
		$("form").submit();
		 --%>
		<form action="inviteCodeAction" method="post">
			<div class="invite_code_input">
				<input type="text" maxlength="1" name="a">
				<input type="text" maxlength="1" name="b">
				<input type="text" maxlength="1" name="c">
				<div>—</div>
				<input type="text" maxlength="1" name="d">
				<input type="text" maxlength="1" name="e">
				<input type="text" id="btn_6" maxlength="1" name="f">
			</div>
		</form>
		</div>
</body>
</html>