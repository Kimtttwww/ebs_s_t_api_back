package com.eb_study.board.free.model.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BoardContent {
	@Schema(description = "게시글 제목", minLength = 4, maxLength = 99, nullable = false)
	@NotEmpty @Length(min = 4, max = 99)
	private String title;

	@Schema(description = "게시글 내용", minLength = 4, maxLength = 1999, nullable = false)
	@NotEmpty @Length(min = 4, max = 1999)
	private String content;
}
