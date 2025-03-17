package com.eb_study.board.free.model.dto;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReplySelect {
	@Schema(description = "댓글 내용", nullable = false)
	private String content;
	@Schema(description = "댓글 작성일", nullable = false)
	private Date created;
}
