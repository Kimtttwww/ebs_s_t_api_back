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
import com.eb_study.common.InputChecker;

@Mapper(
		componentModel = ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface FreeBoardMapper {
	FreeBoardSearchOption toEntity(InFreeBoardSearchOption ifbso);

	BoardInsert toEntity(InBoardInsert b);

	BoardUpdate toEntity(BoardUpdate b);

	ReplyInsert toEntity(ReplyInsert b);

	@Mapping(target = "fileName", expression = "java(a != null ? String.format(\"%s.%s\", a.getFileOrigin(), a.getExt()) : \"\")")
	OutAttach toOutDTO(AttachMetadata a);

	List<OutAttach> toOutDTO(List<AttachMetadata> a);


	@AfterMapping
	default void fbsoAfter(@MappingTarget FreeBoardSearchOption fbso) {
		fbso.setOffset((fbso.getCurrentPage() - 1) * fbso.getPerPage());
	}

	@AfterMapping
	default void boardContentAfter(@MappingTarget BoardContent b) {
		b.setTitle(InputChecker.htmlEntityFilter(b.getTitle()));
		b.setContent(InputChecker.htmlEntityFilter(b.getContent()));
	}

	@AfterMapping
	default void boardInsertAfter(@MappingTarget BoardInsert b) {
		b.setPassword(new BCryptPasswordEncoder().encode(b.getPassword()));
		b.setWriter(InputChecker.htmlEntityFilter(b.getWriter()));
	}

	@AfterMapping
	default void replyContentAfter(@MappingTarget ReplyContent r) {
		r.setContent(InputChecker.htmlEntityFilter(r.getContent()));
	}
}
