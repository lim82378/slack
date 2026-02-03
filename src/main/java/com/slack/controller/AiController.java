package com.slack.controller;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.slack.dto.Channel5Dto;
import com.slack.service.ChannelService;
import com.slack.service.WorkspaceService;

@RestController
@RequestMapping("/ai")
public class AiController {
	//채널 서비스
	@Autowired
	ChannelService cSvc;
	//워크스페이스 서비스
	@Autowired
	WorkspaceService wSvc;
	
	//Chat_GPT_API 키
	private final String OPENAI_API_KEY = "_______________";
	
	/** [MJ] AI 프로필 이미지 생성
	 *  파라미터 : word1, word2, word3, userId, workspaceIdx
	 *  리턴 : filename, url
	 */
	@RequestMapping("/AIprofileImgCreate")
    public Map<String, Object> AIprofileImgCreate(@RequestBody Map<String, String> reqMap) {
        Map<String, Object> retMap = new HashMap<>();
        
        try {
            // 1. JS에서 보낸 값 꺼내기
            String word1 = reqMap.get("Word1");
            String word2 = reqMap.get("Word2");
            String word3 = reqMap.get("Word3");
            String userId = reqMap.get("UserId");
            int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));

            // 단어/문장 정제
            String combinedInput = (word1 + " " + word2 + " " + word3).trim().replaceAll("\\s+", " ");

            // 최종 프롬프트 (귀여운 3D 픽사 스타일)
            String prompt = "A super cute and adorable 3D render in Pixar movie style, " 
                            + "profile picture of: " + combinedInput 
                            + ". Vibrant colors, soft lighting, cheerful expression, clean lines, playful atmosphere, kawaii aesthetics.";

            // 2. OpenAI API 호출
            RestTemplate rest = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + OPENAI_API_KEY);
            headers.add("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("model", "dall-e-3");
            body.put("prompt", prompt);
            body.put("n", 1);
            body.put("size", "1024x1024");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            // OpenAI 서버에 요청
            Map<String, Object> response = rest.postForObject("https://api.openai.com/v1/images/generations", entity, Map.class);
            
            // 3. 응답에서 AI가 생성한 임시 URL 가져오기
            List<Map<String, String>> data = (List<Map<String, String>>) response.get("data");
            String aiUrl = data.get(0).get("url");

            // 4. 이미지 다운로드 및 서버 로컬 폴더에 저장 (C:/Temp/upload/)
            String fileName = "ai_profile_" + System.currentTimeMillis() + ".png";
            
            // try-with-resources를 사용하여 스트림을 자동으로 닫아줍니다 (안정성)
            try (InputStream in = new URL(aiUrl).openStream()) {
                Files.copy(in, Paths.get("/opt/upload/" + fileName));
            }

            // 5. ★ DB 업데이트
            wSvc.profileUpload(fileName, userId, workspaceIdx);

            // 6. 결과 리턴 (JSP 화면 처리를 위해 파일명과 URL 전달)
            retMap.put("status", "success");
            retMap.put("fileName", fileName);
            retMap.put("url", "/upload/" + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            retMap.put("status", "error");
            retMap.put("message", e.getMessage());
        }
        return retMap;
    }
	
	/** [SB] AI 채팅 내역 요약 기능
	 *  파라미터 : channelIdx, workspaceIdx
	 *  리턴 : content
	 */
	@RequestMapping(value = "/summarize", produces = "text/plain; charset=UTF-8")
    public String summarizeChannel(@RequestBody Map<String, String> reqMap) {
            int channelIdx = Integer.parseInt(reqMap.get("ChannelIdx")); 
            int workspaceIdx = Integer.parseInt(reqMap.get("WorkspaceIdx"));
            List<Channel5Dto> channelList = cSvc.channelMsgSelectAI(channelIdx, workspaceIdx);
            
            if(channelList == null || channelList.isEmpty()) {
                    return "최근 7일간 나눈 대화내용이 없어 요약할 수 없습니다.";
            }
            String fullChat = channelList.stream()
                        .map(dto -> dto.getNickname() + ": " + dto.getContent())
                        .collect(Collectors.joining("\n"));
            // 4. API 호출 준비 (RestTemplate 사용)
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.openai.com/v1/chat/completions";
        // 헤더 설정: 인증키와 전송 데이터 타입(JSON) 명시
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);
        // GPT에게 시킬 명령(Prompt)과 재료(fullChat)를 섞어서 메시지 생성
        String prompt = "너는 유능한 팀 프로젝트 매니저야. 아래 대화 내용을 분석해서 3줄 요약해줘.\n"
                  + "1. 핵심 결정 사항\n"
                  + "2. 현재 진행 중인 업무\n"
                  + "3. 향후 일정 또는 공지\n"
                  + "말투는 '~함'체로 통일하고, 가독성 좋게 번호를 붙여서 요약해줘.\n\n" 
                  + "대화 내용:\n" + fullChat;
        // GPT API 규격에 맞는 Body 구성 (Map 사용)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini"); // 사용 모델
        
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> msg = new HashMap<>();
        msg.put("role", "user");
        msg.put("content", prompt);
        messages.add(msg);
        
        requestBody.put("messages", messages);
        // 5. 실제 전송 및 응답 받기
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            // ResponseEntity로 응답 전체를 받아옵니다.
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            // 응답 데이터(JSON)에서 답변 텍스트만 쏙 골라내기
            // 구조: choices -> [0] -> message -> content
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> messageResult = (Map<String, Object>) choices.get(0).get("message");
            
            // 최종 요약본 리턴!
            return (String) messageResult.get("content");
            
        } catch (Exception e) {
            e.printStackTrace();
            return "AI 요약 중 오류가 발생했습니다: " + e.getMessage();
        }                
    }
}
