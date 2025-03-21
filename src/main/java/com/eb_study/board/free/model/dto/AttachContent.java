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
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AttachContent extends AttachNum {
	@Schema(description = "업로드 당시 파일명")
	private String fileOrigin;

	@Schema(description = "파일 확장자", examples = {"jpg", "png", "gif"})
	private String ext;
}
