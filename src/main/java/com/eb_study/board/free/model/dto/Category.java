package com.eb_study.board.free.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "게시글 유형")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Category {
	@Schema(description = "게시글 유형 번호", nullable = false)
	private int categoryNo;
	@Schema(description = "게시글 유형명", nullable = false)
	private String name;
}
