package com.eb_study.board.free.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "게시글 목록 조회 검색 조건 객체")
@Getter
@Setter
@ToString(callSuper = true)
public class FreeBoardSearchOption extends InFreeBoardSearchOption {
	@Schema(description = "검색된 전체 게시글 수", nullable = false, minimum = "0")
	private int allCount;
	@Schema(hidden = true)
	private int offset;
}
