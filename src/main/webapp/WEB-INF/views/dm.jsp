<%@page import="com.slack.dao.*"%>
<%@page import="com.slack.dto.*"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String loginId = (String)session.getAttribute("userId");
	List<DmLeftListDto> listLeft = (List<DmLeftListDto>) request.getAttribute("listLeft");
	List<DmListDto> listRight = (List<DmListDto>) request.getAttribute("listRight");
	List<DmUsersDto> userList = (List<DmUsersDto>) request.getAttribute("userList");
	List<SearchHistoryDto> historyList = (List<SearchHistoryDto>) request.getAttribute("historyList");
	DmDetailListDto dmFromToDto = (DmDetailListDto) request.getAttribute("dmFromToDto");
	String dmToByController = (String) request.getAttribute("dmTo");
	Object dto1 = (DmDetailListDto) request.getAttribute("dmFromToDto"); 
%>

<!DOCTYPE html>
<html>
<head>
	<script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
	<link rel="stylesheet" href="resources/css/dm.css" />
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100;300;400;500;700;900&display=swap" rel="stylesheet">
	<link href="https://cdn.quilljs.com/1.3.6/quill.snow.css" rel="stylesheet">
	<link rel="icon" href="resources/img/slackLogo.png"/>
	<script src="https://cdn.quilljs.com/1.3.6/quill.js"></script>
	<link href="https://cdn.jsdelivr.net/npm/quill-mention@3.1.0/dist/quill.mention.min.css" rel="stylesheet">
	<script src="https://cdn.jsdelivr.net/npm/quill-mention@3.1.0/dist/quill.mention.min.js"></script>
	<meta charset="UTF-8">
	<title>다이렉트 메시지</title>
	<style>
		div.dm_message_content a {
		    color: white !important;
		    text-decoration: none !important;
		    display: inline-block;
		    max-width: 100%;
		    overflow: hidden;
		    
		}
		div.second_child a {
			color: black;
			text-decoration: none !important;
		}
		.ql-editor {
			width: 800px;
		}
	</style>
