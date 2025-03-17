package com.eb_study.board.free.model.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(description = "게시글 등록용 객체")
public record InBoardInsert (
	@Schema(description = "비밀번호", minLength = 4, maxLength = 15)
	@NotBlank @Length(min = 4, max = 15) @Pattern(regexp = "^.[a-zA-Z0-9!@#$%^&*]{4,15}$")
	String password,

	@Schema(description = "게시글 유형 번호", requiredMode = RequiredMode.REQUIRED)
	@Positive
	int categoryNo,

	@Schema(description = "게시글 제목", minLength = 4, maxLength = 99, nullable = false)
	@NotBlank @Length(min = 4, max = 99)
	String title,

	@Schema(description = "게시글 내용", minLength = 4, maxLength = 1999, nullable = false)
	@NotBlank @Length(min = 4, max = 1999)
	String content,

	@Schema(description = "작성자 이름", minLength = 3, maxLength = 5)
	@NotBlank @Length(min = 3, max = 5)
	String writer
) {}
