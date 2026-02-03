let currentChannelIdx = 0; // 생성된 채널 번호를 임시 보관하는 '바구니'
let selectedFile = null; // 파일 객체를 담을 수 있는 전역 변수
let globalChannelIdx = currentChannelIdx || $(".side_menu_item.on").data("channel_idx") || $(this).data("channel_idx");
//웹소켓 사용하기
const host = window.location.host;

const contextPath = "/slack";

const webSocket = new WebSocket("ws://"+host+contextPath+"/broadcasting");
// --> '접속'. (접속하는 데 문제가 있으면, 여기서 에러남.)
         
webSocket.onmessage = function(e) {
	// 채널에서 메시지를 받았을 때.
	let chat;
    try {
        chat = JSON.parse(e.data); 
    } catch (err) {
        console.error("JSON 파싱 에러:", err);
        return;
    }

    // [중요] 현재 활성화된 채널 번호를 실시간으로 가져와 비교
    let activeChannelIdx = $(".side_menu_item.on").data("channel_idx") || currentChannelIdx;

    if (chat.channelIdx == activeChannelIdx) {
    	let profile = chat.profileImg;
	 	let profileUrl = "";
	 	if(1<=profile&&profile<=10) {
	 		profileUrl = "display?filename=ProfileImg"+profile+".png";
	 	}else {
	 		profileUrl = "display?filename=" + profile;
	 	}
        let listHtml = `
		            <div class="channel_content_message">
		                <img src="${profileUrl}"/>
		                <div class="channel_content_name_time">
		                    <span class="send_member">${chat.nickname}</span> 
		        			<span class="send_time">${chat.sentTime}</span>
		                    <div class="channel_content_chat">${chat.msg}</div>
		                </div>
		            </div>`;
        
        const chatArea = $(".channel_content_list_area");
        chatArea.append(listHtml);
        
        // [수정] 0초 타이머를 주어 렌더링 후 실행되도록 보장
        setTimeout(function() {
            chatArea.scrollTop(chatArea[0].scrollHeight);
            console.log("스크롤 이동 완료: ", chatArea[0].scrollHeight);
        }, 0);
    }
};
webSocket.onopen = function(e) {
	console.log("연결되었습니다.");
};
webSocket.onerror = function(e) { 
	alert("error!");
};

