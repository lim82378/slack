<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- 메시지 있으면 출력 --%>
<c:set var="msg" value="${msg}"/>
<c:choose>
	<c:when test="${not empty msg}">			
		<script>alert("${msg}");</script>
	</c:when>
</c:choose>
<%-- //메시지 있으면 출력 --%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>AIslack</title>
	<link rel="stylesheet" href="resources/css/LoginForm.css"/>
	<link rel="icon" href="resources/img/slackLogo.png"/>
	<script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
	<script src="resources/js/LoginAndCreateId.js"></script>
</head>
<body>
	<div style="color: red; text-align: center;">
		<span style="color: red;">
			<h3>test용 계정</br>
			계정 : test1~10</br>
			비밀번호 : 123456</h3></br>
			더미데이터는 test1 많습니다.
		</span>	
	</div>
	<div class="ground"><!-- 팝업끄기: style="display:none" -->
		<div class="hello">
			<span>어서오세요! </span>
			<span>다시 돌아온걸 환영해요! </span>
		</div>
		<form action="login_check" method="post">
			<input type="hidden" name="workspace_idx" value="1"/>
			<div class="email">
				<span>이메일</span>
				<span>*</span>
				<div class="email_bar pink"><!-- "pink"를 추가하면 아래 <span> 표시됨 -->
					<input id="login_id_text" type="text" name="id"/>
				</div>
			</div>
			<div class="password">
				<span>비밀번호</span>
				<span>*</span>
				<div class="password_bar pink">
					<input id="login_pw_text" type="password" name="pw"/>
				</div>
			</div>
			<div class="login">
				<input id="login_btn" type="submit" value="로그인" />
			</div>
			<div class="sign">
				<span>회원가입 하시겠습니까? </span>
				<a href="CreateId" id="btn_join">회원가입</a>
			</div>
		</form>
	</div> 
</body>
</html>