<script>
		const globalWorkspaceIdx = "<%=request.getParameter("workspace_idx")%>";
		const loginId = "<%= loginId %>";
		let currentDmTo = null;
		if('<%=dmFromToDto!=null%>'=='true') {
			if('<%=dmFromToDto!=null ? dmFromToDto.getDmTo() : ""%>'==loginId) {
				currentDmTo = '<%=dmFromToDto!=null ? dmFromToDto.getDmFrom() : ""%>';
			} else {
				currentDmTo = '<%=dmFromToDto!=null ? dmFromToDto.getDmTo() : ""%>';
			}
		}
		let quill;
		let workspaceIdx = 1;
		let targetId = ""; /*디엠 세메시지 받는 사람 */ 
		let loginUserId = '<%=(String)session.getAttribute("userId")%>';
		$(function(){
			$(".dm_message_content").click(function(){ 
				let dmIdx = $(this).data("dm_idx");
				location.href = "dm?dm_idx=" + dmIdx + "&workspace_idx=" + globalWorkspaceIdx;
			});
			quill = new Quill('#editor', {
	        theme: 'snow',
	        placeholder: '@를 누르면 멘션기능이 활성화됩니다. 파일보내기는 에디터에 앨범아이콘을 눌러주시면 됩니다.', 
	        
	        modules: {
	            // 2. 툴바 설정
	            toolbar: {
	            	container : [
	                ['bold', 'italic', 'underline'],
	                ['image'],
	                [{ 'list': 'ordered'}, { 'list': 'bullet' }]
	            ], 
					handlers: {
						image: function(){
							fileHandler();
						}
					}
	            },
	            // 3. 멘션 설정
	            mention: {
	                allowedChars: /^[A-Za-z0-9_]*$/,
	                mentionDenotationChars: ["@"],
	                source: function (searchTerm, renderList, mentionChar) {
	                	$.ajax({
	                        url: 'getMentionList',
	                        type: 'POST',
	                        contentType: 'application/json',
	                        data: JSON.stringify({ "workspaceIdx": globalWorkspaceIdx }),
	                        success: function (data) {
	                            // 2. 서버에서 받은 데이터(data)를 values에 대입
	                            let values = data;

	                            // 3. 필터링 로직 (success 함수 내부)
	                            if (searchTerm.length === 0) {
	                                renderList(values, searchTerm);
	                            } else {
	                                const matches = values.filter(item => 
	                                    item.value.toLowerCase().includes(searchTerm.toLowerCase())
	                                );
	                                renderList(matches, searchTerm);
	                            }
	                        } 
	                    }); 
	                } 
	            } 
	        } 
	    }) 
	 		var newDmQuill = new Quill('#new_dm_editor', {
				  theme: 'snow', //에디터의 테마 
				  modules: {
				    toolbar: [
				      ['bold', 'italic', 'underline'], // 굵게, 기울이기, 밑줄
				      ['image', 'link'],               // 이미지, 링크
				      [{ 'list': 'ordered'}, { 'list': 'bullet' }] // 리스트
				    ]
				  },
				  placeholder: '메시지를 입력하세요...'
				});
	 		//const webSocket = new WebSocket("ws://localhost:9090/slack/broadcasting");
	 		//웹소켓 사용하기
			const host = window.location.host;
			
			const contextPath = "/slack";
			
			const webSocket = new WebSocket("ws://"+host+contextPath+"/broadcasting");
			// --> '접속' (접속하는 데 문제가 있으면, 여기서 에러남.)
			webSocket.onmessage = function(e) {
				console.log(e);
				// 메시지를 받았을 때.
				if (!event.data || event.data.trim() === "") { // data에 담긴게 없으면 리턴 
			  		return; 
    			}
				let data = JSON.parse(e.data);
				
				let found = false;  // 발견했으면 true로 변경.
				$(".dm_message_content").each(function(idx, item) {
					if(data.dmTo == $(item).data("dm_to")) {
						found = true;
					} 	
				});
				if(!found) {
					location.href = "dm?dm_to=" + data.dmTo + "&workspace_idx=" + globalWorkspaceIdx;
					return;
				}
					let profile = data.fromProfileImage;
				 	let profileUrl = "";
				 	if(1<=profile&&profile<=10) {
				 		profileUrl = "display?filename=ProfileImg"+profile+".png";
				 	}else {
				 		profileUrl = "display?filename=" + profile;
				 	}
					let str = `
						<div class="second_child">
						<div class="fl">
							<img src="\${profileUrl}" style="width: 36px;"/>
						</div>
						<div class="chat_profile fl">
							<div class="fl">
								<span>\${data.fromNickname}</span>
							</div>	
							<div class="fl">	
								<span>\${data.sentTime}</span>
							</div>	
							<div style="clear:both;"></div>
						</div>
						<div style="clear:both;"></div>
						<div>\${data.content}</div> 
					</div>
					`;
				$(".chat_area").append(str);
			/* 	}  */
			};
			webSocket.onopen = function(e) {
				console.log("연결되었습니다.");
			};
			webSocket.onerror = function(e) { 
				alert("error!");
				console.log(e);
			};
			
			$(function() {
				$(".btn_1").click(function() {	// .btn_1 : 오른쪽 세부내용 열려있을 때 [보내기] 버튼. 
					let msg = quill.root.innerHTML;
					console.log(msg);
			        let chatData = {
			        	"type":"dm",
			        	"content": msg,
			            "dmFrom" : loginUserId,
			            "dmTo" : currentDmTo,
			            "workspaceIdx": globalWorkspaceIdx,
			        }; 
			        
					webSocket.send(JSON.stringify(chatData));
					quill.root.innerHTML = '';
				});
			});
			$(function() {
				$(".btn_2").click(function() {  // .btn_2 : 새 메시지 보낼 때 [보내기] 버튼.
					let msg = newDmQuill.root.innerHTML;
			        let chatData = {
		        		"type":"dm",
			        	"content": msg,
			            "dmFrom" : loginUserId, 
			            "dmTo" : targetId,
			            "workspaceIdx": globalWorkspaceIdx
			        }; 
			        
					webSocket.send(JSON.stringify(chatData));
					newDmQuill.root.innerHTML = ("");
					setTimeout(function() {
				        location.href = "dm?dm_to=" + encodeURI(targetId) + "&workspace_idx=" + globalWorkspaceIdx;
				    }, 500);
				});
			});
	 		$(".new_message_list").click(function(){
	 			targetId = $(this).data("user_id");
				let userBox = $(this).clone();
	 			$(".new_message_input").css("display","none");
	 			$(".new_message_input").parent().append(userBox);
	 			userBox.append("<button class='remove_userBox' style='margin-left: 16px; margin-top: 7px; border: none; background-color: transparent; color: lightgrey; cursor: pointer;' >X</button>");
	 			$(".remove_userBox").click(function(){
		 			$(userBox).css("display","none");
		 			$(".new_message_input").css("display","block");
		 		}); 
	 			// 있으면 ----> ajax로 '왼쪽'에 있는지 물어봐... ---> 있다고 하면 dm?dm_to=...로 이동 / 없다고 하면 아무 것도 안 함. 
				$.ajax({
					type:'post',
					url: 'dm/getDmIdxByUserId',
					data: JSON.stringify({
						"targetId" : targetId,
						"workspaceIdx" : globalWorkspaceIdx
					}),
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					success: function(data){ 
						console.log(data);
						  // 처음이면 null.
						if(data.dmIdxNew!=null) {
							location.href = "dm?dm_idx=" + data.dmIdxNew + "&workspace_idx=" + data.workspaceIdx;
						}else{
							window.selectedTargetId = targetId; 
					        
					        $("#new_message_list_container").css('display', 'none');
					        $(".chat_target_name").text(targetId); // 채팅창 상단 이름 변경 예시
						}
					},
					error: function(request, status, error){
						alert("에러뜸");
					} 
				});
	 		});
	 		$(document).on("click", ".history_delete_btn", function(){
	 			let searchIdx = $(this).parent().data("search_history_idx");// searchIdx가 이 버튼보다 위에 코드에 있어서 this.parent로 해줘야한다.
	 			$.ajax({
					type:'post',
					url: 'dm/searchDelete',
					data: JSON.stringify({
						"searchIdx" : searchIdx,
					}),
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					success: function(data){
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
	 		$(".move_home_btn").click(function(){
	 			location.href="homeDirectory?workspace_idx=" + globalWorkspaceIdx ;
	 		});
			$(".move_dm_btn").click(function(){
				location.href="dm?workspace_idx=" + globalWorkspaceIdx;
	 		});
			$(".move_myactivity_btn").click(function(){
				location.href="myActivity?workspace_idx=" + globalWorkspaceIdx;
	 		});
			$(".move_file_btn").click(function(){
				location.href="file?workspace_idx=" + globalWorkspaceIdx;
	 		});
			$(".workspace_list_btn").click(function(){ //워크스페이스 리스트 화면으로 이동
				location.href="workspaceList";
			});
			$(document).off("keydown", "#dm_content_input").on("keydown", "#dm_content_input", function(e){
				if(e.keyCode === 229){
					return;
				}
				if(e.key === 'Enter'){
					let keyword = $(this).val();
					searchDm(keyword);
				}
			});
			$(".x_btn").click(function(){ // x버튼 클릭시 팝업창 디스플레이 넌 
				$(this).closest(".popup_window").css("display","none");
				$("#dm_not_content").css("display","flex"); 
				$(".dm_message_content").removeClass("on");
				$("#new_message_list_container").css("display","none");
				$("#dm_black_filter").css("display","none");
			});
			$(".new_message_btn").click(function(){ // 새 메시지 작성 버튼 
				$("#dm_not_content").css("display","none");
				$(".dm_main_content").css("display","none");
				$("#new_message").css("display","block");
				setTimeout(function() {  
		            newDmQuill.update();
		            newDmQuill.focus();
		        }, 100);
			});
			$(".new_message_input").click(function(){ 
				
				$("#workspace_content").css("display","none");
				$("#workspace_content_profile").css("display","block");
				if($("#new_message_list_container").css("display")=="none"){
				$("#new_message_list_container").css("display","block")
				}else
					$("#new_message_list_container").css("display","none")
				$("#dm_clear_filter").css("display","block"); 
				
			});
			$("#dm_clear_filter").click(function(){ // 투명필터 클릭 시 팝업창 디스플레이 넌 
				$("#new_message_list_container").css("display","none");
				$("#dm_clear_filter").css("display","none");
			});
			$(".dm_share_btn").click(function(){ // 파일 공유하기 버튼 클릭 시 팝업창 디스플레이 블락 
				$("#dm_popup_share_file").css("display","block");
				$("#dm_black_filter").css("display","block");
			});
	 	});
	 	function searchDm(keyword){
	 		$.ajax({
				type:'post',
				url: 'dm/data',
				data: JSON.stringify({
					"keyword" : keyword,
				}),
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				success: function(data){
					console.log(data)
					let list = data.dmList;
					useSearchDmList(list);
				},
				error: function(request, status, error){
					alert("[에러] code : " + request.status
							+ "/nmessage:" + request.responseText
							+ "/nerror:" + error);
				}
			});
	 	}
	 	function useSearchDmList(list){
	 		let $searchDmList = $(".dm_message_content").parent();
	 		//$searchDmList.empty();
	 		$("#dm_side_menu > .dm_message_content").remove();
	 		
	 		if(list.length == 0 ){
	 			$searchDmList.append("<div>검색결과없음</div>")
	 			return
	 		}
	 		let html = "";
	 		
	 		for(let i=0; i<list.length; i++){
	 			let item = list[i];
	 			html += "<div class='dm_message_content' data-dm_idx='" + item.dmIdx + "'>";
	 			html += "<div>";
	 			html +=     "<div class='fl' style='position: relative;'>";
	 			html +=         "<img src='resources/img/ProfileImg1.png' style='width: 36px; border-radius: 4px;'>";
	 			html +=         "<svg data-i0m='true' data-qa='presence_indicator' aria-hidden='false' title='온라인' aria-label='온라인' data-qa-type='status-member-filled' viewBox='0 0 20 20' color: #2bac76;'>";
	 			html +=             "<path fill='currentColor' d='M14.5 10a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0'></path>";
	 			html +=         "</svg>";
	 			html +=     "</div>";
	 			html +=     "<div class='fl'>";
	 			html +=         "<span style='font-weight: bold;'>" + (item.nickname || "알 수 없음") + "</span>";
	 			html +=         "<span>" + (item.sentTime || "") + "</span>";
	 			html +=         "<div>" + (item.content || "") + "</div>";
	 			html +=     "</div>";
	 			html +=     "<div style='clear: both;'></div>";
	 			html += "</div>";
	 			html += "</div>";
	 			
	 	    }
	 		$searchDmList.append(html);
	 	}
	 	function fileHandler(){
	 		const fileInput = document.createElement("input");
	 		fileInput.type = "file";
	 		
	 		fileInput.click();
	 		fileInput.onchange = function(){
	 			const selectedFile = fileInput.files[0]
	 			if (selectedFile){
	 				console.log ("선택된 파일 : " + selectedFile.name);
	 				saveToServer(selectedFile);
	 			}
	 		}
	 	}	
	 	function insertToEditor(response){
	 		const range = quill.getSelection();
	 		if(response.isImage){
	 			quill.insertEmbed(range.index, "image", response.url);
	 		} else {
	 			const fileName = "📎" + " "  + response.fileName;
	 			quill.insertText(range.index, fileName, "link", response.url);
	 			quill.setSelection(range.index + fileName.length);
	 		}
	 	}
	 	function saveToServer(file){
	 		if(file === undefined){
	 			console.log("파일이 제대로 전달되지않았습니다.");
	 			return;
	 		}
	 		const formData = new FormData();
	 	    formData.append('uploadFile', file);
	 	    formData.append('otherId', otherId);
	 	    formData.append('workspace_idx',globalWorkspaceIdx);
	 	   $.ajax({
	 	        url: 'uploadChatFile', 
	 	        type: 'POST',
	 	        data: formData,
	 	        contentType: false, // 파일 전송 시 필수: 자동 설정 방지
	 	        processData: false, // 파일 전송 시 필수: 문자열 변환 방지
	 	        success: function(response) {
	 	            
	 	            insertToEditor(response); 
	 	            
	 	        },
	 	        error: function() {
	 	            alert("파일 업로드에 실패했습니다.");
	 	        }
	 	    });
	 	}
	 	window.onbeforeunload = function() {
            $("#loadingOverlay").addClass("visible");
        };
                // 2. 페이지 로드 완료 시
        $(window).on('load', function() {
            $("#loadingOverlay").removeClass("visible");
        });
	</script>
<style>
#dm_clear_filter {
	width: 100vw;
	height: 100vh;
	background-color: transparent;
	z-index: 10;
	position: absolute;
}
</style>
</head>
<body>
	<jsp:include page="searchAll.jsp" />
	<div id="loadingOverlay">
   		<div class="loading-box">
        	<div class="loading-box-content">
            <div class="loader"></div>
            <p>데이터 로딩 중...</p>
            </div>
    	</div>
	</div>
	<div id="dm_clear_filter" style="display: none;"></div>
	<div id="header" class="search_btn">
		<div>
			<h3 class="test_text" style="color: red;">전체 검색-></h3>
			<svg data-i0m="true" data-qa="search" aria-hidden="true"
				viewBox="0 0 20 20" class="">
				<path fill="currentColor" fill-rule="evenodd"
					d="M9 3a6 6 0 1 0 0 12A6 6 0 0 0 9 3M1.5 9a7.5 7.5 0 1 1 13.307 4.746l3.473 3.474a.75.75 0 1 1-1.06 1.06l-3.473-3.473A7.5 7.5 0 0 1 1.5 9"
					clip-rule="evenodd"></path></svg>
			<span>전체검색</span>
		</div>
	</div>
	<div id="dm_sidebar" class="fl">
		<div class="workspace_list_btn"><img src="resources/img/slackLogo.png" style="width: 36px;"/></div>
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
		<div class="on">
			<div class="move_dm_btn">
				<svg data-i0m="true" data-qa="direct-messages-filled"
					aria-hidden="true" viewBox="0 0 20 20" class="" style="-s: 20px;">
					<path fill="currentColor" fill-rule="evenodd"
						d="M12.25 1.5a6.25 6.25 0 0 0-6.207 5.516Q6.269 7 6.5 7a6.5 6.5 0 0 1 6.484 6.957 6.2 6.2 0 0 0 1.867-.522l2.752.55a.75.75 0 0 0 .882-.882l-.55-2.752A6.25 6.25 0 0 0 12.25 1.5m-.75 12a5 5 0 1 0-9.57 2.03l-.415 2.073a.75.75 0 0 0 .882.882l2.074-.414a5 5 0 0 0 7.03-4.57"
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
	<div id="dm_side_menu" class="fl">
		<div>
			<span>다이렉트 메시지</span>
			<h4 class="test_text" style="color: red;    position: absolute; left: 407px; top: 33px;">새메세지 보내기--></h4>
			<div class="new_message_btn fr">
				<svg data-i0m="true" data-qa="compose" aria-hidden="true"
					viewBox="0 0 20 20" class="">
					<path fill="currentColor" fill-rule="evenodd"
						d="M16.707 3.268a1 1 0 0 0-1.414 0l-.482.482 1.439 1.44.482-.483a1 1 0 0 0 0-1.414zM15.19 6.25l-1.44-1.44-5.068 5.069-.431 1.872 1.87-.432zm-.957-4.043a2.5 2.5 0 0 1 3.536 0l.025.025a2.5 2.5 0 0 1 0 3.536l-6.763 6.763a.75.75 0 0 1-.361.2l-3.25.75a.75.75 0 0 1-.9-.9l.75-3.25a.75.75 0 0 1 .2-.361zM5.25 4A2.25 2.25 0 0 0 3 6.25v8.5A2.25 2.25 0 0 0 5.25 17h8.5A2.25 2.25 0 0 0 16 14.75v-4.5a.75.75 0 0 1 1.5 0v4.5a3.75 3.75 0 0 1-3.75 3.75h-8.5a3.75 3.75 0 0 1-3.75-3.75v-8.5A3.75 3.75 0 0 1 5.25 2.5h4.5a.75.75 0 0 1 0 1.5z"
						clip-rule="evenodd"></path></svg>
			</div>
			<div style="clear: both;"></div>
		</div>
		<div>
			<span><svg data-i0m="true" data-qa="search" aria-hidden="true"
					viewBox="0 0 20 20" class="" style="-s: 20px;">
					<path fill="currentColor" fill-rule="evenodd"
						d="M9 3a6 6 0 1 0 0 12A6 6 0 0 0 9 3M1.5 9a7.5 7.5 0 1 1 13.307 4.746l3.473 3.474a.75.75 0 1 1-1.06 1.06l-3.473-3.473A7.5 7.5 0 0 1 1.5 9"
						clip-rule="evenodd"></path></svg></span> <input type="text"
				id="dm_content_input" placeholder="DM 찾기" />
		</div>

		<%
		for (DmLeftListDto dto : listLeft) {
			String sentTime = dto.getSentTime();
			int month = Integer.parseInt(sentTime.substring(5, 7));
			int day = Integer.parseInt(sentTime.substring(8, 10));
			sentTime = month + "월 " + day + "일";
			String msg = dto.getContent().substring(0, dto.getContent().indexOf("</p>") + 4);
			if(msg.contains("<img")) {
				String filename = msg.substring(msg.indexOf("/upload/")+8, msg.indexOf("\"", msg.indexOf("/upload/")+8));
				msg = "(파일명 : " + filename + ")";   // 임시.
			}
		%>
		<div class="dm_message_content" data-dm_idx="<%=dto.getDmIdx()%>"
			data-dm_to="<%=dto.getDmTo()%>">
			<div>
				<div class="fl">
				<%
					String newProfileImage = dto.getProfileImage();
					int defaultProfileImage = -1;
					try {
						defaultProfileImage = Integer.parseInt(newProfileImage);
					} catch(Exception e) { }
					if(defaultProfileImage>=1 && defaultProfileImage<=10) { 
				%>
					<img src="display?filename=ProfileImg<%=defaultProfileImage%>.png" style="width: 36px;" />
				<% }else { %>
					<img src="display?filename=<%=newProfileImage%>" style="width: 36px;" />
				<% } %>
					<svg data-i0m="true" data-qa="presence_indicator"
						aria-hidden="false" title="온라인" aria-label="온라인"
						data-qa-type="status-member-filled" data-qa-presence-self="true"
						data-qa-presence-active="true" data-qa-presence-dnd="false"
						viewBox="0 0 20 20" class="">
						<path fill="currentColor"
							d="M14.5 10a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0"></path></svg>
				</div>
				<div class="fl">
					<span><%=dto.getNickname()%></span><span><%=sentTime%></span>
					<div><%=msg%></div>
				</div>
				<div style="clear: both;"></div>
			</div>
		</div>
		<%
		}
		%>
	</div>
	<%
	if (request.getParameter("dm_idx") == null) {
	%>
	<div id="dm_not_content" class="fl">
		<img class="p-empty_page__illustration__img" alt="말풍선 이미지"
			src="https://a.slack-edge.com/bv1-13-br/empty-dms-light-27b960c.svg">
	</div>
	<%
	}
	%>
	<div id="new_message" class="popup_window fl" style="display: none;">
		<div>
			<div>
				<span>새 메시지</span> <span><svg class="x_btn" data-i0m="true"
						data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class="">
						<path fill="currentColor" fill-rule="evenodd"
							d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0"
							clip-rule="evenodd"></path></svg></span>
			</div>
			<div>
				<div>
					<input type="text" class="new_message_input"
						placeholder="대상: #예시-채널, @사람이름 또는 사람이름@예시.com" />
				</div>
			</div>
			<div></div>
			<div class="chat_area"></div>
			<div id="new_dm_editor" style="height: 80px; cursor: text;"></div>
			<div style="margin-left: 800px; margin-top: 5px; width: 100%;">
				<button class="btn_2"
					style="width: 32px; height: 32px; border: none; background-color: transparent; cursor: pointer; color: rgb(187, 187, 187);">
					<svg data-mx3="true" data-qa="send-filled" aria-hidden="true"
						viewBox="0 0 20 20" class="">
						<path fill="currentColor"
							d="M1.5 2.106c0-.462.498-.754.901-.528l15.7 7.714a.73.73 0 0 1 .006 1.307L2.501 18.46l-.07.017a.754.754 0 0 1-.931-.733v-4.572c0-1.22.971-2.246 2.213-2.268l6.547-.17c.27-.01.75-.243.75-.797 0-.553-.5-.795-.75-.795l-6.547-.171C2.47 8.95 1.5 7.924 1.5 6.704z"></path></svg>
				</button>
			</div>
		</div>
	</div>
	<div id="new_message_list_container" class="popup_window"
		style="display: none;">
		<!-- 새 메시지 보내기에서 인풋 누르면 뜨는 창 -->
		<div>
			<%
			for (DmUsersDto dto : userList) {
			%>
			<div class="new_message_list" data-user_id="<%=dto.getUserId()%>">
				<div>
					<%
                        String newProfileImage = dto.getProfileImage();
                        int defaultProfileImage = -1;
                        try {
                           defaultProfileImage = Integer.parseInt(newProfileImage);
                        } catch(Exception e) { }
                        if(defaultProfileImage>=1 && defaultProfileImage<=10) { 
                     %>
                        <img src="display?filename=ProfileImg<%=defaultProfileImage%>.png" style="width: 20px;"/>
                     <% }else { %>
                        <img src="display?filename=<%=newProfileImage%>" style="width: 20px;"/>
                     <% } %>
				</div>
				<div>
					<strong><%=dto.getNickname()%></strong><span><%=dto.getName()%></span>
				</div>
			</div>
			<%
			}
			%>
		</div>
	</div>
	<%
	if (listRight != null) {
	%>
	<script>
		const otherId = "<%=listRight.get(0).getDmToId()%>";
		console.log(otherId);
	</script>
	<div class="dm_main_content fl popup_window" style="display: block;">
		<div>
			<div>
				<%
					String newProfileImage = listRight.get(0).getToProfileImage();
					int defaultProfileImage = -1;
					try {
						defaultProfileImage = Integer.parseInt(newProfileImage);
					} catch(Exception e) { }
					if(defaultProfileImage>=1 && defaultProfileImage<=10) { 
				%>
					<img src="display?filename=ProfileImg<%=defaultProfileImage%>.png" style="width: 24px;"/>
				<% }else { %>
					<img src="display?filename=<%=newProfileImage%>" style="width: 24px;"/>
				<% } %>
				<svg data-i0m="true" data-qa="presence_indicator"
					aria-hidden="false" title="온라인" aria-label="온라인"
					data-qa-type="status-member-filled" data-qa-presence-self="true"
					data-qa-presence-active="true" data-qa-presence-dnd="false"
					viewBox="0 0 20 20" class="">
					<path fill="currentColor"
						d="M14.5 10a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0"></path></svg>
				<span><%=listRight.get(0).getDmTo()%></span>
			</div>
			<button class="x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true"
					viewBox="0 0 20 20" class="">
					<path fill="currentColor" fill-rule="evenodd"
						d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0"
						clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="dm_main_profile">
			<div class="fl">
				<%
					try {
						defaultProfileImage = Integer.parseInt(newProfileImage);
					} catch(Exception e) { }
					if(defaultProfileImage>=1 && defaultProfileImage<=10) { 
				%>
					<img src="display?filename=ProfileImg<%=defaultProfileImage%>.png" style="width: 102px;"/>
				<% }else { %>
					<img src="display?filename=<%=newProfileImage%>" style="width: 102px;"/>
				<% } %>
				<span><strong><%=listRight.get(0).getDmTo()%></strong></span>
			</div>
			<div class="fl">
				<svg data-i0m="true" data-qa="presence_indicator"
					aria-hidden="false" title="온라인" aria-label="온라인"
					data-qa-type="status-member-filled" data-qa-presence-self="false"
					data-qa-presence-active="true" data-qa-presence-dnd="false"
					viewBox="0 0 20 20" class="is-inline" style="-s: 20px;">
					<path fill="currentColor"
						d="M14.5 10a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0"></path></svg>
			</div>
			<div style="clear: both;"></div>
		</div>
		<div>
			이 대화는 나와<span>@<%=listRight.get(0).getDmTo()%></span>님 간의 대화입니다. 상대방에
			대해 자세히 알아보려면 디렉터리를 확인하세요.
		</div>
		<div>
			<%
			for (DmListDto dto : listRight) {
				String sentTime = dto.getSentTime();
				int month = Integer.parseInt(sentTime.substring(5, 7));
				int day = Integer.parseInt(sentTime.substring(8, 10));
				int time = Integer.parseInt(sentTime.substring(11, 13));
				int minute = Integer.parseInt(sentTime.substring(14, 15));
				sentTime = month + "월 " + day + "일" + " " + time + "시" + minute + "분";
			%>
			<div class="second_child">
				<div class="fl">
				<%
					newProfileImage = dto.getFromProfileImage();
					defaultProfileImage = -1;
					try {
						defaultProfileImage = Integer.parseInt(newProfileImage);
					} catch(Exception e) { }
					if(defaultProfileImage>=1 && defaultProfileImage<=10) { 
				%>
					<img src="display?filename=ProfileImg<%=defaultProfileImage%>.png" style="width: 36px;"/>
				<% }else { %>
					<img src="display?filename=<%=newProfileImage%>" style="width: 36px;"/>
				<% } %>
				</div>
				<div class="chat_profile fl">
					<div class="fl">
						<span><%=dto.getDmFrom()%></span>
					</div>
					<div class="fl">
						<span><%=sentTime%></span>
					</div>
					<div style="clear: both;"></div>
				</div>
				<div style="clear: both;"></div>
				<div><%=dto.getContent()%></div>
			</div>
			<%
			}
			%>
			<!--에디터-->
			<div class="chat_area"></div>
			<div id="editor" style="height: 80px;"></div>
			<div style="text-align: right; margin-top: 5px;">
				<button class="btn_1"
					style="width: 32px; height: 32px; border: none; background-color: transparent; cursor: pointer; color: rgb(187, 187, 187);">
					<svg data-mx3="true" data-qa="send-filled" aria-hidden="true"
						viewBox="0 0 20 20" class="">
						<path fill="currentColor"
							d="M1.5 2.106c0-.462.498-.754.901-.528l15.7 7.714a.73.73 0 0 1 .006 1.307L2.501 18.46l-.07.017a.754.754 0 0 1-.931-.733v-4.572c0-1.22.971-2.246 2.213-2.268l6.547-.17c.27-.01.75-.243.75-.797 0-.553-.5-.795-.75-.795l-6.547-.171C2.47 8.95 1.5 7.924 1.5 6.704z"></path></svg>
				</button>
			</div>
		</div>
		<div style="clear: both;"></div>
		<%
		}
		%>
		<div id="dm_black_filter" style="display: none;"></div>
		<!-- 여기서부터 전체검색 html -->
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
				%>
			</div>
		</div>
</body>
</html>
