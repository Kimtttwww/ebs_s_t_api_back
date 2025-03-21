package com.eb_study.board.free.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "게시글의 댓글")
@Getter
@Setter
@ToString(callSuper = true)
public class ReplyInsert extends ReplyContent {
	@Schema(description = "댓글이 달릴 게시글 번호")
	@Positive
	private int boardNo;
}
