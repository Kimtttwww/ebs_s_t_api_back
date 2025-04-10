package com.eb_study.board.free.model.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.eb_study.board.free.model.dto.AttachMetadata;
import com.eb_study.board.free.model.dto.BoardContent;
import com.eb_study.board.free.model.dto.BoardInsert;
import com.eb_study.board.free.model.dto.BoardUpdate;
import com.eb_study.board.free.model.dto.FreeBoardSearchOption;
import com.eb_study.board.free.model.dto.InBoardInsert;
import com.eb_study.board.free.model.dto.InFreeBoardSearchOption;
import com.eb_study.board.free.model.dto.OutAttach;
import com.eb_study.board.free.model.dto.ReplyContent;
import com.eb_study.board.free.model.dto.ReplyInsert;
import com.eb_study.common.util.InputChecker;

@Mapper(
		componentModel = ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface FreeBoardMapper {
	FreeBoardSearchOption toEntity(InFreeBoardSearchOption ifbso);

	BoardInsert toEntity(InBoardInsert board);

	BoardUpdate toEntity(BoardUpdate board);

	ReplyInsert toEntity(ReplyInsert reply);

	@Mapping(target = "fileName", expression = "java(attach != null ? String.format(\"%s.%s\", attach.getFileOrigin(), attach.getExt()) : \"\")")
	OutAttach toOutDTO(AttachMetadata attach);

	List<OutAttach> toOutDTO(List<AttachMetadata> attachs);


	@AfterMapping
	default void fbsoAfter(@MappingTarget FreeBoardSearchOption fbso) {
		if (fbso != null) {
			fbso.setOffset((fbso.getCurrentPage() - 1) * fbso.getPerPage());
		}
	}

	@AfterMapping
	default void boardContentAfter(@MappingTarget BoardContent board) {
		if (board != null) {
			board.setTitle(InputChecker.htmlEntityFilter(board.getTitle()));
			board.setContent(InputChecker.htmlEntityFilter(board.getContent()));
		}
	}

	@AfterMapping
	default void boardInsertAfter(@MappingTarget BoardInsert board) {
		if (board != null) {
			board.setPassword(new BCryptPasswordEncoder().encode(board.getPassword()));
			board.setWriter(InputChecker.htmlEntityFilter(board.getWriter()));
		}
	}

	@AfterMapping
	default void replyContentAfter(@MappingTarget ReplyContent reply) {
		if (reply != null) {
			reply.setContent(InputChecker.htmlEntityFilter(reply.getContent()));
		}
	}
}
