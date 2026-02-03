package com.slack.websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.dto.DmListDto;
import com.slack.dto.Users3Dto;
import com.slack.service.DirectoryService;
import com.slack.service.DmService;
import com.slack.util.ApplicationContextProvider;

public class BroadSocket extends TextWebSocketHandler{
public static Set<WebSocketSession> clients = Collections.synchronizedSet(new HashSet<>());
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Map<String, Object> map = session.getAttributes();
		String userId = (String) map.get("userId");
		clients.add(session);
		System.out.println("새로운 클라이언트 IN : 현재 " + clients.size() + "명.");
		
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println("클라이언트로부터 도착한 메시지 : " + message.getPayload());
		ObjectMapper objMapper = new ObjectMapper(); // JSON에 담긴 데이터를 문자열로 읽을 수 있게 해줌 
		String payload = message.getPayload();
		Map<String, Object> data = objMapper.readValue(payload, Map.class);
		String type = (String)data.get("type");
		
		
			
		if("dm".equals(type)) {	
			DmService dSvc = ApplicationContextProvider.getBean(DmService.class); // 수동으로 서비스 호출할 수 있게 해줌 
			DirectoryService dirSvc = ApplicationContextProvider.getBean(DirectoryService.class); 
			DmListDto dto = objMapper.readValue(payload, DmListDto.class);
			Users3Dto dto2 = dirSvc.getWorkspaceMyImg(dto.getWorkspaceIdx(), dto.getDmFrom());
			
			String fromNick = dSvc.getNicknameForDm(dto.getDmFrom(), dto.getWorkspaceIdx());
			dto.setFromProfileImage(dto2.getProfileImage());//수정함 dto.setProfileImage (DmListDto)
			dto.setFromNickname(fromNick);
			dto.setSentTime("방금 전");
			
			dSvc.addDm(dto);
			
			
			String processedJson = objMapper.writeValueAsString(dto);
			for(WebSocketSession client : clients) {
				client.sendMessage(new TextMessage(processedJson));
			}
		} else {
			for(WebSocketSession client : clients) {
				if(client.isOpen()) {
					client.sendMessage(new TextMessage(payload));
				}
			}
		}
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		clients.remove(session);
		System.out.println("클라이언트 OUT : 현재 " + clients.size() + "명.");
	}
}
