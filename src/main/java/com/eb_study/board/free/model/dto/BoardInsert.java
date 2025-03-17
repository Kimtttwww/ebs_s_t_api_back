package com.eb_study.board.free.model.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "게시글 등록용 객체")
@Getter
@Setter
@ToString(callSuper = true)
public class BoardInsert extends BoardContent {
	@Schema(hidden = true)
	@Positive
	private int boardNo;
	
	@Schema(description = "비밀번호", minLength = 4, maxLength = 15)
	@NotEmpty @Length(min = 4, max = 15) @Pattern(regexp = "^.[a-zA-Z0-9!@#$%^&*]{4,15}$")
	private String password;

	@Schema(description = "게시글 유형 번호", requiredMode = RequiredMode.REQUIRED)
	@Positive
	private int categoryNo;

	@Schema(description = "작성자 이름", minLength = 3, maxLength = 5)
	@NotEmpty @Length(min = 3, max = 5)
	private String writer;

	@Schema(hidden = true, description = "첨부파일 여부")
//	TODO 파일 구현 필요
	private boolean attach;
}
