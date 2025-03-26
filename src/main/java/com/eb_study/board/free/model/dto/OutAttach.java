package com.eb_study.board.free.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "반환용 첨부파일 객체")
public record OutAttach (
		@Schema(description = "게시글 번호")
		int boardNo,

		@Schema(description = "첨부파일 번호")
		int attachNo,

		@Schema(description = "업로드 당시 파일명")
		String fileName
) {}
