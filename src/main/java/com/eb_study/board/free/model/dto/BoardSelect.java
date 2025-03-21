package com.eb_study.board.free.model.dto;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "게시글 목록 조회용 객체")
@Getter
@Setter
@ToString(callSuper = true)
public class BoardSelect extends BoardContent {
	@Schema(description = "게시글 번호", nullable = false)
	private int boardNo;

	@Schema(description = "게시글 유형 번호", nullable = false)
	private int categoryNo;

	@Schema(description = "게시글 유형명", nullable = true)
	private String categoryName;

	@Schema(description = "게시글 작성자 이름", nullable = false)
	private String writer;

	@Schema(description = "게시글 작성일", nullable = false)
	private Date created;

	@Schema(description = "게시글 수정일", nullable = true)
	private Date updated;

	@Schema(description = "게시글 조회수", defaultValue = "0", nullable = false)
	private int views;

	@Schema(description = "첨부파일 여부", defaultValue = "false", nullable = false)
	private boolean attach;
}
