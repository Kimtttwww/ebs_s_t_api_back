package com.eb_study.board.free.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AttachNum {
	@Schema(description = "게시글 번호")
	private int boardNo;

	@Schema(description = "첨부파일 번호")
	private int attachNo;


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AttachNum)) return false;
		AttachNum a = (AttachNum) obj;
		return this.boardNo == a.getBoardNo() && this.attachNo == a.getAttachNo();
	}
}
