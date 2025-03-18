package com.eb_study.board.free.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class AttachSelect {
	private int boardNo;
	private int attachNo;
	private String fileOrigin;
	private String fileRename;
	private String ext;
}
