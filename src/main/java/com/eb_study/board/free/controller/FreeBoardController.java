package com.eb_study.board.free.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eb_study.board.free.model.dto.ArgsBoardList;
import com.eb_study.board.free.model.dto.BoardDelete;
import com.eb_study.board.free.model.dto.BoardDetailSelect;
import com.eb_study.board.free.model.dto.BoardSelect;
import com.eb_study.board.free.model.dto.BoardUpdate;
import com.eb_study.board.free.model.dto.Category;
import com.eb_study.board.free.model.dto.FreeBoardSearchOption;
import com.eb_study.board.free.model.dto.InBoardInsert;
import com.eb_study.board.free.model.dto.InFreeBoardSearchOption;
import com.eb_study.board.free.model.dto.ReplyInsert;
import com.eb_study.board.free.model.mapper.FreeBoardMapper;
import com.eb_study.board.free.model.service.FreeBoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "자유게시판 관련 API")
@RestController
public class FreeBoardController {
	@Autowired
	private FreeBoardService service;
	@Autowired
	private FreeBoardMapper mapper;

	/**
	 * 게시글 목록 조회 페이지
	 * @param gfbso 검색 조건 및 페이징
	 * @return 게시글 목록과 검색 조건
	 */
	@Operation(summary = "게시글 목록 조회", description = "게시글 목록 조회 페이지")
	@ApiResponse(responseCode = "200", description = "ok")
	@ApiResponse(responseCode = "500", description = "server error")
	@GetMapping("boards/free/list")
	public ResponseEntity<ArgsBoardList> getBoardList(
		@Parameter(required = false, description = "검색 조건, (Json)", allowEmptyValue = true)
			@ModelAttribute InFreeBoardSearchOption ifbso) {
		log.info(ifbso != null ? ifbso.toString() : "ifbso is null");
		FreeBoardSearchOption option = mapper.toEntity(ifbso);
//		TODO 손보기
		option.setAllCount(service.getAllBoardCount(option));

		log.info(option.toString());
		return ResponseEntity.ok(new ArgsBoardList(option.getAllCount(), service.getBoardList(option)));
	}

	/**
	 * 게시글 유형 목록 조회 페이지
	 * @return 게시글 유형 목록
	 */
	@Operation(summary = "카테고리 목록 조회", description = "게시글 유형 목록 조회 페이지")
	@ApiResponse(responseCode = "200", description = "ok")
	@ApiResponse(responseCode = "500", description = "server error")
	@GetMapping("boards/free/category")
	public ResponseEntity<List<Category>> getCategoryList() {
		return ResponseEntity.ok(service.getCategoryList());
	}

	/**
	 * 게시글 조회
	 * @param boardNo 게시글 번호
	 * @param doIncreaseViews 조회수 증가 여부
	 * @return 게시글
	 * @throws SQLException 게시글 조회수 증가 실패
	 * @throws IllegalArgumentException 잘못된 요청
	 */
	@Operation(summary = "게시글 조회", description = "단일 게시글 조회")
	@ApiResponse(responseCode = "200", description = "ok")
	@ApiResponse(responseCode = "500", description = "server error")
	@GetMapping("boards/free/view/{boardNo}")
//	TODO 파일 기능 구현 필요
	public ResponseEntity<BoardDetailSelect> getBoard(
		@Parameter(description = "게시글번호")
			@PathVariable("boardNo") int boardNo,
		@Parameter(description = "조회수 증가 여부, (URL Parameter)", examples = {@ExampleObject(value = "true", description = "조회수 증가"), @ExampleObject(value = "false", description = "조회수 불변")})
			@RequestParam(name = "add") boolean doIncreaseViews) throws Exception {
		return ResponseEntity.ok(service.getBoard(boardNo, doIncreaseViews));
	}


	/**
	 * 게시글 등록
	 * @param b 등록할 게시글
	 * @return 등록된 게시글의 게시글 번호
	 * @throws Exception 게시글 등록 실패
	 * TODO 파일 구현 필요
	 */
	@Operation(summary = "게시글 등록", description = "새 게시글 등록")
	@ApiResponse(responseCode = "200", description = "ok, 등록된 게시글 번호 반환")
	@ApiResponse(responseCode = "400", description = "유효하지 않은 입력")
	@ApiResponse(responseCode = "500", description = "server error")
	@PostMapping("board/free/write")
	public ResponseEntity<Integer> insertBoard(
		@Parameter(description = "작성할 게시글, 유효성 검사 있음 (json)")
			@ModelAttribute @Valid InBoardInsert b,
		@Parameter(description = "작성할 게시글의 첨부파일")
			@RequestParam(name = "attachs", required = false) List<MultipartFile> files) throws Exception {
//		TODO ? recode로 받아서 다른 dto로 service 주면 무슨 소용?
		return ResponseEntity.ok(service.insertBoard(mapper.toEntity(b), files));
	}

	/**
	 * 게시글 수정
	 * @param b 수정할 게시글
	 * @return 수정된 게시글의 게시글 번호
	 * @throws IllegalArgumentException 비밀번호 불일치
	 * @throws Exception 게시글 수정 실패
	 * TODO 파일 구현 필요
	 */
	@Operation(summary = "게시글 수정", description = "게시글 내용 수정")
	@ApiResponse(responseCode = "200", description = "ok")
	@ApiResponse(responseCode = "400", description = "유효하지 않은 입력(비밀번호 불일치 등)")
	@ApiResponse(responseCode = "500", description = "server error")
	@PostMapping("board/free/modify")
	public ResponseEntity<Integer> updateBoard(
		@Parameter(description = "수정할 게시글 내용, 유효성 검사 있음 (json)")
			@RequestBody @Valid BoardUpdate b) throws Exception {
		return ResponseEntity.ok(service.updateBoard(b));
	}

	/**
	 * 댓글 등록
	 * @param reply 등록할 댓글
	 * @throws SQLException 댓글 등록 실패
	 */
	@Operation(summary = "댓글 등록", description = "게시글의 댓글 등록")
	@ApiResponse(responseCode = "204", description = "ok")
	@ApiResponse(responseCode = "400", description = "유효하지 않은 입력")
	@ApiResponse(responseCode = "500", description = "server error")
	@PostMapping("board/reply")
	public ResponseEntity<?> insertReply(
		@Parameter(description = "등록할 댓글 내용, 유효성 검사 있음 (json)")
			@RequestBody @Valid ReplyInsert r) throws Exception {
		service.insertReply(r);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 게시글 + 연?관된 댓글 삭제
	 * @param boardNo 게시글 번호
	 * @param password 비밀번호
	 * @return 게시글 + 연?관된 댓글 삭제 여부
	 * @throws IllegalArgumentException 비밀번호 불일치
	 * @throws SQLException 게시글 | 연관 댓글 삭제 실패
	 * TODO 파일 구현 필요
	 * TODO 파일 구현 후 검증 필요
	 */
	@Operation(summary = "게시글 삭제", description = "게시글 및 연관된 댓글 삭제")
	@ApiResponse(responseCode = "204", description = "ok")
	@ApiResponse(responseCode = "400", description = "유효하지 않은 입력")
	@ApiResponse(responseCode = "500", description = "server error")
	@PostMapping("board/free/remove")
	public ResponseEntity<?> deleteBoard(
		@Parameter(description = "삭제할 게시글 내용, 유효성 검사 있음 (json)")
			@RequestBody @Valid BoardDelete b) throws Exception {
		log.info(b.toString());
		service.deleteBoard(b);
		return ResponseEntity.noContent().build();
	}
}
