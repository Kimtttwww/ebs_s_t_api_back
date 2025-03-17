package com.eb_study.board.free.model.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.eb_study.board.free.model.dto.BoardInsert;
import com.eb_study.board.free.model.dto.FreeBoardSearchOption;
import com.eb_study.board.free.model.dto.InBoardInsert;
import com.eb_study.board.free.model.dto.InFreeBoardSearchOption;
import com.eb_study.board.free.model.dto.OutFreeBoardSearchOption;

@Mapper(
		componentModel = ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface FreeBoardMapper {
	FreeBoardSearchOption toEntity(InFreeBoardSearchOption ifbso);

	BoardInsert toEntity(InBoardInsert b);
	
	@Deprecated
	OutFreeBoardSearchOption toOutDTO(FreeBoardSearchOption fbso);

	@AfterMapping
	default void toEntityAfter(@MappingTarget FreeBoardSearchOption fbso) {
		fbso.setOffset((fbso.getCurrentPage() - 1) * fbso.getPerPage());
	}
}
