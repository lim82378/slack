/*
 * function check() { let id = $("input[name='id']").val(); let pw =
 * $("input[name='pw']").val();
 * 
 * if(/^[a-zA-Z]*$/.test(id) == false) { alert("아이디는 영문자 와 @를 넣어서 입력하세요");
 * return false; }
 * 
 * if(/^[0-9]*$/.test(pw) == false) { alert("비밀번호는 숫자로만 입력하세요"); return false; }
 * return true; }
 */		
$(function(){ 
	$(".creat_sign_up1").click(function(){
		let userId = $("#email_text").val();
		let nickname = $("#nickname_text").val();
		let name = $("#user_name_text").val();
		let password = $("#password_text").val();
		let phone = $("#phone_text").val();
		console.log(userId + "/" + nickname + "/" + name + "/" + password + "/" + phone);
		$.ajax({
	    	type: "post", 
			url: "createId", 
			data: JSON.stringify({
				"UserId": userId,
				"Nickname": nickname,
				"Name": name,
				"Password": password,
				"Phone": phone
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) { //성공시
	    		alert("회원가입 되었습니다.");
	    		location.href="/slack";
	    	},
			error: function(request, status, error) { //실패시
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
	});
});