package com.eb_study.board.free.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "게시글 목록 구성용 객체")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ArgsBoardList {
	@Schema(description = "검색된 전체 게시글 수", nullable = false, minimum = "0")
	private int allCount;
	@Schema(description = "게시글 목록, 비어있을 수 있음", nullable = false)
	private List<BoardSelect> board;
}
