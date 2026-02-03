<%@page import="com.slack.dao.*"%>
<%@page import="com.slack.dto.*"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String loginId = (String)session.getAttribute("userId");
	List<ChannelIdxNameDto> listFavoriteChannel = (List<ChannelIdxNameDto>)request.getAttribute("listFavoriteChannel");
	List<ChannelIdxNameDto> listMyChannelName = (List<ChannelIdxNameDto>)request.getAttribute("listMyChannelName");
	List<Users3Dto> listGetWorkspaceMembers = (List<Users3Dto>)request.getAttribute("listGetWorkspaceMembers");
	List<UsersProfileDto> listAllProfileSelect = (List<UsersProfileDto>)request.getAttribute("listAllProfileSelect");
	List<SearchHistoryDto> historyList = (List<SearchHistoryDto>) request.getAttribute("historyList");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="icon" href="resources/img/slackLogo.png"/>
	<meta charset="UTF-8">
	<title>홈 - 디렉터리</title>
	<script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100;300;400;500;700;900&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="resources/css/homeDirectory.css"/>
	<script src="resources/js/homeDirectory.js"></script>
	<link href="https://cdn.quilljs.com/1.3.6/quill.snow.css" rel="stylesheet">
	<script src="https://cdn.quilljs.com/1.3.6/quill.js"></script>
	<script>
		const globalWorkspaceIdx = <%=request.getParameter("workspace_idx")%>;
		const loginId = "<%= loginId %>";
	</script>
	<script>
	document.addEventListener("DOMContentLoaded", function() {
	    window.quill = new Quill('#editor', {
	        theme: 'snow',
	        modules: {
	            toolbar: {
	                container: [
	                    ['bold', 'italic', 'underline'],
	                    ['image', 'link']
	                ],
	                handlers: {
	                    // Quill의 기본 이미지 버튼 기능을 커스텀 fileHandler로 대체
	                    image: fileHandler 
	                }
	            }
	        },
	        placeholder: '메시지를 입력하세요...'
	    });
	});

	// 1. 파일 선택창 실행
	function fileHandler() {
	    const fileInput = document.createElement("input");
	    fileInput.type = "file";
	    // 이미지뿐만 아니라 다른 파일도 선택 가능하도록 설정
	    fileInput.setAttribute('accept', 'image/*, .pdf, .zip, .txt, .docx'); 
	    
	    fileInput.click();
	    fileInput.onchange = function() {
	        const selectedFile = fileInput.files[0];
	        if (selectedFile) {
	            saveToServer(selectedFile);
	        }
	    };
	}

	// 2. 서버로 파일 전송 (컨트롤러 파라미터명 매칭)
	function saveToServer(file) {
    const formData = new FormData();
    formData.append('uploadFile', file);
    
    // 1. otherId가 정의되어 있지 않으면 빈 문자열("")이나 "ALL"을 할당
    // typeof를 사용하면 변수가 아예 선언되지 않았을 때 발생하는 에러를 피할 수 있습니다.
    const senderOtherId = (typeof otherId !== 'undefined') ? otherId : ""; 
    
    // 2. globalWorkspaceIdx도 안전하게 체크
    const workspaceIdx = (typeof globalWorkspaceIdx !== 'undefined') ? globalWorkspaceIdx : 0;

    formData.append('otherId', senderOtherId); 
    formData.append('workspace_idx', workspaceIdx);

	    $.ajax({
	        url: 'uploadChatFile', 
	        type: 'POST',
	        data: formData,
	        contentType: false, 
	        processData: false, 
	        success: function(response) {
	            insertToEditor(response); 
	        },
	        error: function() {
	            alert("파일 업로드에 실패했습니다.");
	        }
	    });
	}

	// 3. 에디터 본문에 결과 삽입
	function insertToEditor(response) {
	    // 현재 커서 위치 확인 (없으면 맨 끝)
	    const range = quill.getSelection(true); 
	    
	    if (response.isImage) {
	        // 컨트롤러에서 resultMap.put("isImage", true)로 준 경우
	        quill.insertEmbed(range.index, "image", response.url);
	        quill.setSelection(range.index + 1); 
	    } else {
	        // 일반 파일일 경우 📎 아이콘과 파일명을 링크로 삽입
	        const fileNameWithIcon = "📎 " + response.fileName;
	        quill.insertText(range.index, fileNameWithIcon, "link", response.url);
	        quill.setSelection(range.index + fileNameWithIcon.length);
	    }
	}

	// 4. 전송 시 에디터 내용 저장
	function saveContent() {
	    const content = quill.root.innerHTML;
	    // 에디터가 비어있는 기본 상태(<p><br></p>)면 전송 중단
	    if (content === '<p><br></p>' || content.trim() === '') {
	        alert("내용을 입력해주세요.");
	        return;
	    }
	    
	    document.getElementById('content').value = content;
	    document.getElementById('editorForm').submit();
	}
	</script>
