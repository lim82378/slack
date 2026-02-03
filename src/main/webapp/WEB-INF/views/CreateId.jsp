<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>slack1</title>
	<script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
	<link rel="stylesheet" href="resources/css/CreateIdForm.css"/>
	<link rel="icon" href="resources/img/slackLogo.png"/>
	<script src="resources/js/LoginAndCreateId.js"></script>
</head>
<body>
	<div class="sign_up_box">
		<div class="sign_up">
			<span> 계정만들기 </span>
		</div>
		<div class="email">
			<span>이메일</span>
			<span>*</span>
			<div class="email_bar pink">
				<input id="email_text" type="email" required/>
			</div>
			<span class="pink" style="display:none;">
				<img src="resources/img/bang.png"/>
				<span>필수요건.</span>
			</span>
		</div>
		<div class="nickname">
			<span>별명</span>
			<div class="nickname_bar pink">
				<input id="nickname_text" type="text" required/>
			</div>
			<span class="pink" style="display:none;">
				<span>다른 회원에게 표기되는 이름이에요.</span>
			</span>
		</div>
		<div class="username">
			<span>사용자명</span>
			<span>*</span>
			<div class="username_bar pink">
				<input id="user_name_text" type="text" required/>
			</div>
			<span class="pink" style="display:none;">
				<span>숫자,문자,밑줄_,마침표만 사용할 수 있습니다.</span>
			</span>
		</div>
		<div class="password">
			<span>비밀번호</span>
			<span>*</span>
			<div class="password_bar pink ">
				<input id="password_text" type="password" required/>
			</div>
			<span class="pink" style="display:none;">
				<img src="resources/img/bang.png"/>
				<span>최소 8글자 이상이여야해요.</span>
			</span>
		</div>
		<div class="username">
			<span>전화번호</span>
			<span>*</span>
			<div class="username_bar pink">
				<input type="text" 
		           name="phone" 
		           id="phone_text"
		           minlength="11" 
		           maxlength="11" 
		           pattern="\d{11}" 
		           required />
			</div>
			<span class="pink" style="display:none;">
				<span>-를 제외한 11자리 숫자를 입력해주세요(010포함)</span>
			</span>
		</div>
		<div class="creat_sign_up">
			<div class="creat_sign_up1 ">
				<span> 계정만들기</span>
			</div>
		</div>
		<div class="users">
			 <a href="/slack" id="link_login">이미 계정이 있나요? 로그인하세요</a>
		</div>	
	</div>

</body>
</html>