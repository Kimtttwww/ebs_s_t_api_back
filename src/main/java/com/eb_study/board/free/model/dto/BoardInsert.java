package com.eb_study.board.free.model.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class BoardInsert extends BoardContent {
	@Positive
	private int boardNo;

	@NotBlank @Length(min = 4, max = 15) @Pattern(regexp = "^.[a-zA-Z0-9!@#$%^&*]{4,15}$")
	private String password;

	@Positive
	private int categoryNo;

	@NotBlank @Length(min = 3, max = 5)
	private String writer;

	private boolean attach;
}
