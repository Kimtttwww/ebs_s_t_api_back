package com.eb_study.board.free.model.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "게시글 수정용 객체")
@Getter
@Setter
@ToString(callSuper = true)
public class BoardUpdate extends BoardContent {
	@Schema(description = "게시글 번호", minimum = "1")
	@Positive
	private int boardNo;

	@Schema(description = "비밀번호", minLength = 4, maxLength = 15)
	@NotBlank @Length(min = 4, max = 15) @Pattern(regexp = "^.[a-zA-Z0-9!@#$%^&*]{4,15}$")
	private String password;

//	TODO 파일 구현 필요
	@Schema(hidden = true)
	private boolean attach;
}
