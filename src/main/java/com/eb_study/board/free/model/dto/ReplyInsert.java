package com.eb_study.board.free.model.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReplyInsert {
	@Schema(description = "댓글이 달릴 게시글 번호")
	@Positive
	private int boardNo;

	@Schema(description = "댓글 내용")
	@NotBlank @Length(min = 1)
	private String content;
}
