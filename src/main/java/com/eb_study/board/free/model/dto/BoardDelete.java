package com.eb_study.board.free.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "게시글 삭제용 객체")
@Getter
@Setter
@ToString
public class BoardDelete {
	@Schema(description = "게시글 번호", minimum = "1")
	@Positive
	private int boardNo;

	@Schema(description = "비밀번호", minLength = 4, maxLength = 15)
	@NotBlank //@Length(min = 4, max = 15) @Pattern(regexp = "^.[a-zA-Z0-9!@#$%^&*]{4,15}$")
	private String password;
}
