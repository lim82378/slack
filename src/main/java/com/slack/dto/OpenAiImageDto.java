package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiImageDto {
	private String model = "dall-e-3"; // 사용할 모델 명시
    private String prompt;
    private int n = 1;
    private String size = "1024x1024";
}
