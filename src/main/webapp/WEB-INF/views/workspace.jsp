<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    
<% String loginId = (String)session.getAttribute("userId"); %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>팀 이름 설정 -새 워크스페이스-</title>
<link rel="icon" href="resources/img/slackLogo.png"/>
<link rel="stylesheet" href="resources/css/workspace.css"/>
<script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100;300;400;500;700;900&display=swap" rel="stylesheet">
	<script>
		const loginId = "<%= loginId %>";
		let uploadFileName = "";
		let workspaceIdx; // 전역변수.
		$(function(){
			$("#file_upload_btn").click(function(){
				$("#real_upload_btn").click();
			});
			$("#real_upload_btn").on('change',function(){
				let file = this.files[0];
				
			    if(file){
			    	let formData = new FormData();
		            formData.append("file", file);
		            formData.append("workspaceIdx", workspaceIdx);
		            
				    $.ajax({
				        url: 'upload', // 파일을 받을 컨트롤러 주소
				        type: 'POST',
				        data: formData,
				        processData: false, 
				        contentType: false, 
				        success: function(response) {
				            alert("사진 업로드를 완료했습니다.");
				        }
			    	});
			    }    
			});
			$(".workspace_first_btn").click(function(){ 
				let workspaceName = $('#workspace_name_input').val();
				$.ajax({
					type:'post',
					url: 'workspace/data',
					data: JSON.stringify({
						"workspaceName" : workspaceName,
						"UserId": loginId
					}),
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					success: function(data){
						$("#workspace_content").css("display","none");
						$("#workspace_content_profile").css("display","block");
						workspaceIdx = data.workspaceIdx;
					},
					error: function(request, status, error){
						alert("[에러] code : " + request.status
								+ "/nmessage:" + request.responseText
								+ "/nerror:" + error);
					}
				});
			});
			$(".upload_profile_photo").click(function(){
				$("#workspace_black_filter").css("display","block");
				$("#popup_workspace_profile_upload").css("display","block");
			});
			$(".x_btn").click(function(){
				$(this).closest(".popup_window").css("display","none");
				$("#workspace_black_filter").css("display","none");
			});
			$(".workspace_profile_btn").click(function(){
				$("#popup_workspace_profile_upload").css("display","none");
				$("#workspace_black_filter").css("display","none");
			});
			$(".button_next").click(function(){
				$("#workspace_content_profile").css("display","none");
				$("#workspace_invite").css("display","block");
				
			});
			$(".workspace_last_btn").click(function(){
				let emailTo = $("#input_email_to").val();
				if(emailTo === "") {
					alert("무분별한 워크스페이스 생성을 막기 위해 이메일 인증이 필요합니다.")
					return;
				}
				$.ajax({
					type:'post',
					url: 'invite',
					data: JSON.stringify({
						"workspaceIdx" : workspaceIdx,
						"emailTo" : emailTo
					}),
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					success: function(data){
						if(data.result=="success")
						location.href="inviteCode";
						
					},
					error: function(request, status, error){
						alert("[에러] code : " + request.status
								+ "/nmessage:" + request.response.responseText
								+ "/nerror:" + error);
					}
				});
				
				
				//location.href="homeDirectory"; // 민재 주소로 변경하기
			});
		});
	</script>
