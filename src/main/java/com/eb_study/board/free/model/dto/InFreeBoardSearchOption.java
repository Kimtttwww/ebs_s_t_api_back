package com.eb_study.board.free.model.dto;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "게시글 목록 조회 검색 조건 요청 객체")
@Getter
@Setter
@ToString
public class InFreeBoardSearchOption {
	@Schema(description = "게시글 작성일 시작범위", requiredMode = RequiredMode.NOT_REQUIRED, nullable = true)
	Date startDate;
	@Schema(description = "게시글 작성일 끝범위", requiredMode = RequiredMode.NOT_REQUIRED, nullable = true)
	Date endDate;
	@Schema(description = "게시글 유형", requiredMode = RequiredMode.NOT_REQUIRED, nullable = true)
	int category;
	@Schema(description = "검색어", requiredMode = RequiredMode.NOT_REQUIRED, nullable = true)
	String query;
	@Schema(description = "페이지 당 게시글 수", requiredMode = RequiredMode.NOT_REQUIRED, defaultValue = "10")
	int perPage;
	@Schema(description = "현재 페이지", requiredMode = RequiredMode.NOT_REQUIRED, defaultValue = "1")
	int currentPage;


	public InFreeBoardSearchOption() {
		super();
		this.perPage = 10;
		this.currentPage = 1;
	}
}
