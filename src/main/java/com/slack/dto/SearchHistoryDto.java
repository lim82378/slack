package com.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryDto {
	private String searchHistoryIdx;
	private String searchKeyword; // 검색내역
	private String searchTime; // 검색일시
}