</head>
<body>
	<div id="workspace_header" class="fl"></div>
	<div id="workspace_sidebar" class="fl"></div>
	<div id="workspace_sidebar_menu" class="fl"></div>
	<div id="workspace_content"  class="fl">
		<div>
			<div>1 / 3단계</div>
			<h2>Slack 워크스페이스의 이름을 무엇으로 정하고 싶으세요?</h2>
			<span>회사나 팀 이름처럼 팀이 인식할 수 있는 이름을 선택하세요.</span>
			<div id="workspace_name">
				<div>
					<input type="text" id="workspace_name_input" placeholder="예:정처산기 2조"/>
					<span>50</span>
				</div>	
			</div>
			<div>
				<button class="workspace_first_btn">다음</button>
			</div>
		</div>
	</div>
	<div id="workspace_content_profile" style="display: none;" class="fl">
		<div>
			<div>2 / 3단계</div>
			<h2>프로필 등록</h2>
			<div>프로필 사진을 추가하면 팀원이 사용자를 쉽게 알아보고 연결하는 데 도움이 됩니다.</div>
			<div id="workspace_user_name" style="display: none;">
				<div>
					<input type="text" placeholder="성명 입력"/>
					<span>50</span>
				</div>	
			</div>
			<span>내 프로필 사진</span> <span>(옵션)</span>
			<div class="profile_section">
				<div>팀원들이 적절한 사람과 대화하고 있음을 알 수 있도록 하세요.
				</div>
				<div>
					<img alt="" src="https://ca.slack-edge.com/T0A05H067D0-U09VD5MDYUB-g5d848ae856f-192" srcset="https://ca.slack-edge.com/T0A05H067D0-U09VD5MDYUB-g5d848ae856f-192, https://ca.slack-edge.com/T0A05H067D0-U09VD5MDYUB-g5d848ae856f-512 2x" aria-hidden="true" class="p-setup-page__profile-photo">
				</div>
				<button class="upload_profile_photo">
					<span>사진 업로드</span>
				</button>
			</div>
			<button class="button_next">
				<span>다음</span>
			</button>
		</div>	
	</div>
	<div id="workspace_black_filter" style="display: none;"></div> <!-- 페이드아웃필터 -->
	<div id="popup_workspace_profile_upload" class="popup_window" style="display: none;">
		<div>
			<h2>프로필 사진 추가</h2> <!-- 프로필사진추가 -->
		</div> <!-- 프로필사진추가 컨테이너 -->
		<div class="x_btn">
			<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
		</div> <!-- X버튼 -->
		<div class="img_file_upload">
			<div>
				<div>
					<img alt="이미지 업로드" aria-hidden="true" class="file-upload__image" src="https://a.slack-edge.com/bv1-13-br/wp_add_profile_minicard@2x-3cf01eb.png" srcset="https://a.slack-edge.com/bv1-13-br/wp_add_profile_minicard@1x-8595002.png 1x, https://a.slack-edge.com/bv1-13-br/wp_add_profile_minicard@2x-3cf01eb.png 2x">
					<h3>사진 끌어서 놓기</h3>
				</div>
				<input type="file" id="real_upload_btn" style="display:none;"/>
				<button id="file_upload_btn"><h3>파일보내기</h3></button>
			</div> <!-- 이미지 업로드 / 사진 끌어서 놓기 -->
		</div> <!-- 가운데 이미지 넣을 컨테이너 -->
		<div>
			<div class="workspace_cancle_save">
				<button class="workspace_profile_btn"><b>취소</b></button>
				<button class="workspace_profile_btn"><b>저장</b></button>
			</div>
		</div> <!-- 취소랑 저장버튼 -->
	</div> <!-- 사진 프로필 업로드 팝업 -->
	<div id="workspace_invite" style=display:none; class="fl">
		<div>
			<div>3 / 3단계</div>
			<h2>워크스페이스 생성 인증</h2>
			<div>이메일 본인 인증</div>
			<div>
				<div class="workspace_input_email"">
					<input type="text" id="input_email_to" placeholder="실제 이메일을 입력해주세요."
					/>
				</div>	
			</div>	
			<span style=color = red;>가입된 이메일로만 초대코드를 받을 수 있는 점에 유의해주세요.</br>
			계정비활성화를 원하시는 분께서는 관리자에게 연락해주세요.</br>
			관리자Email:seungbin4369@gmail.com , lim82378@gmail.com
			</span>
			<div>	
				<button class="workspace_last_btn">다음</button>				
			</div>
		</div>	
	</div>
</body>
</html>