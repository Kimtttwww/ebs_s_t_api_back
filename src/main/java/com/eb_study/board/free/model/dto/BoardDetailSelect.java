package com.eb_study.board.free.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "게시글 조회용 객체")
@Getter
@Setter
@ToString(callSuper = true)
public class BoardDetailSelect extends BoardSelect {
//	TODO 파일 구현 필요
	@Schema(description = "첨부파일", nullable = true)
	private List<Attach> attachs;
}