//워크스페이스 이름 설정
$(document).ready(function() {
    $.ajax({
    	type: "post", 
		url: "workspace_name", 
		data: JSON.stringify({
			"WorkspaceIdx": globalWorkspaceIdx
		}),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function(data) {
    		$(".workspace_name").text(data.name);
    	},
		error: function(request, status, error) {
			alert("[에러] code:"+request.status
			+"\nmessage:" +request.responseText+"\nerror:" + error);
		}
    });
});
$(function(){
	//웹소켓 채널 메세지 보내기
	$("#editor_dtn").click(function() { //enter말고 클릭으로 고정하기
		let userId = loginId; //누가
		let channelIdx = currentChannelIdx || $(".side_menu_item.on").data("channel_idx");//어디에서
		let workspaceIdx = globalWorkspaceIdx;
		
		let msg = quill.root.innerHTML; //메세지 내용 퀼 에디터의 HTML 내용 가져오기		

		// 에디터가 비어있는지 체크 (HTML 태그 제외하고 실제 텍스트가 있는지 확인)
	    if (quill.getText().trim().length === 0 && !msg.includes('<img')) {
	        return; 
	    }
		//어디로 보내는지
		$.ajax({
	    	type: "post", 
			url: "channel_send_msg", 
			data: JSON.stringify({
				"UserId": userId,
				"ChannelIdx": channelIdx,
				"WorkspaceIdx": workspaceIdx,
				"Msg": msg,
				"type":"channel"
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) { //성공시
	    	if (typeof webSocket !== 'undefined' && webSocket.readyState === WebSocket.OPEN) {
	            webSocket.send(JSON.stringify(data)); 
	        } else {
	            console.log("웹소켓 연결이 닫혀있거나 변수가 정의되지 않았습니다.");
	        }
	        
	        // 2. 에디터 비우기
	        quill.setContents([]);
	    	},
			error: function(request, status, error) { //실패시
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
	});
	
	//홈 이동
	$(".move_home_btn").click(function(){
		location.href="homeDirectory?workspace_idx="+ globalWorkspaceIdx;
	});
	
	//dm 이동
	$(".move_dm_btn").click(function(){
		location.href="dm?workspace_idx="+globalWorkspaceIdx;
	});
	
	//내활동 이동
	$(".move_myactivity_btn").click(function(){
		location.href="myActivity?workspace_idx="+globalWorkspaceIdx;
	});
	
	//파일 이동
	$(".move_file_btn").click(function(){
		location.href="file?workspace_idx="+globalWorkspaceIdx;
	});
	
	//워크스페이스 리스트 이동
	$(".workspace_list_btn").click(function(){
		location.href="workspaceList";
	});
	
	//디렉터리 버튼 클릭
	$("#open_popup_directory").click(function(){
		$(".side_menu_item").removeClass("on");
		$(this).addClass("on");
		$("#main_content").css("display","block");
		$("#basic_channel_div").css("display","none");
	});
	
	//전체채팅방 클릭시(다른채널들도 만들어야하는지 아니면 새롭게 해야되는지) 비동기 통신
	$("#basic_channel").click(function(){
		$(".side_menu_item").removeClass("on");
		$(this).addClass("on");
		$("#basic_channel_div").css("display","block");
		$("#main_content").css("display","none");
	});
	
	//채널 클릭시
	$(document).on("click", ".open_popup_channel" , function() {
		let channelIdx = $(this).data("channel_idx");
		let workspaceIdx = globalWorkspaceIdx;
		$(".side_menu_item").removeClass("on");
		$(this).addClass("on");
		$("#new_channel").css("display","block");
		$("#main_content").css("display","none");
		$.ajax({
		    	type: "post", 
				url: "channel_div_setting", 
				data: JSON.stringify({
					"ChannelIdx": channelIdx,
					"WorkspaceIdx": workspaceIdx
				}),
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				success: function(data) { //성공시
		    		$(".channel_content_header_name").text(data.channelName);
		    		$(".channel_name").text(data.channelName);
		    		$("#channel_created_time").text(data.createdtime);
		    		$("#channel_member_count").text(data.members);
		    		//dto불러와서 반복문해주기
		    		// 2. 채팅 내역 반복문 처리
		            let listHtml = "";
		            let chatList = data.list; // 컨트롤러에서 retMap.put("list", list)로 보냈기 때문

		            if (chatList && chatList.length > 0) {
		                chatList.forEach(chat => {
		                	
		                	let profile = chat.profileImage;
		       			 	let profileUrl = "";
		       			 	if(1<=profile&&profile<=10) {
		       			 		profileUrl = "display?filename=ProfileImg"+profile+".png";
		       			 	}else {
		       			 		profileUrl = "display?filename=" + profile;
		       			 	}

		                    listHtml += `
		                       <div class="channel_content_message button_hover_set_gray">
		                    		<img src="${profileUrl}"/>
		                    		<div class="channel_content_name_time">
		                    			<span class="send_member">${chat.nickname}</span>
		                    			<span class="send_time">${chat.sentTime}</span>
		                    			<div class="channel_content_chat">${chat.content}</div>
		                    		</div>
		                       </div>
		                    `;
		                });
		            } else {
		                listHtml = "<div style='padding:20px; text-align:center; color:gray;'>새로 생긴 채널입니다.</div>";
		            }

		            // 3. 채팅 영역에 삽입
		            // 채팅 내역이 들어가는 실제 부모 div의 클래스명으로 바꿔주세요.
		            $(".channel_content_list_area").html(listHtml); 
		            
		            // 4. 스크롤 최하단 이동 (약간의 지연을 주어 렌더링 후 이동 보장)
		            setTimeout(function() {
		                let chatArea = $(".channel_content_list_area");
		                if (chatArea.length > 0) {
		                    // animate를 쓰면 툭 끊기지 않고 부드럽게 내려가서 더 고급스러워 보입니다.
		                    chatArea.scrollTop(chatArea[0].scrollHeight);
		                }
		            }, 50); // 0.05초 뒤에 실행 (화면이 그려질 최소한의 시간)
		    	},
				error: function(request, status, error) { //실패시
					alert("[에러] code:"+request.status
					+"\nmessage:" +request.responseText+"\nerror:" + error);
				}
		  });
	});
	
	//채널에서 우클릭 시
	$(document).on("contextmenu", ".open_popup_channel", function(e){
		const $btn = $(this);
		let channelIdx = $(this).data("channel_idx");
		e.preventDefault();
		$(".channel_right_click_menu_div").find(".channel_favorite_btn").data("channel_idx", channelIdx);
		$(".channel_right_click_menu_div").find(".channel_getout_btn").data("channel_idx", channelIdx);
		$(".channel_right_click_menu_div").find(".channel_AI_chat_btn").data("channel_idx", channelIdx);
		$(".channel_right_click_menu_div").find(".channel_content_information_btn").data("channel_idx", channelIdx);
		$(".channel_right_click_menu_div").css({ display: 'block', left: e.pageX, top: e.pageY });
		$("#clear_filter").css("display","block");
	});
	
	// 우클릭하고 다른 곳 클릭 시 참고: 팝업 외부 클릭 시 닫기 기능도 위임 방식으로 쓰면 안정적입니다.
    $(document).on("click", function() {
        $(".channel_right_click_menu_div").hide();
        $(".remove_member").hide();
        $("#clear_filter").hide();
    });
    
    //채널 상세보기 멤버에서 멤버 div 우클릭 하면 채널 멤버 제거 버튼 활성화 해야함
    $(document).on("contextmenu", ".channel_member_uesrs", function(e){
    	let userId = $(this).data("id");
    	e.preventDefault();
    	$(".remove_member").data("user-id", userId);
    	$(".remove_member").css({ display: 'block', left: e.pageX, top: e.pageY });
    });
    
    //채널 멤버 제거 버튼 클릭 시
	$(".remove_member").click(function(){
		let userId = $(this).data("user-id");
		let channelIdx = $(".side_menu_item.on").data("channel_idx") || $(this).data("channel_idx");
		
		console.log("제거 대상 "+userId+" 채널 번호 "+channelIdx);
		$.ajax({
	    	type: "post", 
			url: "remove_channel_member", 
			data: JSON.stringify({
				"UserId": userId,
				"ChannelIdx" : channelIdx
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) { //성공시
	    		alert("멤버가 제거 되었습니다.");
	    		let targetSelector = ".channel_member_uesrs[data-id='" + userId + "']";
	    		
	    		$(targetSelector).remove();
	    	},
			error: function(request, status, error) { //실패시
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
	});
	
	//모든 팝업 X 버튼 클릭시 display:none if 딥필터와 블랙필터가 같이 켜져 있을때는 딥필터만 꺼지게..
	$(".x_btn").click(function(){
		$("input").val(""); //일단은 만듬 텍스트 초기화 하는 방법
		$(this).closest(".popup_window").css("display","none");
		$("#black_filter").css("display","none");
		$("#black_filter_deep").css("display","none");
	});
	
	//검색 x 클릭시
	$(".search_x_btn").click(function(){
		$("input").val("");
		location.reload();
	});
	
	//프로필 클릭시 $(document).on("click", ".profile" , function()
	$(document).on("click", ".profile" , function() {
		let name = $(this).find("div").text().trim();
		let userId = $(this).data("user_id");
		let myId = loginId;
		$.ajax({
			type: "post", 
			url: "user_profile", 
			data: JSON.stringify({
				"UserId": userId,
				"WorkspaceIdx": globalWorkspaceIdx
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) {
				//if(data.nickname && data.nickname.includes("(나)")) {
				let profile = data.profileImg;
				let profileUrl = "";
				if(1<= profile && profile <= 10) {
					profileUrl = "display?filename=ProfileImg" + profile + ".png";
				}else {
					profileUrl = "display?filename=" + profile;
				}
				if(data.useremail==myId) {
					$(".popup_member_profile_div").css("display","none");
					$("#popup_my_profile_div").css("display","block");
					$(".profile_user_name").text(data.name);
					$(".on_off_line").text(data.status);
					$(".users_email").text(data.useremail);
					$("#my_profile_img_3").attr("src", profileUrl);
				} else {
					$(".popup_member_profile_div").css("display","none");
					$("#popup_my_profile_div").css("display","none");
					$("#member_profile2").css("display","block");
					$(".profile_user_name").text(data.name);
					$(".on_off_line").text(data.status);
					$(".user_condition").text(data.condition);
					$(".users_email").text(data.useremail);
					$("#member_profile_img").attr("src", profileUrl);
				}
			},
			error: function(request, status, error) {
				alert("userId"+userId);
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
		});
	});
	//상태 설정 버튼
	$("#popup_status").click(function(){
		$("#black_filter").css("display","block");
		$("#popup_profile_status_update_div").css("display","block");
	});
	
	//백틱으로 div만들고 초기화 버튼 만들어놓고 초기화 누르면 display none 되면서 상태 초기화
	$(".status_update").click(function(){
		let condition = $(this).find("div").text();
		let myId = loginId;
		let workspaceIdx = globalWorkspaceIdx;
		const selectedImg = $(this).find("img").attr("src");
		$.ajax({
	    	type: "post", 
			url: "set_condition", 
			data: JSON.stringify({
				"Condition": condition,
				"UserId": myId,
				"WorkspaceIdx": workspaceIdx
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) { //성공시
	    		let listHtml = "";
	    		let newcondition = data.condition;
	    		console.log(newcondition);
	    		listHtml += `
	    			<div class="status_update no-hover">
					<img src="${selectedImg}" style="margin-left: 16px;"/>
					<div style="margin-left : 41px; ">${newcondition}</div>
				</div>
	    		`;
	    		// 프로필 영역에 삽입
	            $("#my_profile_status_area").html(listHtml);
	    	},
			error: function(request, status, error) { //실패시
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
		$("#popup_profile_status_update_div").css("display","none");
		$("#black_filter").css("display","none");
	});
	
	//비밀번호 변경 버튼
	$("#popup_password_set").click(function(){
		$("#black_filter").css("display","block");
		$("#edit_password_div").css("display","block");
		$("#current_pw").val("");
	    $("#new_pw").val("");
	    $("#new_pw_check").val("");
	    $("#current_pw_false").css("display","none");
	    $("#pw_check_false").css("display","none");
	    $("#new_pw_rule").css("display","none");
	});
	
	//비밀번호 변경하기 버튼
	$("#edit_password_end").click(function(){
		let userId = loginId;
		let userPw = $("#current_pw").val();
		let newPw = $("#new_pw").val();
		let newPwCheck = $("#new_pw_check").val();
		if(newPw === newPwCheck && newPw.length > 5) {
			$.ajax({
		    	type: "post", 
				url: "set_password", 
				data: JSON.stringify({
					"UserId": userId,
					"UserPw" : userPw,
					"NewPw" : newPw,
					"NewPwCheck" : newPwCheck
				}),
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				success: function(data) { //성공시
		    		let nowPw = data.nowPw;
		    		if(userPw==nowPw) {
		    			$("#black_filter").css("display","none");
		    			$("#edit_password_div").css("display","none");
		    			alert("변경되었습니다.");		    			
		    		} else {
		    			$("#current_pw_false").css("display","block");
		    		}
		    	},
				error: function(request, status, error) { //실패시
					alert("[에러] code:"+request.status
					+"\nmessage:" +request.responseText+"\nerror:" + error);
				}
		    });
		}else {
			if(newPw !== newPwCheck) {
				$("#pw_check_false").css("display","block");
				alert("새 비밀번호 체크에서 막힘");
				event.preventDefault();
			}else {
				$("#new_pw_rule").css("display","block");
				alert("비빌번호 6자리에서 막힘");
				event.preventDefault();
			}
		}
	});
	
	//사진 업로드 버튼
	$("#popup_profile_img").click(function(){
		$("#black_filter").css("display","block");
		$("#edit_my_profile_picture_div").css("display","block");
	});
	$("#profile_file_upload_dtn1").click(function(){ 
		$("#click_file_upload_1").click();
	});
	$("#profile_file_upload_dtn2").click(function(){ 
		$("#edit_my_profile_div").hide();
		$("#edit_my_profile_picture_div").css("display","block");
	});
	
	// 1. 파일 선택 시 브라우저에서 미리보기만 즉시 실행
	$(".click_file_upload").on("change", function(e) {
	    let file = e.target.files[0];
	    if (file) {
	    	selectedFile = file; // 전역 변수에 파일 저장
	        // 브라우저 임시 URL 생성 (서버 통신 없음)
	        let reader = new FileReader();
	        reader.onload = function(e) {
	            $("#before_upload").hide(); // 기존 아이콘 숨기기
	            $("#profile_file_upload_dtn1").hide(); // 업로드 버튼 숨기기
	            
	            // 미리보기 이미지 태그들에 주입
	            $("#my_profile_img").attr("src", e.target.result).show();
	            $("#my_profile_img_2 img").attr("src", e.target.result); 
	        }
	        reader.readAsDataURL(file);
	    }
	});

	// 2. '저장' 버튼 클릭 시 실제 서버 업로드 진행
	$("#edit_my_profile_picture_end").click(function() {
		if (!selectedFile) {
	        alert("사진을 먼저 선택해주세요.");
	        return;
	    }

	    let formData = new FormData();
	    formData.append("file", selectedFile);
	    formData.append("userId", loginId);
	    formData.append("workspaceIdx", globalWorkspaceIdx);

	    $.ajax({
	        type: "post",
	        url: "file_upload",
	        data: formData,
	        contentType: false,
	        processData: false,
	        success: function(data) {
	            if (data !== "fail") {
	                selectedFile = null;
	                $(".click_file_upload").val("");
	                // 서버에서 저장된 파일명을 리턴받았으므로, display 매핑으로 최종 확인
	                let finalUrl = "display?filename=" + data;
	                let userId=loginId;
	                $("#my_profile_img").attr("src", finalUrl);
	                $("#my_profile_img_2 img").attr("src", finalUrl);
	                $("#my_profile_img_3 ").attr("src", finalUrl);
	                //반복문돌려서 나를 찾고 나랑 비교해서 그 img만 바꿔주기
	                $(".profile").each(function(idx, item) {
	                	if($(item).attr("data-user_id")==loginId) {
	                		$(item).find("img").attr("src", finalUrl);
	                	}
	                });
	                // 팝업 닫기 등 후속 처리
	                $("#edit_my_profile_picture_div").hide();
	                $("#black_filter").hide();
	            } else {
	                alert("업로드 실패");
	            }
	        },
	        error: function(request, status, error) {
	            alert("에러 발생: " + error);
	        }
	    });
	});
	
	// 취소 버튼이나 X 버튼 클릭 시
	$(".x_btn, .edit_my_profile_picture_footer .x_btn").click(function(){
	    selectedFile = null;             // 변수 초기화
	    $(".click_file_upload").val(""); // input 태그 비우기
	    
	    // 미리보기 이미지 초기화 (기본 이미지로 되돌리기)
	    $("#my_profile_img").hide().attr("src", "");
	    $("#my_profile_img_2 img").attr("src", "https://ca.slack-edge.com/T09J85U0SLV-U09LDJKJR7S-g9beb95f9c41-72");
	    $("#before_upload").show();
	    $("#profile_file_upload_dtn1").show();
	    // 팝업 닫기
	    $("#edit_my_profile_picture_div").hide();
	    $("#black_filter").hide();
	});
	
	//내 프로필 > 편집 버튼
	$("#my_profile_edit").click(function(){
		$.ajax({
	    	type: "post", 
			url: "user_profile", 
			data: JSON.stringify({
				"UserId": loginId,
				"WorkspaceIdx": globalWorkspaceIdx
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) { //성공시
	    		let profile = data.profileImg;
	    		 let profileUrl = "";
	    		 if(1<=profile&&profile<=10) {
	    			 profileUrl = "display?filename=ProfileImg"+profile+".png";
	    		 }else {
	    			 profileUrl = "display?filename=" + profile;
	    		 }
	    		 $("#edit_my_profile_img").attr("src",profileUrl);
	    	},
			error: function(request, status, error) { //실패시
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
		$("#black_filter").css("display","block");
		$("#edit_my_profile_div").css("display","block");
	});
	
	//내 프로필 > 편집 > AI 프로필 생성 버튼
	$("#open_popup_AI_profile").click(function(){
		$("#edit_my_profile_div").css("display","none");
		$("#AI_profile_img_div").css("display","block");
	});
	
	// 디렉터리 > 사용자 초대 버튼
	$("#open_popup_member_invite").click(function(){
		$("#popup_invite").css("display","block");
		$("#black_filter").css("display","block");
	});
	
	//디렉터리 참여순 버튼(정렬)
	$("#open_popup_first_time").click(function(e){
		$("#clear_filter").css("display","block");
		$("#directory_popup_sort_div").css({ display: 'block', left: e.pageX + -230, top: e.pageY + 25 });
	});
	
	$(".directory_popup_sort").click(function(){
		$(".directory_popup_sort").removeClass("on_hover");
		$(this).addClass("on_hover");
		$("#first_time_text").text($(this).text());
		$("#clear_filter").css("display","block");
	});
	
	//클리어필터 클릭시 
	$("#clear_filter").click(function(){
		$("#directory_popup_sort_div").css("display","none");
		$(".channel_right_click_menu_div").css("display","none");
		$("#clear_filter").css("display","none");
	});
	
	//채널 추가 버튼 > 채널 추가 팝업
	$("#open_popup_channel_create").click(function(){
		$("#channel_creation_div").css("display","block");
		$("#black_filter").css("display","block");
	});
	
	//채널 추가 팝업 > 생성 > 채널 사용자 추가 팝업
	// [공정 1] 채널 생성 버튼 (Next)
	$("#channel_creation_next").click(function(){
	    let text = $("#channel_create_name").val();
	    if (!text) { alert("채널 이름을 입력해주세요."); return; }

	    $.ajax({
	        type: "post",
	        url: "channel_create", // 여기서는 채널 생성만 담당
	        data: JSON.stringify({
	            "UserId": loginId,
	            "WorkspaceIdx": globalWorkspaceIdx,
	            "Text": text,
	            "Topic": "주제 추가",
	            "Explanation": "설명"
	        }),
	        contentType: "application/json; charset=utf-8",
	        success: function(data) {
	            // 1. 서버가 준 channelIdx를 전역 변수에 저장 (중요!)
	            currentChannelIdx = data.channelIdx; 
	            
	            // 2. UI 전환 (사용자 추가 팝업 열기)
	            $(".channel_name").text(text); // 팝업 내 채널이름 갱신
	            $("#channel_creation_div").hide();
	            $(".channel_creation_member_invite_div").show();

	        }
	    });
	});
	
	//사용자 추가 라디오 버튼
	$("#invite_radio").click(function(){
		$(".channel_member_name_invite_div").css("display","block");
	});
	
	//사용자 추가 라디오 버튼
	$("#not_invite_radio").click(function(){
		$(".channel_member_name_invite_div").css("display","none");
	});
	
	//사용자 추가에서 검색 시
	$("#channel_member_search_text").on("input", function(){
		let workspaceIdx = globalWorkspaceIdx;
		let query = $(this).val();
		let channelIdx = currentChannelIdx || $(".side_menu_item.on").data("channel_idx");
		let userId = loginId;
		console.log("채널 값 : "+channelIdx);
		
		 if (query.length > 0) { //input text에 무언가 입력할 때
		        $.ajax({
		            type: "post",
		            url: "search_all_member",
		            data: JSON.stringify({
		                "WorkspaceIdx": workspaceIdx,
		                "Search": query,
		                "ChannelIdx": channelIdx
		            }),
		            contentType: "application/json; charset=utf-8",
		            dataType: "json",
		            success: function(data) {
		            	 console.log(data);
		            	 let listHtml = "";
			             let users = data.userList;
			             let search = data.search;
			             
			             if (users && users.length > 0) { //검색이 될 때
			            	 users.forEach(user => {
		            		 let profile = user.profileImage;
		            		 let profileUrl = "";
		            		 if(1<=profile&&profile<=10) {
		            			 profileUrl = "display?filename=ProfileImg"+profile+".png";
		            		 }else {
		            			 profileUrl = "display?filename=" + profile;
		            		 }
			            	 listHtml += `
			                            <div>
			            			 		<h3>이 채널에서</h3>
			            			 	</div>
			            			 	<div class="channel_member_uesrs" data-id="${userId}" style="display:blcok">
			            			 		<div>
			            			 			<img src=${profileUrl}/>
			            			 			<div class="channel_member_uesrs_text_set">
	    											<div>
	    												<span class="channel_member_uesrs_nickname">${nickname} 
	    												</span>
	    											</div>
			            			 				<!-- 온라인>초록 오프라인>회색 -->
	    											<svg data-i0m="true" data-qa="presence_indicator" aria-hidden="false" title="온라인" aria-label="온라인" data-qa-type="status-member-filled" data-qa-presence-self="true" data-qa-presence-active="true" data-qa-presence-dnd="false" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" d="M14.5 10a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0"></path></svg>
			            			 			</div>
			            			 		</div>
			            			 	</div>
			            			 	`;
			            	 });
	                        if (listHtml !== "") { //listHtml이 없을때
	                        	 users.forEach(user => { 
	                        		 listHtml += `
	                        				 `;
	                        	 });
		                    } else {
		                        $(".channel_create_search_popup").hide();
		                    }
			             } else { //검색이 안될 때
			            	 //여기서 검색 안되는 화면 뿌려주기
			             }
			             
		            }, // success 끝
		            error: function(request, status, error) {
		                alert("[에러] code:" + request.status + "\nmessage:" + request.responseText + "\nerror:" + error);
		            } // error 끝
		        }); // ajax 끝
		    } else { //아무것도 검색하지 않을 때
		        //아무것도 검색하지 않을 때 화면 뿌려주기
		    }
	});
	
	//사용자 추가 검색에서 텍스트 입력시
	$("#input_channel_member_name").on("input", function() {
	    let workspaceIdx = globalWorkspaceIdx;
	    let query = $(this).val();
	    let userId = loginId;

	    if (query.length > 0) {
	        $.ajax({
	            type: "post",
	            url: "search_users",
	            data: JSON.stringify({
	                "WorkspaceIdx": workspaceIdx,
	                "Search": query
	            }),
	            contentType: "application/json; charset=utf-8",
	            dataType: "json",
	            success: function(data) {
	                console.log(data);
	                let listHtml = "";
	                let users = data.userList;

	                if (users && users.length > 0) {
	                    // 하나의 forEach문 안에서 필터링과 HTML 생성을 동시에 합니다.
	                    users.forEach(user => {
	                        // 1. 나 자신인지 확인 
	                        if (String(user.USERID) === String(userId)) {
	                            return; // 나 자신은 건너뜀
	                        }
	                        let profile = user.PROFILEIMAGE;
	                        let profileUrl = "";
	                        if(1<=profile&&profile<=10) {
	                       	 profileUrl = "display?filename=ProfileImg"+profile+".png";
	                        }else {
	                       	 profileUrl = "display?filename=" + profile;
	                        }
	                        // 2. HTML 생성
	                        listHtml += `
	                            <div class="users_search_div" data-id="${user.USERID}">
	                                <img src=${profileUrl} 
	                                     onerror="this.src='resources/img/profile/ProfileImg1.png'"/>
	                                <span class="channel_invite_users">${user.NICKNAME}</span>
	                            </div>
	                        `;
	                    });

	                    // 생성된 HTML이 있을 때만 표시
	                    if (listHtml !== "") {
	                        $(".channel_create_search_popup").html(listHtml).show();
	                    } else {
	                        $(".channel_create_search_popup").hide();
	                    }
	                } else {
	                    $(".channel_create_search_popup").hide();
	                }
	            }, // success 끝
	            error: function(request, status, error) {
	                alert("[에러] code:" + request.status + "\nmessage:" + request.responseText + "\nerror:" + error);
	            } // error 끝
	        }); // ajax 끝
	    } else {
	        $(".channel_create_search_popup").hide();
	    }
	});
	
	
	$(document).off("click", ".users_search_div").on("click", ".users_search_div", function() {
	    let selectedNickname = $(this).find(".channel_invite_users").text();
	    let selectedUserId = $(this).data("id"); // <div data-id="${user.USERID}"> 에서 가져옴
	    $("#input_channel_member_name").val(selectedNickname);
	    $("#input_channel_member_name").attr("data-selected-id", selectedUserId); // .attr로 명시적 저장
	    $(".channel_create_search_popup").hide();
	});
	
	//채널 생성 완료 버튼
	$("#channel_creation_end").click(function(){
	    // 라디오 버튼 확인: 특정 사용자 추가인지 확인
	    let isSpecificInvite = $("#invite_radio").is(":checked");
	    let selectedUserId = $("#input_channel_member_name").attr("data-selected-id") || "";
	    let channelIdx = currentChannelIdx || $(".side_menu_item.on").data("channel_idx");
	    if (isSpecificInvite && !selectedUserId) {
	        alert("초대할 멤버를 검색하여 선택해주세요.");
	        return;
	    }

	    // 이미 생성된 채널(currentChannelIdx)에 멤버만 추가하는 전용 주소 호출
	    $.ajax({
	        type: "post",
	        url: "add_channel_member", // 새로운 컨트롤러 주소 (또는 로직 분리)
	        data: JSON.stringify({
	            "ChannelIdx": channelIdx,
	            "UserId": selectedUserId,
	            "WorkspaceIdx": globalWorkspaceIdx,
	            "InviteAll": $("#not_invite_radio").is(":checked") // 모두 추가 여부
	        }),
	        contentType: "application/json; charset=utf-8",
	        success: function(res) {
	            alert("설정이 완료되었습니다.");
	            location.reload(); // 모든 공정 완료 후 갱신
	        }
	    });
	});
	
	//내 프로필 변경사항 저장 버튼
	$("#edit_my_profile_end").click(function(){
		let workspaceIdx = globalWorkspaceIdx;
	    let name = $("#users_name_update_text").val();
	    let nickname = $("#workspace_nickname_update_text").val();
	    let title = $("#users_title_update_text").val();
	    let userId = loginId;
		$.ajax({
	    	type: "post", 
			url: "myProfileUpdate", 
			data: JSON.stringify({
				"WorkspaceIdx":workspaceIdx,
				"UserId": userId,
				"Name": name,
				"Nickname": nickname,
				"Title": title
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) { //성공시
	    		$("#edit_my_profile_div").css("display","none");
	    		$("#black_filter").css("display","none");
	    		$("#my_workspace_nickname_div").text(data.name);
	    		$(".profile_user_name").text(data.nickname);	    		
	    	},
			error: function(request, status, error) { //실패시
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
	});
	
	//AI 프로필 이미지 생성하기 버튼
	$("#AI_profile_img_end").click(function(){
		let word1 = $("#keyword1").val();
		let word2 = $("#keyword2").val();
		let word3 = $("#keyword3").val();
		$.ajax({
	    	type: "post", 
			url: "ai/AIprofileImgCreate", 
			data: JSON.stringify({
				"WorkspaceIdx": globalWorkspaceIdx,
				"UserId": loginId,
				"Word1": word1,
				"Word2": word2,
				"Word3": word3
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			beforeSend: function() {
	            //서버에 요청 보내기 직전에 로딩 화면 띄우기
	            $("#loading_layer").css("display", "flex");
	            $("#AI_profile_img_div").hide();
	            $("#black_filter").hide();
	        },
			success: function(data) { //성공시
	        	if(data.status === "success") {
	        		// 1. 캐시 방지를 위한 타임스탬프
	                let newImgUrl = data.url + "?t=" + new Date().getTime();
	                
	                // 2. 다른 곳(내 설정창 등)에 있는 내 프로필 교체
	                $("#my_profile_img_3").attr("src", newImgUrl);
	                
	                // 3. 워크스페이스 멤버 리스트 중에서 '내 아이디'인 이미지만 교체
	                // loginId는 JS 전역변수로 가지고 계신 값을 사용하면 됩니다.
	                $(".profile[data-user_id='" + loginId + "']").find(".my_workspace_AI_profile").attr("src", newImgUrl);
	                
	                alert("나만의 AI 프로필이 완성되었습니다!");
	            }
	    	},
			error: function(request, status, error) { //실패시
	    		alert("이미지 생성 중 오류가 발생했습니다.");
			},
			complete: function() {
	            //성공하든 실패하든 통신이 끝나면 로딩 화면 숨기기
	            $("#loading_layer").hide();
	        }
	    });
		$("#AI_profile_img_div").css("display","none");
		$("#black_filter").css("display","none");
	});
	
	//사진 업로드 팝업 저장 버튼
	$("#edit_my_profile_picture_end").click(function(){
		$("#edit_my_profile_picture_div").css("display","none");
		$("#black_filter").css("display","none");
	});
	
	//사용자 추가 x 버튼
	$(".member_invite_x_btn").click(function(){
		$(".channel_creation_member_invite_div").css("display","none");
		$("#black_filter").css("display","none");
	});
	
	//사용자 초대 보내기 버튼
	$("#popup_invite_end").click(function(){
		$("#popup_invite").css("display","none");
		$("#black_filter").css("display","none");
		let userId = $("#popup_invite_text").val();
		let workspaceIdx = globalWorkspaceIdx;
		$.ajax({
	    	type: "post", 
			url: "invite_users", 
			data: JSON.stringify({
				"UserId": userId,
				"WorkspaceIdx": workspaceIdx
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) {
	    		alert("초대에 성공했습니다.");
	    		console.log("이름은 "+data.name+"닉네임은 "+data.nickname+"프로필 이미지 랜덤으로는 "+data.profileImg);
	    	},
			error: function(request, status, error) {
				alert("userId"+userId);
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
	});
	
	//채널 인원수 버튼
	$(".channel_content_users_btn").click(function(){
		$(".channel_menu_button").removeClass("on");
		$(".channel_information_member").addClass("on");
		$(".show_channel_information_div").css("display","block");
		$(".show_channel_member_content").css("display","block");
		$(".show_channel_information_content").css("display","none");
		$("#black_filter").css("display","block");
	});
	
	//채널 ... 버튼 채널 세부정보 보기 클릭시(정보)
	$(".channel_content_information_btn").click(function(){
		const $btn = $(this);
		let channelIdx = $(".side_menu_item.on").data("channel_idx") || $(this).data("channel_idx");
		$.ajax({
			type: "post",
			url: "information_Info",
			data : JSON.stringify({
				"ChannelIdx": channelIdx
			}),
			contentType:"application/json; charset=utf-8",
			dataType: "json",
			success: function(data) { 
				//채널매니저 조회
				$(".channel_manager_name").text(data.managerName);
				//채널매니저 아이디랑 로그인 아이디가 다를시 편집버튼 숨키기.
				//채널 이름 조회
				$(".this_channel_name").text(data.channelName);
				//채널 주제 조회
				$(".channel_topic").text(data.topic);
				//채널 설명 조회
				let explanation = data.explanation;
				const maxLength = 38;
				if(explanation && explanation.length > maxLength) {
					explanation = explanation.substring(0, maxLength) + "...";
				}
				$(".channel_explanation").text(explanation);
				
			},
			error:function(request,status,error) {
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
		});
		$(".channel_right_click_menu_div").css("display","none");
		$(".channel_menu_button").removeClass("on");
		$(".channel_information_data").addClass("on");
		$(".show_channel_information_div").css("display","block");
		$(".show_channel_information_content").css("display","block");
		$(".show_channel_member_content").css("display","none");
		$("#black_filter").css("display","block");
	});	
	
	//채널 세부정보 보기(정보/멤버)
	$(".channel_information_data, .channel_information_member").click(function(){
	    // 공통 작업
	    $(".channel_right_click_menu_div").hide();
	    $(".channel_menu_button").removeClass("on");
	    $(this).addClass("on");
	    $("#black_filter").show();

	    // 클래스에 따른 분기 처리
	    const isInfo = $(this).hasClass("channel_information_data");

	    $(".show_channel_information_content").toggle(isInfo);
	    $(".show_channel_member_content").toggle(!isInfo);
	    //이 채널의 멤버들 조회
	    let channelIdx = $(".side_menu_item.on").data("channel_idx") || $(this).data("channel_idx");
	    let workspaceIdx = globalWorkspaceIdx;
	    let myId = loginId;
	    $.ajax({
	    	type: "post", 
			url: "channel_member_select", 
			data: JSON.stringify({
				"ChannelIdx": channelIdx,
				"WorkspaceIdx" : workspaceIdx
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) {
	    		let userList = data.list;
	    		let htmlContent = "";
	    		userList.forEach(list => {
	    			const nickname=list.NICKNAME;
	    			let profileImage = list.PROFILEIMAGE;
	    			const userId = list.USERID;
		       		let profileUrl = "";
		       		if(1<=profileImage&&profileImage<=10) {
		       			profileUrl = "display?filename=ProfileImg"+profileImage+".png";
		       		}else {
		       			profileUrl = "display?filename=" + profileImage;
		       		}
	    			htmlContent += `
	    					<div class="channel_member_uesrs" data-id="${userId}" style="display:blcok">
	    						<div>
	    							<img src=${profileUrl}/>
	    								<div class="channel_member_uesrs_text_set">
	    									<div>
	    									<span class="channel_member_uesrs_nickname">${nickname} 
	    									</span>
	    								</div>
	    							<!-- 온라인>초록 오프라인>회색 -->
	    								<svg data-i0m="true" data-qa="presence_indicator" aria-hidden="false" title="온라인" aria-label="온라인" data-qa-type="status-member-filled" data-qa-presence-self="true" data-qa-presence-active="true" data-qa-presence-dnd="false" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" d="M14.5 10a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0"></path></svg>
	    							</div>
	    						</div>
	    					</div>
	    					`;
	    			
	    			//여기서 "(나)" if문 걸고 만들기
	    			if(myId == userId) {
	    				$(".channel_member_uesrs_nickname").text("(나)");
	    			}
	    		});
	    		$("#channel_member_uesrs_div").html(htmlContent);
	    	},
			error: function(request, status, error) {
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
	});
	
	//채널 세부정보 보기에서 채널 사용자 추가 팝업 x누르면 depp필터만 사라지게 하고싶은데 어떻게 해야할지
	$(".channel_users_invite_btn").click(function(){
		let channelIdx = $(".side_menu_item.on").data("channel_idx");
		let workspaceIdx = globalWorkspaceIdx;
	    $.ajax({
	    	type: "post", 
			url: "channel_name", 
			data: JSON.stringify({
				"ChannelIdx": channelIdx,
				"WorkspaceIdx" : workspaceIdx
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) {
	    		$(".channel_name").text(data.name);
	    		$(".users_int").text(data.users);
	    	},
			error: function(request, status, error) {
				alert("userId"+userId);
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
		$("#channel_creation_div").css("display","none");
		$(".channel_creation_member_invite_div").css("display","block");
		$("#black_filter_deep").css("display","block");
	});
	
	//채널 이름 편집 버튼
	$("#channel_name_set_btn").click(function(){
		$("#this_channel_name_set").css("display","block");
		$("#black_filter").css("display","block");
		$("#black_filter_deep").css("display","block");
	});
	
	//채널 이름 변경사항 저장 버튼
	$("#this_channel_name_set_end").click(function(){
		let channelName = $("#channel_name_update_text").val();
		let channelIdx = $(".side_menu_item.on").data("channel_idx") || $(this).data("channel_idx");
		let userId = loginId;
		$.ajax({
	    	type: "post", 
			url: "update_channel_name", 
			data: JSON.stringify({
				"UserId": userId,
				"ChannelIdx": channelIdx,
				"ChannelName": channelName
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) { //성공시
	    		console.log(data.message);
	    	},
			error: function(request, status, error) { //실패시
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
		$("#this_channel_name_set").css("display","none");
		$("#black_filter").css("display","block");
		$("#black_filter_deep").css("display","none");
	});
	//채널 주제 편집 버튼
	$("#channel_topic_set_btn").click(function(){
		$("#this_channel_name_set").css("display","none");
		$("#black_filter").css("display","block");
		$("#black_filter_deep").css("display","none");
		$("#this_channel_topic_set").css("display","block");
		$("#black_filter").css("display","block");
		$("#black_filter_deep").css("display","block");
	});
	//채널 주제 변경사항 저장 버튼
	$("#this_channel_topic_set_end").click(function(){
		let topic = $("#channel_topic_text").val();
		let channelIdx = $(".side_menu_item.on").data("channel_idx") || $(this).data("channel_idx");
		let userId = loginId;
		
		$.ajax({
	    	type: "post", 
			url: "update_topic", 
			data: JSON.stringify({
				"UserId": userId,
				"ChannelIdx": channelIdx,
				"Topic": topic
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) { //성공시
	    		console.log(data.message);
	    	},
			error: function(request, status, error) { //실패시
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
		$("#this_channel_topic_set").css("display","none");
		$("#black_filter").css("display","block");
		$("#black_filter_deep").css("display","none");
	});
	
	//채널 설명 편집 버튼
	$("#channel_explanation_set_btn").click(function(){
		$("#this_channel_explanation_set").css("display","block");
		$("#black_filter").css("display","block");
		$("#black_filter_deep").css("display","block");
	});
	
	//채널 설명 변경사항 저장 버튼
	$("#this_channel_explanation_set_end").click(function(){
		let explanation = $("#channel_explanation_text").val();
		let channelIdx = $(".side_menu_item.on").data("channel_idx") || $(this).data("channel_idx");
		let userId = loginId;
		
		$.ajax({
	    	type: "post", 
			url: "update_explanation", 
			data: JSON.stringify({
				"UserId": userId,
				"ChannelIdx": channelIdx,
				"Explanation": explanation
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) { //성공시
	    		console.log(data.message);
	    	},
			error: function(request, status, error) { //실패시
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
	    });
		$("#this_channel_explanation_set").css("display","none");
		$("#black_filter").css("display","block");
		$("#black_filter_deep").css("display","none");
	});
	
	//채널 우클릭 버튼 클릭 시(AI 요약)
	$(".channel_AI_chat_btn").click(function(){
		const $btn = $(this);
		let channelIdx = $(this).data("channel_idx");
		  $.ajax({
	          type: "post", 
	              url: "ai/summarize", 
	              data: JSON.stringify({
	                      "ChannelIdx": channelIdx,
	                      "WorkspaceIdx": globalWorkspaceIdx
	              }),
	              contentType: "application/json; charset=utf-8",
	              dataType: "text",
	              beforeSend: function() {
	          // 1. 서버에 보내기 전: 결과창에 로딩 애니메이션이나 메시지 표시
	          $(".AI_chat_content").html(`
                  <div class="loading-area" style="text-align:center; padding: 20px;">
                      <img src="https://i.gifer.com/ZZ5H.gif" width="30" style="margin-bottom:10px;"><br>
                      <p style="color: #666; font-size: 14px;">🤖 AI가 지난 대화를 분석하여<br>요약본을 작성 중입니다...</p>
                  </div>
              `);
	      },
	              success: function(response) { //성공시
	                  const summary = `
                              <div class="ai-summary-result">
                                      <h4> AI 대화 요약 결과</h4>
                                      <hr>
                                      <p style="white-space: pre-wrap;">${response}</p>
                                      </div>
                                      `;
	                  $(".AI_chat_content").html(summary);
	                  
	          },
	              error: function(request, status, error) { //실패시
	        	  			alert("ChannelIdx"+channelIdx);
		                    alert("[에러] code:"+request.status
		                    +"\nmessage:" +request.responseText+"\nerror:" + error);
	              }
		  });
		$(".channel_right_click_menu_div").css("display","none");
		$("#AI_chat_div").css("display","block");
		$("#black_filter").css("display","block");
	});
	
	//채널 나가기
	$(".channel_getout_btn").click(function(){
		const $btn = $(this);
		let userId = loginId;
		let channelIdx = $(this).data("channel_idx");
		
		if(channelIdx==1) {
			$(".channel_right_click_menu_div").css("display","none");
			alert("테스트용 설정 방이라서 삭제가 불가합니다.");
			return;
		}
		$(".channel_right_click_menu_div").css("display","none");
		if(confirm("채널에서 나가시겠습니까?")) {
			$.ajax({
				type: "post",
				url: "channel_delete",
				data: JSON.stringify({
					"UserId": userId,
					"ChannelIdx": channelIdx
				}),
				contentType:"application/json; charset=utf-8",
				dataType: "json",
				success: function(data) {
					console.log(channelIdx+"삭제됨");
					location.reload();
				},
				error:function(request,status,error) {
					alert("UserId"+userId);
					alert("ChannelIdx"+channelIdx);
					alert("[에러] code:"+request.status
					+"\nmessage:" +request.responseText+"\nerror:" + error);
				}
			});
			console.log("채널 퇴장 완료");
		}else {
			console.log("채널 퇴장하지 않았습니다.");
		}
	});
	
	//즐겨찾기 추가/제거
	$(".channel_favorite_btn").click(function(){
		let userId = loginId;
		const $btn = $(this);
		let channelIdx = $(this).data("channel_idx");
		$.ajax({
			type: "post",
			url: "user_favorite",
			data: JSON.stringify({
				"UserId": userId,
				"ChannelIdx": channelIdx
			}),
			contentType:"application/json; charset=utf-8",
			dataType: "json",
			success: function(data) {
				// 현재 조작하려는 채널 요소를 선택
	            let $targetItem = $(`.channel_item_${channelIdx}`);
	            let channelName = $targetItem.find('span').text();

	            if(data.status === "inserted") {
	            	// [즐겨찾기 추가 시]
	                // 1. 요소를 복제해서 즐겨찾기 컨테이너에 추가
	                let $newItem = $targetItem.first().clone();
	                $("#favorite_list_container").append($newItem);
	                
	                // 2. 일반 목록에서는 제거 (실시간 이동 효과)
	                $targetItem.remove();
	                $(".channel_right_click_menu_div").css("display","none");

	            } else if(data.status === "deleted") {
	            	// [즐겨찾기 해제 시]
	                
	                // 1. 즐겨찾기 목록에서 해당 채널 요소를 찾음
	                let $favItem = $("#favorite_list_container").find(`.channel_item_${channelIdx}`);
	                
	                if ($favItem.length > 0) {
	                    // 2. detach()로 요소를 떼어냄 (이벤트와 데이터는 그대로 유지)
	                    let $movingItem = $favItem.detach();
	                    
	                    // 3. 일반 채널 목록 컨테이너의 맨 뒤(append)에 추가
	                    $("#normal_channel_list_container").append($movingItem);
	                    
	                    console.log(`채널 ${channelIdx}번이 일반 목록 맨 뒤로 이동되었습니다.`);
	                }

	                // 4. 우클릭 메뉴 숨기기
	                $(".channel_right_click_menu_div").hide();
	            }
			},
			error:function(request,status,error) {
				alert("[에러] code:"+request.status
				+"\nmessage:" +request.responseText+"\nerror:" + error);
			}
		});
	});
	
	//워크스페이스 사용자 검색
	$("#directory_search_member").on("keyup", function(e) {
		if(e.keyCode=== 13) {
			let search = $(this).val();
			let userId = loginId;
			let workspaceIdx = globalWorkspaceIdx;
			if(search.trim() === "") {
				 location.reload();
			}
			$.ajax({
		    	type: "post", 
				url: "search_directory_member", 
				data: JSON.stringify({
					"Search": search,
					"WorkspaceIdx": workspaceIdx
				}),
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				success: function(data) { //성공시
		    		$('.profile').css("display","none");
		    		let users = data.list;
		    		
		    		if(users && users.length > 0) {
		    			users.forEach(user => {
		    				let profile = user.PROFILEIMAGE;
		    				 let profileUrl = "";
		    				 if(1<=profile&&profile<=10) {
		    					 profileUrl = "display?filename=ProfileImg"+profile+".png";
		    				 }else {
		    					 profileUrl = "display?filename=" + profile;
		    				 }
		    				let userHtml = `
		    							<div class="profile" data-user_id="${user.USERID}">
		    								<img src=${profileUrl}/>
		    									<div>${user.NICKNAME}
		    										<span><svg data-i0m="true" data-qa="presence_indicator" aria-hidden="false" title="온라인" aria-label="온라인" data-qa-type="status-member-filled" data-qa-presence-self="true" data-qa-presence-active="true" data-qa-presence-dnd="false" viewBox="0 0 20 20" class="is-inline" style="--s: 20px;"><path fill="currentColor" d="M14.5 10a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0"></path></svg></span>
		    								</div>
		    							</div>		
		    						`;
		    					
		    						$('#div_profile').append(userHtml);
		    			});
		    		} else {
	                    $('#div_profile').append('<p>검색 결과가 없습니다.</p>');
	                }
		    	},
				error: function(request, status, error) { //실패시
					alert("[에러] code:"+request.status
					+"\nmessage:" +request.responseText+"\nerror:" + error);
				}
		    });
		}
	});
});