</head>
<body>
	
	<jsp:include page="searchAll.jsp" />
	<div id="header" class="search_btn">
		<div>
			<h3 class="test_text" style="color: red;">전체 검색-></h3>
			<svg data-i0m="true" data-qa="search" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M9 3a6 6 0 1 0 0 12A6 6 0 0 0 9 3M1.5 9a7.5 7.5 0 1 1 13.307 4.746l3.473 3.474a.75.75 0 1 1-1.06 1.06l-3.473-3.473A7.5 7.5 0 0 1 1.5 9" clip-rule="evenodd"></path></svg>
			<span class="workspace_name">전체검색</span>
		</div>
	</div>
	<div id="sidebar" class="fl">
		<!-- B2 -->
		<div class="workspace_list_btn"><img src="resources/img/slackLogo.png" style="width: 36px;"/></div>
		<!-- 홈 -->
		<div class="on">
			<div class="move_home_btn">
			<svg data-i0m="true" data-qa="home-filled" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="m3 7.649-.33.223a.75.75 0 0 1-.84-1.244l7.191-4.852a1.75 1.75 0 0 1 1.958 0l7.19 4.852a.75.75 0 1 1-.838 1.244L17 7.649v7.011c0 2.071-1.679 3.84-3.75 3.84h-6.5C4.679 18.5 3 16.731 3 14.66zM11 11a1 1 0 0 1 1-1h1a1 1 0 0 1 1 1v1a1 1 0 0 1-1 1h-1a1 1 0 0 1-1-1z" clip-rule="evenodd"></path></svg></div>
			<div>홈</div>
		</div>
		<!-- DM -->
		<div> 
			<div class="move_dm_btn">
			<svg data-i0m="true" data-qa="direct-messages" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M7.675 6.468a4.75 4.75 0 1 1 8.807 3.441.75.75 0 0 0-.067.489l.379 1.896-1.896-.38a.75.75 0 0 0-.489.068 5 5 0 0 1-.648.273.75.75 0 1 0 .478 1.422q.314-.105.611-.242l2.753.55a.75.75 0 0 0 .882-.882l-.55-2.753A6.25 6.25 0 1 0 6.23 6.064a.75.75 0 1 0 1.445.404M6.5 8.5a5 5 0 0 0-4.57 7.03l-.415 2.073a.75.75 0 0 0 .882.882l2.074-.414A5 5 0 1 0 6.5 8.5m-3.5 5a3.5 3.5 0 1 1 1.91 3.119.75.75 0 0 0-.49-.068l-1.214.243.243-1.215a.75.75 0 0 0-.068-.488A3.5 3.5 0 0 1 3 13.5" clip-rule="evenodd"></path></svg></div>
			<div>DM</div>
		</div>
		<!-- 내활동 -->
		<div> 
			<div class="move_myactivity_btn">
			<svg data-i0m="true" data-qa="notifications" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M9.357 3.256c-.157.177-.31.504-.36 1.062l-.05.558-.55.11c-1.024.204-1.691.71-2.145 1.662-.485 1.016-.736 2.566-.752 4.857l-.002.307-.217.217-2.07 2.077c-.145.164-.193.293-.206.374a.3.3 0 0 0 .034.199c.069.12.304.321.804.321h4.665l.07.672c.034.327.17.668.4.915.214.232.536.413 1.036.413.486 0 .802-.178 1.013-.41.227-.247.362-.588.396-.916l.069-.674h4.663c.5 0 .735-.202.804-.321a.3.3 0 0 0 .034-.199c-.013-.08-.061-.21-.207-.374l-2.068-2.077-.216-.217-.002-.307c-.015-2.291-.265-3.841-.75-4.857-.455-.952-1.123-1.458-2.147-1.663l-.549-.11-.05-.557c-.052-.558-.204-.885-.36-1.062C10.503 3.1 10.31 3 10 3s-.505.1-.643.256m-1.124-.994C8.689 1.746 9.311 1.5 10 1.5s1.31.246 1.767.762c.331.374.54.85.65 1.383 1.21.369 2.104 1.136 2.686 2.357.604 1.266.859 2.989.894 5.185l1.866 1.874.012.012.011.013c.636.7.806 1.59.372 2.342-.406.705-1.223 1.072-2.103 1.072H12.77c-.128.39-.336.775-.638 1.104-.493.538-1.208.896-2.12.896-.917 0-1.638-.356-2.136-.893A3 3 0 0 1 7.23 16.5H3.843c-.88 0-1.697-.367-2.104-1.072-.433-.752-.263-1.642.373-2.342l.011-.013.012-.012 1.869-1.874c.035-2.196.29-3.919.894-5.185.582-1.22 1.475-1.988 2.684-2.357.112-.533.32-1.009.651-1.383" clip-rule="evenodd"></path></svg></div>
			<div>내활동</div>
			</div>
		<!-- 파일 -->
		<div> 
			<div class="move_file_btn">
			<svg data-i0m="true" data-qa="canvas-browser" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M4.836 3A1.836 1.836 0 0 0 3 4.836v7.328c0 .9.646 1.647 1.5 1.805V7.836A3.336 3.336 0 0 1 7.836 4.5h6.133A1.84 1.84 0 0 0 12.164 3zM1.5 12.164a3.337 3.337 0 0 0 3.015 3.32A3.337 3.337 0 0 0 7.836 18.5h3.968c.73 0 1.43-.29 1.945-.805l3.946-3.946a2.75 2.75 0 0 0 .805-1.945V7.836a3.337 3.337 0 0 0-3.015-3.32A3.337 3.337 0 0 0 12.164 1.5H4.836A3.336 3.336 0 0 0 1.5 4.836zM7.836 6A1.836 1.836 0 0 0 6 7.836v7.328C6 16.178 6.822 17 7.836 17H11.5v-4a1.5 1.5 0 0 1 1.5-1.5h4V7.836A1.836 1.836 0 0 0 15.164 6zm8.486 7H13v3.322z" clip-rule="evenodd"></path></svg></div>
			<div>파일</div>
		</div>
	</div> <!-- //#sidebar -->
	<div id="side_menu" class="fl">
		<div><span class="workspace_name">워크스페이스이름</span></div>
		<div id="open_popup_directory" class="side_menu_item">
			<svg data-i0m="true" data-qa="user-directory" aria-hidden="true" viewBox="0 0 20 20" class="is-inline" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M4.75 1.5A1.75 1.75 0 0 0 3 3.25v.5a.75.75 0 0 0 1.5 0v-.5A.25.25 0 0 1 4.75 3h10c.69 0 1.25.56 1.25 1.25v11.5c0 .69-.56 1.25-1.25 1.25h-10a.25.25 0 0 1-.25-.25v-.5a.75.75 0 0 0-1.5 0v.5c0 .966.784 1.75 1.75 1.75h10a2.75 2.75 0 0 0 2.75-2.75V4.25a2.75 2.75 0 0 0-2.75-2.75zM2.25 6a.75.75 0 0 0 0 1.5h2a.75.75 0 0 0 0-1.5zm-.75 4a.75.75 0 0 1 .75-.75h2a.75.75 0 0 1 0 1.5h-2A.75.75 0 0 1 1.5 10m.75 2.5a.75.75 0 0 0 0 1.5h2a.75.75 0 0 0 0-1.5zm5.79.472.02.01q.037.016.09.018h4.7a.23.23 0 0 0 .11-.028 2.1 2.1 0 0 0-.736-.991c-.372-.271-.92-.481-1.724-.481-.805 0-1.353.21-1.724.481a2.1 2.1 0 0 0-.736.991m4.12-2.702q.117-.13.218-.268C12.784 9.437 13 8.712 13 8c0-1.624-1.287-2.5-2.5-2.5S8 6.376 8 8c0 .712.217 1.437.622 2.002q.1.139.219.268-.53.191-.949.5a3.6 3.6 0 0 0-1.285 1.755 1.42 1.42 0 0 0 .294 1.431 1.68 1.68 0 0 0 1.249.544h4.7a1.68 1.68 0 0 0 1.249-.544 1.42 1.42 0 0 0 .293-1.431 3.6 3.6 0 0 0-2.233-2.255M9.5 8c0-.65.463-1 1-1s1 .35 1 1c0 .426-.133.838-.34 1.127-.203.282-.434.398-.66.398s-.457-.116-.66-.398A2 2 0 0 1 9.5 8" clip-rule="evenodd"></path></svg>
			<span>디렉터리</span>
		</div>
		<div class="separator"></div>
		<div class="side_menu_item">
			<svg data-i0m="true" data-qa="star" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 16px;"><path fill="currentColor" fill-rule="evenodd" d="M9.044 4.29c-.393.923-.676 2.105-.812 3.065a.75.75 0 0 1-.825.64l-.25-.027c-1.066-.12-2.106-.236-2.942-.202-.45.018-.773.079-.98.167-.188.08-.216.15-.227.187-.013.042-.027.148.112.37.143.229.4.497.77.788.734.579 1.755 1.128 2.66 1.54a.75.75 0 0 1 .35 1.036c-.466.87-1.022 2.125-1.32 3.239-.15.56-.223 1.04-.208 1.396.015.372.113.454.124.461l.003.001a.2.2 0 0 0 .042.006.9.9 0 0 0 .297-.06c.297-.1.678-.319 1.116-.64.87-.635 1.8-1.55 2.493-2.275a.75.75 0 0 1 1.085 0c.692.724 1.626 1.639 2.5 2.275.44.32.822.539 1.12.64a.9.9 0 0 0 .3.06q.038-.003.044-.006h.002c.011-.009.109-.09.123-.46.013-.357-.06-.836-.212-1.397-.303-1.114-.864-2.368-1.33-3.24a.75.75 0 0 1 .35-1.037c.903-.41 1.92-.96 2.652-1.54.369-.292.625-.56.768-.787.139-.223.124-.329.112-.37-.012-.038-.039-.107-.226-.186-.206-.088-.527-.149-.976-.167-.835-.034-1.874.082-2.941.201l-.246.027a.75.75 0 0 1-.825-.64c-.136-.96-.42-2.142-.813-3.064-.198-.464-.405-.82-.605-1.048-.204-.232-.319-.243-.34-.243s-.135.01-.34.243c-.2.228-.407.584-.605 1.048m-.522-2.036c.343-.39.833-.754 1.467-.754s1.125.363 1.467.754c.348.396.63.914.858 1.449.359.84.627 1.83.798 2.723.913-.1 1.884-.192 2.708-.158.521.021 1.052.094 1.503.285.47.2.902.556 1.076 1.14.177.597-.004 1.153-.279 1.592-.271.434-.676.826-1.108 1.168-.662.524-1.482 1.003-2.256 1.392.41.85.836 1.884 1.1 2.856.17.625.286 1.271.264 1.846-.021.56-.182 1.218-.749 1.623-.555.398-1.205.316-1.7.148-.51-.173-1.034-.493-1.523-.849-.754-.55-1.523-1.261-2.158-1.896-.634.634-1.4 1.346-2.15 1.895-.487.356-1.01.677-1.518.85-.495.168-1.144.25-1.699-.148-.565-.405-.727-1.062-.75-1.62-.024-.574.09-1.22.257-1.846.261-.972.684-2.007 1.093-2.858-.775-.389-1.597-.867-2.262-1.39-.433-.342-.84-.734-1.111-1.168-.276-.44-.457-.997-.28-1.595.174-.585.608-.941 1.079-1.141.45-.191.983-.264 1.505-.285.826-.033 1.799.059 2.713.159.17-.893.439-1.882.797-2.723.228-.535.51-1.053.858-1.449" clip-rule="evenodd"></path></svg>
			<span>즐겨찾기</span>
		</div>
		<div id="favorite_list_container">
			<% for(ChannelIdxNameDto dto : listFavoriteChannel) {%>
				<div class="open_popup_channel side_menu_item channel_item_<%=dto.getChannelIdx()%>" data-channel_idx="<%=dto.getChannelIdx()%>">
					<svg data-i0m="true" data-qa="sidebar-channel-icon-prefix" aria-hidden="true" data-sidebar-channel-icon="channel" viewBox="0 0 20 20" class="" style="--s: 16px;"><path fill="currentColor" fill-rule="evenodd" d="M9.74 3.878a.75.75 0 1 0-1.48-.255L7.68 7H3.75a.75.75 0 0 0 0 1.5h3.67L6.472 14H2.75a.75.75 0 0 0 0 1.5h3.463l-.452 2.623a.75.75 0 0 0 1.478.255l.496-2.878h3.228l-.452 2.623a.75.75 0 0 0 1.478.255l.496-2.878h3.765a.75.75 0 0 0 0-1.5h-3.506l.948-5.5h3.558a.75.75 0 0 0 0-1.5h-3.3l.54-3.122a.75.75 0 0 0-1.48-.255L12.43 7H9.2zM11.221 14l.948-5.5H8.942L7.994 14z" clip-rule="evenodd"></path></svg>
					<span><%=dto.getChannelName() %></span>
				</div>
			<% } %>
		</div>
		<div class="side_menu_item">
			<svg data-i0m="true" data-qa="channel-section-collapse" aria-hidden="true" viewBox="0 0 20 20" class="is-inline"><path fill="currentColor" d="M13.22 9.423a.75.75 0 0 1 .001 1.151l-4.49 3.755a.75.75 0 0 1-1.231-.575V6.25a.75.75 0 0 1 1.23-.575z"></path></svg>
			<span>채널</span>
		</div>
		<div id="normal_channel_list_container">
			<% for(ChannelIdxNameDto dto : listMyChannelName) {%>
				<div class="open_popup_channel side_menu_item channel_item_<%=dto.getChannelIdx()%>" data-channel_idx="<%=dto.getChannelIdx()%>">
					<svg data-i0m="true" data-qa="sidebar-channel-icon-prefix" aria-hidden="true" data-sidebar-channel-icon="channel" viewBox="0 0 20 20" class="" style="--s: 16px;"><path fill="currentColor" fill-rule="evenodd" d="M9.74 3.878a.75.75 0 1 0-1.48-.255L7.68 7H3.75a.75.75 0 0 0 0 1.5h3.67L6.472 14H2.75a.75.75 0 0 0 0 1.5h3.463l-.452 2.623a.75.75 0 0 0 1.478.255l.496-2.878h3.228l-.452 2.623a.75.75 0 0 0 1.478.255l.496-2.878h3.765a.75.75 0 0 0 0-1.5h-3.506l.948-5.5h3.558a.75.75 0 0 0 0-1.5h-3.3l.54-3.122a.75.75 0 0 0-1.48-.255L12.43 7H9.2zM11.221 14l.948-5.5H8.942L7.994 14z" clip-rule="evenodd"></path></svg>
					<span><%=dto.getChannelName() %></span>
				</div>
			<% } %>
		</div>
		<div id="open_popup_channel_create" class="side_menu_item">
			<img src="resources/img/Ico_channel_plus.png"/>
			<span>채널 추가</span>
		</div>
		<!-- 추가 내용 -->
		<h3 style="color: red; margin-left: 20px;">채널 우클릭 버튼 있습니다.</h3>
	</div>
	<div id="main_content" class="fl">
		<h1>디렉터리</h1>
		<div>
			<div id="div_directory_search">
				<div>
					<!-- 돋보기 -->
					<svg data-i0m="true" data-qa="search" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M9 3a6 6 0 1 0 0 12A6 6 0 0 0 9 3M1.5 9a7.5 7.5 0 1 1 13.307 4.746l3.473 3.474a.75.75 0 1 1-1.06 1.06l-3.473-3.473A7.5 7.5 0 0 1 1.5 9" clip-rule="evenodd"></path></svg>
					<input id="directory_search_member" type="text" placeholder="사람 검색"/>
					<!-- x -->
					<span class="search_x_btn">x</span>
				</div>
				<span id="open_popup_member_invite">사용자 초대</span>
			</div>
			<div>
			</div>
			<div class="member_list_container">
				<div id="div_profile">
				<%for(Users3Dto dto : listGetWorkspaceMembers) { %>
					<div class="profile" data-user_id="<%=dto.getUserId()%>">
					<%
						String newProfileImage = dto.getProfileImage();
						int defaultProfileImage = -1;
						try {
							defaultProfileImage = Integer.parseInt(newProfileImage);
						} catch(Exception e) { }
						if(defaultProfileImage>=1 && defaultProfileImage<=10) { 
					%>
						<img class="my_workspace_AI_profile" src="display?filename=ProfileImg<%=defaultProfileImage%>.png"/>
					<% }else { %>
						<img class="my_workspace_AI_profile" src="display?filename=<%=newProfileImage%>" />
					<% } %>
						<div id="my_workspace_nickname_div">
							<%=dto.getNickname() %>
							<%if(loginId.equals(dto.getUserId())){ %>
							(나)
							<%} %>
							<span><svg data-i0m="true" data-qa="presence_indicator" aria-hidden="false" title="온라인" aria-label="온라인" data-qa-type="status-member-filled" data-qa-presence-self="true" data-qa-presence-active="true" data-qa-presence-dnd="false" viewBox="0 0 20 20" class="is-inline" style="--s: 20px;"><path fill="currentColor" d="M14.5 10a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0"></path></svg></span>
						</div>
					</div>
				<% } %>
				</div>
			</div>
		</div>
	</div>
	<!-- 팝업 활성화 시 검정 화면 -->
	<div id="black_filter" style="display:none;"></div>
	<div id="black_filter_deep" style="display:none;"></div>
	<div id="clear_filter" style="display:none;"></div>
	<div id="popup_invite" class="popup_window" style="display:none;">
		<div class="popup_invite_header">
			<div><span class="workspace_name">워크스페이스이름</span>(으)로 사용자 초대</div>
			<button class="x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="popup_invite_content">
			<div>초대 받을 사람:</div>
			<div><input id="popup_invite_text" type="text" placeholder="name@gmail.com"/></div>
		</div>
		<div class="popup_invite_footer">
			<div>AI 슬랙 회원만 초대가 가능합니다.</div>
			<button id="popup_invite_end" >보내기</button>
		</div>
	</div>
	<!-- 멤버 검색 -->
	<div id="popup_member_search_div" style="display:none"><!-- style="display:none" -->
		<div class="popup_member_search on">
			<img src="https://ca.slack-edge.com/T09J85U0SLV-U09KCU4JE0K-g594277b77a8-24"/>
			<svg data-i0m="true" data-qa="presence_indicator" aria-hidden="false" title="자리 비움" aria-label="자리 비움" data-qa-type="status-member" data-qa-presence-self="false" data-qa-presence-active="false" data-qa-presence-dnd="false" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M7 10a3 3 0 1 1 6 0 3 3 0 0 1-6 0m3-4.5a4.5 4.5 0 1 0 0 9 4.5 4.5 0 0 0 0-9" clip-rule="evenodd"></path>
			</svg>
			<Strong>재준</Strong>
			<h1>· 재준</h1>
			<div><svg data-i0m="true" data-qa="user-directory" aria-hidden="true" id="c-search_autocomplete__suggestion_toolbar_button_icon-0" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M4.75 1.5A1.75 1.75 0 0 0 3 3.25v.5a.75.75 0 0 0 1.5 0v-.5A.25.25 0 0 1 4.75 3h10c.69 0 1.25.56 1.25 1.25v11.5c0 .69-.56 1.25-1.25 1.25h-10a.25.25 0 0 1-.25-.25v-.5a.75.75 0 0 0-1.5 0v.5c0 .966.784 1.75 1.75 1.75h10a2.75 2.75 0 0 0 2.75-2.75V4.25a2.75 2.75 0 0 0-2.75-2.75zM2.25 6a.75.75 0 0 0 0 1.5h2a.75.75 0 0 0 0-1.5zm-.75 4a.75.75 0 0 1 .75-.75h2a.75.75 0 0 1 0 1.5h-2A.75.75 0 0 1 1.5 10m.75 2.5a.75.75 0 0 0 0 1.5h2a.75.75 0 0 0 0-1.5zm5.79.472.02.01q.037.016.09.018h4.7a.23.23 0 0 0 .11-.028 2.1 2.1 0 0 0-.736-.991c-.372-.271-.92-.481-1.724-.481-.805 0-1.353.21-1.724.481a2.1 2.1 0 0 0-.736.991m4.12-2.702q.117-.13.218-.268C12.784 9.437 13 8.712 13 8c0-1.624-1.287-2.5-2.5-2.5S8 6.376 8 8c0 .712.217 1.437.622 2.002q.1.139.219.268-.53.191-.949.5a3.6 3.6 0 0 0-1.285 1.755 1.42 1.42 0 0 0 .294 1.431 1.68 1.68 0 0 0 1.249.544h4.7a1.68 1.68 0 0 0 1.249-.544 1.42 1.42 0 0 0 .293-1.431 3.6 3.6 0 0 0-2.233-2.255M9.5 8c0-.65.463-1 1-1s1 .35 1 1c0 .426-.133.838-.34 1.127-.203.282-.434.398-.66.398s-.457-.116-.66-.398A2 2 0 0 1 9.5 8" clip-rule="evenodd"></path></svg>
			</div>
		</div>
		<div class="popup_member_search on"><!-- 온라인 상황일때 -->
			<img src="https://ca.slack-edge.com/T09J85U0SLV-U09LDJKJR7S-g9beb95f9c41-192/"/>
			<svg data-i0m="true" data-qa="presence_indicator" aria-hidden="false" title="자리 비움" aria-label="자리 비움" data-qa-type="status-member" data-qa-presence-self="false" data-qa-presence-active="false" data-qa-presence-dnd="false" viewBox="0 0 20 20" class="">
			<path fill="rgb(32, 161, 113)" d="M14.5 10a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0"></path>
			</svg>
			<Strong>민재</Strong>
			<h1>· 민재</h1>
			<div><svg data-i0m="true" data-qa="user-directory" aria-hidden="true" id="c-search_autocomplete__suggestion_toolbar_button_icon-0" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M4.75 1.5A1.75 1.75 0 0 0 3 3.25v.5a.75.75 0 0 0 1.5 0v-.5A.25.25 0 0 1 4.75 3h10c.69 0 1.25.56 1.25 1.25v11.5c0 .69-.56 1.25-1.25 1.25h-10a.25.25 0 0 1-.25-.25v-.5a.75.75 0 0 0-1.5 0v.5c0 .966.784 1.75 1.75 1.75h10a2.75 2.75 0 0 0 2.75-2.75V4.25a2.75 2.75 0 0 0-2.75-2.75zM2.25 6a.75.75 0 0 0 0 1.5h2a.75.75 0 0 0 0-1.5zm-.75 4a.75.75 0 0 1 .75-.75h2a.75.75 0 0 1 0 1.5h-2A.75.75 0 0 1 1.5 10m.75 2.5a.75.75 0 0 0 0 1.5h2a.75.75 0 0 0 0-1.5zm5.79.472.02.01q.037.016.09.018h4.7a.23.23 0 0 0 .11-.028 2.1 2.1 0 0 0-.736-.991c-.372-.271-.92-.481-1.724-.481-.805 0-1.353.21-1.724.481a2.1 2.1 0 0 0-.736.991m4.12-2.702q.117-.13.218-.268C12.784 9.437 13 8.712 13 8c0-1.624-1.287-2.5-2.5-2.5S8 6.376 8 8c0 .712.217 1.437.622 2.002q.1.139.219.268-.53.191-.949.5a3.6 3.6 0 0 0-1.285 1.755 1.42 1.42 0 0 0 .294 1.431 1.68 1.68 0 0 0 1.249.544h4.7a1.68 1.68 0 0 0 1.249-.544 1.42 1.42 0 0 0 .293-1.431 3.6 3.6 0 0 0-2.233-2.255M9.5 8c0-.65.463-1 1-1s1 .35 1 1c0 .426-.133.838-.34 1.127-.203.282-.434.398-.66.398s-.457-.116-.66-.398A2 2 0 0 1 9.5 8" clip-rule="evenodd"></path></svg>
			</div>
		</div>
	</div>	
		<!-- 내 프로필 -->
		<div id="popup_my_profile_div" class="popup_window" style="display:none"><!-- style="display:none" -->
			<div class="my_profile_header">
				<div>프로필</div>
				<button class="x_btn">
					<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
				</button>
			</div>
			<div class="my_profile_content">
				<div> <!-- 프로필 사진 및 사진 업로드 버튼 -->
					<img id="my_profile_img_3" src="resources/img/profile/ProfileImg1.png"/>
					<button id="popup_profile_img">사진 업로드</button>
				</div>
				<div><!-- 이름 및 편집 버튼 -->
					<div class="profile_user_name">________</div>
					<button id="my_profile_edit">편집</button>
				</div>
				<div><!-- 온라인 -->
					<svg data-i0m="true" data-qa="presence_indicator" aria-hidden="false" title="온라인" aria-label="온라인" data-qa-type="status-member-filled" data-qa-presence-self="true" data-qa-presence-active="true" data-qa-presence-dnd="false" viewBox="0 0 20 20" class="is-inline" style="--s: 20px;"><path fill="currentColor" d="M14.5 10a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0"></path></svg>
					<span class="on_off_line">_______</span>
					<span class="on_status"></span>
				</div>
				<!-- 상태 설정 시 여기에 생성 되어야 함 -->
				<div id="my_profile_status_area"></div>
				<div class="dtn_status_password"><!-- 상태설정 및 비빌번호 변경 버튼 -->
					<button id="popup_status">상태 설정</button>
					<button id="popup_password_set">비밀번호 변경</button>
				</div>
				<div class="profile_separator"></div>
				<div class="my_profile_footer">
					<!-- 이메일 정보 -->
					<span>이메일 정보</span>
					<img src="resources/img/ico_email_36x36.png"/>
					<span>이메일 주소</span>
					<span class="users_email">______</span>
				</div>
			</div>
		</div>	
		<!-- 멤버 프로필 -->
		<div id="member_profile2" class="popup_member_profile_div popup_window" style="display:none"><!-- style="display:none" -->
			<div class="member_profile_header">
				<div>프로필</div>
				<button class="x_btn">
					<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
				</button>
			</div>
			<div class="member_profile_content">
				<div> <!-- 프로필 사진  -->
					<img id="member_profile_img" src="resources/img/profile/ProfileImg2.png"/>
				</div>
				<div><!-- 이름 -->
					<div class="profile_user_name">________</div>
				</div>
				<div><!-- 온라인/자리비움 -->
					<svg class="offline_color" data-i0m="true" data-qa="presence_indicator" aria-hidden="false" title="자리 비움" aria-label="자리 비움" data-qa-type="status-member" data-qa-presence-self="false" data-qa-presence-active="false" data-qa-presence-dnd="false" viewBox="0 0 20 20" class="is-inline" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M7 10a3 3 0 1 1 6 0 3 3 0 0 1-6 0m3-4.5a4.5 4.5 0 1 0 0 9 4.5 4.5 0 0 0 0-9" clip-rule="evenodd"></path></svg>
					<span class="on_off_line">_______</span>
					<button>메세지</button>
				</div>
				<div id="users_profile_status_area">
					<span><span class="profile_user_name">???</span>님의 상태 : <span class="user_condition">_____</span></span>
				</div>
				<div class="profile_separator"></div>
				<div class="member_profile_footer">
					<!-- 이메일 정보 -->
					<span>이메일 정보</span>
					<img src="resources/img/ico_email_36x36.png"/>
					<span>이메일 주소</span>
					<span class="users_email">______</span>
				</div>
			</div>
		</div>
	<!-- 프로필 상태 설정 -->
	<div id="popup_profile_status_update_div" class="popup_window" style="display:none"> <!-- style="display:none" -->
		<div class="profile_status_update_header"><!-- header -->
			<div>상태 설정</div>
			<button class="x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="profile_status_update_content"><!-- content -->
			<div><span>유저닉네임</span>의 경우:</div>
			<div class="status_update_div">
				<div class="status_update">
					<img src="https://a.slack-edge.com/production-standard-emoji-assets/14.0/google-medium/1f5d3-fe0f.png"/>
					<div>미팅중</div>
				</div>
			</div>
			<div class="status_update_div">
				<div class="status_update">
					<img src="https://a.slack-edge.com/production-standard-emoji-assets/14.0/google-medium/1f68c.png"/>
					<div>출퇴근 중</div>
				</div>
			</div>
			<div class="status_update_div">
				<div class="status_update">
					<img src="https://a.slack-edge.com/production-standard-emoji-assets/14.0/google-medium/1f912.png"/>
					<div>병가</div>
				</div>
			</div>
			<div class="status_update_div">
				<div class="status_update">
					<img src="https://a.slack-edge.com/production-standard-emoji-assets/14.0/google-medium/1f334.png"/>
					<div>휴가 중</div>
				</div>
			</div>
			<div class="status_update_div">
				<div class="status_update">
					<img src="https://a.slack-edge.com/production-standard-emoji-assets/14.0/google-medium/1f3e1.png"/>
					<div>원격으로 작업 중</div>
				</div>
			</div>
			<div class="status_update_div">
				<div class="status_update">
					<img src="resources/img/gCal-logo.png"/>
					<div>미팅 중</div>
				</div>
			</div>
		</div>
		<div class="profile_status_update_footer"><!-- footer -->
			<button class="x_btn">취소</button>
		</div>
	</div>
	<!-- 내 프로필 설정 -->
	<div id="edit_my_profile_div" class="popup_window" style="display:none"> <!-- style="display:none" -->
		<div class="edit_my_profile_header">
			<div>내 프로필 편집</div>
			<button class="x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="edit_my_profile_content_div">
			<div class="fl">
				<div class="edit_my_profile_content">
					<span>성명</span>
					<div><div><input id="users_name_update_text" type="text" placeholder="성명"/></div></div>
					<span>별명</span>
					<div><div><input id="workspace_nickname_update_text" type="text" placeholder="별명"/></div></div>
					<span>직함</span>
					<div><div><input id="users_title_update_text" type="text" placeholder="직함"/></div></div>
				</div>
			</div>
			<div class="fr">
				<div class="edit_my_profile_content">
					<span>프로필 사진</span>
					<img id="edit_my_profile_img" src=https://ca.slack-edge.com/T09J85U0SLV-U09LDJKJR7S-g9beb95f9c41-192/>
					<div><button id="profile_file_upload_dtn2">사진 업로드</button>
					<input id="click_file_upload_1" class="click_file_upload" type="file" name="file_uplaod" style="display:none" />
					</div><!-- 클릭시 파일탐색기 열림 -->
					<div><button id="open_popup_AI_profile">AI 프로필 이미지 생성</button></div>
				</div>
			</div>
		</div>
		<div class="edit_my_profile_footer">
			<button class="x_btn">취소</button>
			<button id="edit_my_profile_end">변경사항 저장</button>
		</div>
	</div>
	<!-- 프로필 사진 추가 -->
	<div id="edit_my_profile_picture_div" class="popup_window" style="display:none"><!-- style="display:none" -->
		<div class="edit_my_profile_picture_header">
			<div>프로필 사진 추가</div>
			<button class="x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="edit_my_profile_picture_content">
			<div class="file_upload_div">
				<div>
					<img src="https://a.slack-edge.com/bv1-13-br/wp_add_profile_minicard@2x-3cf01eb.png"/>
					<h3>파일 업로드 버튼 누르기</h3>
				</div>
				
				<img id="my_profile_img" src="" style="display:none; max-width: 460px; max-height: 460px; object-fit: cover; position: absolute; top: 10%; left: 20%; overflow: hidden;" />
				
				<button id="profile_file_upload_dtn1">파일 업로드</button>
				<input id="click_file_upload_2" class="click_file_upload" type="file" name="file_uplaod" style="display:none" />
			</div>
			<div class="file_preview">
				<div>미리보기</div>
				<div id="my_profile_img_2">
					<img src="https://ca.slack-edge.com/T09J85U0SLV-U09LDJKJR7S-g9beb95f9c41-72"/>
					<div>
						<span id="my_nickname_file_preview">닉네임</span>
					</div>
				</div>
			</div>
		</div>
		<div class="edit_my_profile_picture_footer">
			<button class="x_btn">취소</button>
			<button id="edit_my_profile_picture_end">저장</button>
		</div>
	</div>
	<!-- AI 프로필 이미지 생성 -->
	<div id="AI_profile_img_div" class="popup_window" style="display:none"><!-- style="display:none" -->
		<div class="AI_profile_img_header">
			<div>AI 프로필 이미지 생성</div>
			<button class="x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="AI_profile_img_content">
			<span>키워드 1</span>
			<div class="input_AI_profile"><div><input id="keyword1" type="text" placeholder="초롱초롱한 눈으로"/></div></div>
			<div class="AI_img_tip">키워드의 내용은 자세할 수록 더 정확한 프로필 이미지를 생성 할 수 있습니다.</div>
			<span>키워드 2</span>
			<div class="input_AI_profile"><div><input id="keyword2" type="text" placeholder="바라보는"/></div></div>
			<div class="AI_img_tip">키워드의 예시로는 비 오는 날, 우산을 들고 있는, 고양이가 있습니다.</div>
			<span>키워드 3</span>
			<div class="input_AI_profile"><div><input id="keyword3" type="text" placeholder="고양이"/></div></div>
			<div class="AI_img_tip">특정 인물 이름이나 AI가 활용하기 어려운 키워드는 생성되지 않을 수 있습니다.</div>
		</div>
		<div class="AI_profile_img_footer">
			<button class="x_btn">취소</button>
			<button id="AI_profile_img_end">AI 프로필 이미지 생성하기</button>
		</div>
	</div>
	<!-- 비밀번호 변경 -->
	<div id="edit_password_div" class="popup_window" style="display:none"><!-- style="display:none" -->
		<div class="edit_password_header">
			<div>비밀번호 변경하기</div>
			<button class="x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="edit_password_content">
			<!-- style="display:none" -->
			<span>현재 비밀번호<span>*</span></span>
			<div class="input_edit_password"><div><input id="current_pw" type="password" placeholder="현재 비밀번호"/></div></div>
			<div class="input_edit_password_tip" id="current_pw_false" style="display:none">⚠비밀번호가 일치하지 않아요.</div>
			<span>새 비밀번호<span>*</span></span>
			<div class="input_edit_password"><div><input id="new_pw" type="password" placeholder="새 비밀번호"/></div></div>
			<div class="input_edit_password_tip" id="new_pw_rule" style="display:none">⚠6자 이상이여야 해요.</div>
			<span>새 비밀번호 확인<span>*</span></span>
			<div class="input_edit_password"><div><input id="new_pw_check" type="password" placeholder="새 비밀번호 확인"/></div></div>
			<div class="input_edit_password_tip" id="pw_check_false" style="display:none">⚠비밀번호가 일치하지 않아요.</div>
		</div>
		<div class="edit_password_foorter">
			<button class="x_btn">취소</button>
			<button id="edit_password_end">변경하기</button>
		</div>
	</div>
	<!-- 채널들 기본 채널 내용 -->
	<div id="new_channel" class="channel_content_div fl" style="display:none">
		<div class="channel_content_header">
			<div class="channel_content_header_text">
				<svg data-i0m="true" data-qa="channel-filled" aria-hidden="true" viewBox="0 0 20 20" class="is-inline"><path fill="currentColor" fill-rule="evenodd" d="M9.984 4.176a1 1 0 0 0-1.968-.352L7.448 7H4a1 1 0 0 0 0 2h3.091l-.803 4.5H3a1 1 0 1 0 0 2h2.93l-.414 2.324a1 1 0 0 0 1.968.352l.478-2.676h2.719l-.415 2.324a1 1 0 1 0 1.968.352l.478-2.676H16a1 1 0 1 0 0-2h-2.93l.803-4.5H17a1 1 0 1 0 0-2h-2.77l.504-2.824a1 1 0 1 0-1.968-.352L12.198 7H9.48zm1.054 9.324L11.84 9H9.123l-.804 4.5z" clip-rule="evenodd"></path></svg>
				<span class="channel_content_header_name">채널이름</span>
			</div>
			<div class="channel_content_header_button">
				<button class="channel_content_users_btn" ><img src="resources/img/ico_member.png"/><span id="channel_member_count">?</span></button>
				<button class="channel_content_information_btn" ><svg data-i0m="true" data-qa="ellipsis-vertical-filled" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M10 5.5A1.75 1.75 0 1 1 10 2a1.75 1.75 0 0 1 0 3.5m0 6.25a1.75 1.75 0 1 1 0-3.5 1.75 1.75 0 0 1 0 3.5m-1.75 4.5a1.75 1.75 0 1 0 3.5 0 1.75 1.75 0 0 0-3.5 0" clip-rule="evenodd"></path></svg></button>
			</div>
		</div>
		<div class="channel_message_div">
		<!-- 채널 첫 생성시 만들어질 부분 -->
			<div class="new_channel_div">
				<div id="new_channel_header_name_style">#<span class="channel_name">채널이름</span></div>
				<div id="new_channel_tip_font">고객님이 이 채널을 생성한 날짜는 <span id="channel_created_time">만든 시간</span>입니다. #<span class="channel_name">새로운 채널 이름</span>채널의 맨 첫 부분입니다.</div>
			</div>
			<div class="channel_content_message_time_line">
				<button class="time_line_btn">채팅 리스트</button>
			</div>
			<div class="channel_content_list_area">
				<!-- 채팅 내용 리스트 -->
			</div>
		</div>
		<div class="channel_content_footer">
			<!-- 채팅 에디터 자리 --> 
			<form id="editorForm" onsubmit="return false;">
			    <div id="editor" style="height: 80px; padding-right: 40px;"></div><!-- 에디터 css -->
			    <input type="hidden" name="content" id="content">
			    <button id="editor_dtn" type="button"><svg data-mx3="true" data-qa="send-filled" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" d="M1.5 2.106c0-.462.498-.754.901-.528l15.7 7.714a.73.73 0 0 1 .006 1.307L2.501 18.46l-.07.017a.754.754 0 0 1-.931-.733v-4.572c0-1.22.971-2.246 2.213-2.268l6.547-.17c.27-.01.75-.243.75-.797 0-.553-.5-.795-.75-.795l-6.547-.171C2.47 8.95 1.5 7.924 1.5 6.704z"></path></svg></button>
			</form>
		</div>
	</div>
	<!-- 채널 생성 팝업 -->
	<div id="channel_creation_div" class="popup_window" style="display:none"> <!-- style="display:none" -->
		<div class="channel_creation_header">
			<div class="header_set">채널 생성</div>
			<button class="header_button_set x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="channel_creation_content">
			<span class="content_main_span_set">이름</span>
			<div class="input_edit_blue_border"><div><input id="channel_create_name" type="text" placeholder="예:개발부서-방"/></div></div>
			<div class="content_tip">채널에서는 특정 주제에 대한 대화가 이루어집니다. 찾고 이해하기 쉬운 이름을 사용하세요.</div>
		</div>
		<div class="channel_creation_footer">
			<button id="channel_creation_next">생성</button>
		</div>
	</div>
	<!-- 채널생성 이후 다음팝업 채널내의 사용자 추가 -->
	<div class="channel_creation_member_invite_div" class="popup_window" style="display:none"> <!-- style="display:none" -->
		<div class="channel_creation_member_invite_header">
			<div class="channel_creation_member_invite_header_set"><svg data-i0m="true" data-qa="channel" aria-hidden="true" viewBox="0 0 20 20" class="is-inline"><path fill="currentColor" fill-rule="evenodd" d="M9.74 3.878a.75.75 0 1 0-1.48-.255L7.68 7H3.75a.75.75 0 0 0 0 1.5h3.67L6.472 14H2.75a.75.75 0 0 0 0 1.5h3.463l-.452 2.623a.75.75 0 0 0 1.478.255l.496-2.878h3.228l-.452 2.623a.75.75 0 0 0 1.478.255l.496-2.878h3.765a.75.75 0 0 0 0-1.5h-3.506l.948-5.5h3.558a.75.75 0 0 0 0-1.5h-3.3l.54-3.122a.75.75 0 0 0-1.48-.255L12.43 7H9.2zM11.221 14l.948-5.5H8.942L7.994 14z" clip-rule="evenodd"></path></svg>
			<span><span class="channel_name_header channel_name">채널이름수정</span>에 사용자 추가</span></div>
			<span class="content_tip"><span class="workspace_name">워크스페이스이름수정</span>의 멤버가 아닌 AI슬랙 사용자(이메일)도 추가할 수 있습니다.</span>
			<button class="header_button_set x_btn member_invite_x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="channel_creation_member_invite_content">
			<!-- 라벨 라디오 -->
			<label class="channel_member_all_invite">
				<input id="not_invite_radio" class="channel_members_invite_radio" type="radio" name="add_members"/>
				<span class="workspace_name" id="labalName">채널이름수정</span>
				<span class="channel_creation_member_invite_content_span">의 <span class="users_int">n</span>명 멤버 모두 추가</span>
			</label>
			<br/>
			<label class="channel_member_name_invite">
				<input id="invite_radio" class="channel_members_invite_radio" type="radio" name="add_members"/>
				<span class="channel_creation_member_invite_content_span">특정 사용자 추가</span>
			</label> <!-- style="display:none" -->
			<div class="channel_member_name_invite_div" style="display:none"><!-- 특정 사용자 클릭 시 -->
				<div class="input_edit_blue_border"><div><input id="input_channel_member_name" type="text" placeholder="사용자 이름 또는 이메일 입력"/></div></div>
			</div>
		</div>
		<div class="channel_creation_member_invite_footer">
			<button id="channel_creation_end" class="button_hover_set_green">완료됨</button>
		</div>
	</div>
	<!-- 채널목록 우클릭 시  -->
	<div class="channel_right_click_menu_div" style="display:none"> <!-- style="display:none" -->
		<div class="channel_right_click_menu_content channel_content_information_btn"><span>채널 세부정보 보기</span></div>
		<div class="channel_right_click_menu_content channel_AI_chat_btn"><span class="color_red">AI 채널 요약</span></div>
		<div class="channel_right_click_menu_content channel_favorite_btn"><span>즐겨찾기 추가/제거</span></div>
		<div class="channel_right_click_menu_content channel_getout_btn"><span class="color_red">채널에서 나가기</span></div>
	</div>
	<!-- 채널 세부정보 보기 --> 
	<!-- style="display:none" -->
	<div class="show_channel_information_div popup_window" style="display:none">
		<div class="show_channel_information_header">
			<span class="channel_shop">
				<svg data-i0m="true" data-qa="channel-filled" aria-hidden="true" viewBox="0 0 20 20" class="is-inline"><path fill="currentColor" fill-rule="evenodd" d="M9.984 4.176a1 1 0 0 0-1.968-.352L7.448 7H4a1 1 0 0 0 0 2h3.091l-.803 4.5H3a1 1 0 1 0 0 2h2.93l-.414 2.324a1 1 0 0 0 1.968.352l.478-2.676h2.719l-.415 2.324a1 1 0 1 0 1.968.352l.478-2.676H16a1 1 0 1 0 0-2h-2.93l.803-4.5H17a1 1 0 1 0 0-2h-2.77l.504-2.824a1 1 0 1 0-1.968-.352L12.198 7H9.48zm1.054 9.324L11.84 9H9.123l-.804 4.5z" clip-rule="evenodd"></path></svg>
			</span>
			<span class="channel_information_name this_channel_name">채널 이름 수정
				<button class="channel_favorite_star"> <!-- before after 로 노란색 채우고 안 채우고로 수정 -->
					<svg data-i0m="true" data-qa="star" aria-hidden="true" viewBox="0 0 20 20" class="" style="--s: 16px;"><path fill="currentColor" fill-rule="evenodd" d="M9.044 4.29c-.393.923-.676 2.105-.812 3.065a.75.75 0 0 1-.825.64l-.25-.027c-1.066-.12-2.106-.236-2.942-.202-.45.018-.773.079-.98.167-.188.08-.216.15-.227.187-.013.042-.027.148.112.37.143.229.4.497.77.788.734.579 1.755 1.128 2.66 1.54a.75.75 0 0 1 .35 1.036c-.466.87-1.022 2.125-1.32 3.239-.15.56-.223 1.04-.208 1.396.015.372.113.454.124.461l.003.001a.2.2 0 0 0 .042.006.9.9 0 0 0 .297-.06c.297-.1.678-.319 1.116-.64.87-.635 1.8-1.55 2.493-2.275a.75.75 0 0 1 1.085 0c.692.724 1.626 1.639 2.5 2.275.44.32.822.539 1.12.64a.9.9 0 0 0 .3.06q.038-.003.044-.006h.002c.011-.009.109-.09.123-.46.013-.357-.06-.836-.212-1.397-.303-1.114-.864-2.368-1.33-3.24a.75.75 0 0 1 .35-1.037c.903-.41 1.92-.96 2.652-1.54.369-.292.625-.56.768-.787.139-.223.124-.329.112-.37-.012-.038-.039-.107-.226-.186-.206-.088-.527-.149-.976-.167-.835-.034-1.874.082-2.941.201l-.246.027a.75.75 0 0 1-.825-.64c-.136-.96-.42-2.142-.813-3.064-.198-.464-.405-.82-.605-1.048-.204-.232-.319-.243-.34-.243s-.135.01-.34.243c-.2.228-.407.584-.605 1.048m-.522-2.036c.343-.39.833-.754 1.467-.754s1.125.363 1.467.754c.348.396.63.914.858 1.449.359.84.627 1.83.798 2.723.913-.1 1.884-.192 2.708-.158.521.021 1.052.094 1.503.285.47.2.902.556 1.076 1.14.177.597-.004 1.153-.279 1.592-.271.434-.676.826-1.108 1.168-.662.524-1.482 1.003-2.256 1.392.41.85.836 1.884 1.1 2.856.17.625.286 1.271.264 1.846-.021.56-.182 1.218-.749 1.623-.555.398-1.205.316-1.7.148-.51-.173-1.034-.493-1.523-.849-.754-.55-1.523-1.261-2.158-1.896-.634.634-1.4 1.346-2.15 1.895-.487.356-1.01.677-1.518.85-.495.168-1.144.25-1.699-.148-.565-.405-.727-1.062-.75-1.62-.024-.574.09-1.22.257-1.846.261-.972.684-2.007 1.093-2.858-.775-.389-1.597-.867-2.262-1.39-.433-.342-.84-.734-1.111-1.168-.276-.44-.457-.997-.28-1.595.174-.585.608-.941 1.079-1.141.45-.191.983-.264 1.505-.285.826-.033 1.799.059 2.713.159.17-.893.439-1.882.797-2.723.228-.535.51-1.053.858-1.449" clip-rule="evenodd"></path></svg>
				</button>
			</span>
			<button class="header_button_set x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="show_channel_information_menu">
			<button class="channel_menu_button channel_information_data">정보</button>
			<button class="channel_menu_button channel_information_member">멤버</button>
		</div>
		<!-- 정보 on 일 때    style="display:none" -->
		<div class="show_channel_information_content"> 
			<div class="manager_channel_name_set">
				<div>채널 이름
					<button id="channel_name_set_btn" class="blue_edit">편집</button>
				</div>
				<div class="channel_name_edit">
					<span class="channel_name_shop">
						<svg data-i0m="true" data-qa="channel-filled" aria-hidden="true" viewBox="0 0 20 20" class="is-inline"><path fill="currentColor" fill-rule="evenodd" d="M9.984 4.176a1 1 0 0 0-1.968-.352L7.448 7H4a1 1 0 0 0 0 2h3.091l-.803 4.5H3a1 1 0 1 0 0 2h2.93l-.414 2.324a1 1 0 0 0 1.968.352l.478-2.676h2.719l-.415 2.324a1 1 0 1 0 1.968.352l.478-2.676H16a1 1 0 1 0 0-2h-2.93l.803-4.5H17a1 1 0 1 0 0-2h-2.77l.504-2.824a1 1 0 1 0-1.968-.352L12.198 7H9.48zm1.054 9.324L11.84 9H9.123l-.804 4.5z" clip-rule="evenodd"></path></svg>
						<span class="this_channel_name">채널-이름</span>
					</span>
				</div>
			</div>
			<div class="main_content">
				<div class="main_content_div">
					<div>주제
						<button id="channel_topic_set_btn" class="blue_edit">편집</button>
					</div>
					<div><span class="channel_name_shop channel_topic">주제 추가</span></div>
				</div>
				<div class="main_content_div">
					<div>설명
						<button id="channel_explanation_set_btn" class="blue_edit">편집</button>
					</div>
					<div><span class="channel_name_shop channel_explanation">설명 추가</span></div>
				</div>
				<div class="main_content_div">
					<div>매니저
					</div>
					<div><span class="channel_manager_name channel_name_shop">매니저 이름</span></div>
				</div>
				<div class="channel_leave">채널에서 나가기</div>
			</div>
		</div>
		<!-- 멤버 on 일 떄   style="display:none"-->
		<div class="show_channel_member_content" style="display:none">
			<div class="channel_search_member_div">
				<div class="channel_search_member">
					<!-- 돋보기 -->
					<svg data-i0m="true" data-qa="search" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M9 3a6 6 0 1 0 0 12A6 6 0 0 0 9 3M1.5 9a7.5 7.5 0 1 1 13.307 4.746l3.473 3.474a.75.75 0 1 1-1.06 1.06l-3.473-3.473A7.5 7.5 0 0 1 1.5 9" clip-rule="evenodd"></path></svg>
					<div class="channel_search_member_input_text">
						<input id="channel_member_search_text" type="text" placeholder="멤버 찾기"/>
					</div>
				</div>
			</div>
			<!-- 검색 창에 일치하지 항목을 못 찾았을 때  style="display:none"-->
			<div class="channel_not_search" style="display:none">
				<div><span class="channel_member_uesrs_nickname">YG</span>에 대해 일치하는 항목을 찾지 못했습니다.
				</div>
				<button class="channel_users_invite_btn">사용자 추가</button>
			</div>
			<!--style="display:none"-->
			<!-- 사용자 추가 div -->
			<div class="channel_member_invite_button" style="display:block">
				<div class="channel_users_invite_btn"><img src="resources/img/ico_user_invite.png"/>사용자 추가</div>
			</div>
			<!-- 채널 멤버 조회 -->
			<div id="channel_member_uesrs_div">
				<!-- 채널 멤버 리스트 -->
			</div>
			<!-- 멤버 검색 안할 때   style="display:none"-->
			<div class="channel_member_search_result" style="display:none">
				<div class="channel_member_search_users">
					<span>이 채널에서</span>
					<div>
						<img src="resources/img/ico_bin_user.png"/>
						<span>일치하는 항목 없음</span>
					</div>
				</div>
				<div class="channel_out_users_div">
					<span>이 채널에 없음</span>
					<div class="channel_out_users">
						<img src="https://ca.slack-edge.com/T09J85U0SLV-U09KCU4JE0K-g594277b77a8-48"/>
						<div>
							<span class="channel_member_uesrs_nickname">멤버이름
							</span>
						</div>
						<!-- 온라인>초록 오프라인>회색 -->
						<svg class="offline_color"data-i0m="true" data-qa="presence_indicator" aria-hidden="false" title="자리 비움" aria-label="자리 비움" data-qa-type="status-member" data-qa-presence-self="false" data-qa-presence-active="false" data-qa-presence-dnd="false" viewBox="0 0 20 20" class="" style="--s: 20px;"><path fill="currentColor" fill-rule="evenodd" d="M7 10a3 3 0 1 1 6 0 3 3 0 0 1-6 0m3-4.5a4.5 4.5 0 1 0 0 9 4.5 4.5 0 0 0 0-9" clip-rule="evenodd"></path></svg>
						<span>멤버이름</span>
						<button>채널에 추가</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- AI 채팅 요약 -->
	<div id="AI_chat_div" class="popup_window" style="display:none">
		<div class="AI_chat_header">
			<div class="header_set">AI 채팅 내역 조회</div>
			<button class="header_button_set x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div>요약/#<span class="AI_chat_channel_name channel_name">채널이름수정</span></div>
		<div>
			<!-- 요약 내용 리스트 -->
		</div>
		<div class="AI_chat_content">
			<pre class="font_style">
				//
			</pre>
		</div>
	</div>
	<!-- 채널 이름 변경 --><!--style="display:none"-->
	<div id="this_channel_name_set" class="this_channel_seting_edit_div popup_window" style="display:none">
		<div class="this_channel_seting_edit_header">
			<div class="header_set">이 채널 이름 변경</div>
			<button class="header_button_set x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="this_channel_seting_edit_content">
			<span class="content_main_span_set">채널 이름</span>
			<div class="this_channel_seting_edit_input_text">
				<div class="input_edit_blue_border"><div><input id="channel_name_update_text" type="text" placeholder="# 채널명"/></div></div>
			</div>
			<div class="content_tip">이름은 공백이나 마침표가 없는 소문자여야 하며, 80자를 초과할 수 없습니다.</div>
		</div>
		<div class="this_channel_seting_edit_foorter">
			<button id="this_channel_name_set_end">변경사항 저장</button>
			<button class="x_btn">취소</button>
		</div>
	</div>
	<!-- 채널 주제 편집 --><!--style="display:none"-->
	<div id="this_channel_topic_set" class="this_channel_seting_edit_div popup_window" style="display:none">
		<div class="this_channel_seting_edit_header">
			<div class="header_set">주제 편집</div>
			<button class="header_button_set x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="this_channel_seting_edit_content">
			<span class="content_main_span_set">채널 이름</span>
			<div class="this_channel_seting_edit_input_text">
				<div class="input_edit_blue_border"><div><input id="channel_topic_text" type="text" placeholder="주제 추가"/></div></div>
			</div>
			<div class="content_tip">지금 바로 #<span class="channel_name">채널이름수정</span>이(가) 중점으로 하는 항목이 무엇인지 사람들로 하여금 알게 하세요(예: 프로젝트 마일스톤). 주제는 항상 머리글에 표시됩니다.</div>
		</div>
		<div class="this_channel_seting_edit_foorter">
			<button id="this_channel_topic_set_end">변경사항 저장</button>
			<button class="x_btn">취소</button>
		</div>
	</div>
	<!-- 채널 설명 편집 --><!--style="display:none"-->
	<div id="this_channel_explanation_set" class="this_channel_seting_edit_div popup_window" style="display:none">
		<div class="this_channel_seting_edit_header">
			<div class="header_set">설명 편집</div>
			<button class="header_button_set x_btn">
				<svg data-i0m="true" data-qa="close" aria-hidden="true" viewBox="0 0 20 20" class=""><path fill="currentColor" fill-rule="evenodd" d="M16.53 3.47a.75.75 0 0 1 0 1.06L11.06 10l5.47 5.47a.75.75 0 0 1-1.06 1.06L10 11.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L8.94 10 3.47 4.53a.75.75 0 0 1 1.06-1.06L10 8.94l5.47-5.47a.75.75 0 0 1 1.06 0" clip-rule="evenodd"></path></svg>
			</button>
		</div>
		<div class="this_channel_seting_edit_content">
			<span class="content_main_span_set">설명 추가</span>
			<div class="this_channel_seting_edit_input_text">
				<div class="input_edit_blue_border"><div><input id="channel_explanation_text" type="text" placeholder="설명 추가"/></div></div>
			</div>
			<div class="content_tip">사람들에게 이 채널의 목적에 대해 설명해 주세요.</div>
		</div>
		<div class="this_channel_seting_edit_foorter">
			<button id="this_channel_explanation_set_end">변경사항 저장</button>
			<button class="x_btn">취소</button>
		</div>
	</div>
	<!-- 채널 생성 팝업창 검색 팝업 -->
	<div class="channel_create_search_popup" style="display:none"> 
		<div class="users_search_div"><img src="resources/img/profile/ProfileImg1.png"/><span class="channel_invite_users">참여자</span></div>
	</div>
	<!-- 채널에서 나가기 -->
	<div class="channel_getout_div" style="display:none">
		<span class="channel_getout_text"><h1 class="channel_getout_name">채널이름</h1>에서 나가시겠습니까?</span>
	</div>
	<!-- 채널에서 제거 -->
	<div class="remove_member" style="display:none">채널에서 제거하기</div>
	<div style="clear:both;"></div>
	<!-- AI이미지 생성중 로딩 div -->
	<div id="loading_layer" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); z-index:9999; flex-direction:column; justify-content:center; align-items:center; color:white;">
    <div class="spinner" style="width:50px; height:50px; border:5px solid #f3f3f3; border-top:5px solid #3498db; border-radius:50%; animation:spin 1s linear infinite;"></div>
    <p style="margin-top:20px; font-size:18px;">AI가 프로필 이미지를 그리고 있습니다... (약 15초 소요)</p>
	</div>
	<!-- 로딩창 style -->
	<style>
	@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
	</style>
</body>
</html>