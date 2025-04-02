package com.eb_study.board.free.model.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.FileSystemResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.eb_study.board.free.model.dao.FreeBoardDao;
import com.eb_study.board.free.model.dto.AttachMetadata;
import com.eb_study.board.free.model.dto.AttachNum;
import com.eb_study.board.free.model.dto.BoardDelete;
import com.eb_study.board.free.model.dto.BoardInsert;
import com.eb_study.board.free.model.dto.BoardSelect;
import com.eb_study.board.free.model.dto.BoardUpdate;
import com.eb_study.board.free.model.dto.Category;
import com.eb_study.board.free.model.dto.FreeBoardSearchOption;
import com.eb_study.board.free.model.dto.ReplyInsert;
import com.eb_study.board.free.model.dto.ReplySelect;
import com.eb_study.file.FileManager;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FreeBoardService {
	private final FreeBoardDao dao;

	private final FileManager fileManager;


	/**
	 * 게시글 목록 조회
	 * @param option 검색 조건 및 페이징
	 * @return 게시글 목록
	 */
	public List<BoardSelect> getBoardList(FreeBoardSearchOption option) {
		return dao.getBoardList(option);
	}

	/**
	 * 검색 조건에 해당하는 전체 게시글 갯수
	 * @param option 검색 조건 및 페이징
	 * @return 전체 게시글 수
	 */
	public int getAllBoardCount(FreeBoardSearchOption option) {
		return dao.getAllBoardCount(option);
	}

	/**
	 * 카테고리 목록 조회
	 * @return 카테고리 목록
	 */
	public List<Category> getCategoryList() {
		return dao.getCategoryList();
	}

	/**
	 * 게시글 조회 with 조회수 증감
	 * @param boardNo 게시글 번호
	 * @param doIncreaseViews 조회수 증가 사용 여부(게시글 조회용)
	 * @return 게시글
	 */
	public Optional<BoardSelect> getBoard(int boardNo, boolean doIncreaseViews) {
		if (doIncreaseViews) dao.increaseViews(boardNo);
		return dao.getBoard(boardNo);
	}

	/**
	 * 첨부파일 목록 조회
	 * @param boardNo 게시글 번호
	 * @return 첨부파일 목록
	 */
	public List<AttachMetadata> getAttachList(int boardNo) {
		return dao.getAttachList(boardNo);
	}

	/**
	 * 댓글 목록 조회
	 * @param boardNo 게시글 번호
	 * @return 댓글 목록
	 */
	public List<ReplySelect> getReplyList(int boardNo) {
		return dao.getReplyList(boardNo);
	}


	/**
	 * 게시글 등록
	 * @param board 등록할 게시글
	 * @return 등록된 게시글의 게시글 번호
	 * @throws IOException 첨부파일 저장 경로 접근 불가 | ?
	 */
	@Transactional(rollbackFor = Exception.class)
	public void insertBoard(BoardInsert board, List<MultipartFile> files) throws IOException {
		boolean exist = files != null && !files.isEmpty();

		board.setAttach(exist);
		dao.insertBoard(board);

		if (exist) insertAttachList(board.getBoardNo(), files);
	}

	/**
	 * 게시글 수정
	 * @param b 수정할 게시글
	 * @param files 새로 등록할 첨부파일들
	 * @return 수정된 게시글의 게시글 번호
	 * @throws IllegalArgumentException 비밀번호 불일치 | ?
	 * @throws IOException 저장위치 사용 불가 | ?
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateBoard(BoardUpdate board, List<MultipartFile> files) throws IllegalArgumentException, IOException {
		matchBoardPassword(board.getBoardNo(), board.getPassword());

		dao.updateBoard(board);
		updateAttach(board.getBoardNo(), board.getAttach(), files);
	}

	/**
	 * 댓글 등록
	 * @param reply 등록할 댓글
	 */
	public void insertReply(ReplyInsert reply) {
		dao.insertReply(reply);
	}

	/**
	 * 게시글 및 댓글 및 첨부파일 삭제
	 * @param board 삭제할 게시글
	 * @throws IllegalArgumentException 비밀번호 불일치
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteBoard(BoardDelete board) throws IllegalArgumentException {
		matchBoardPassword(board.getBoardNo(), board.getPassword());
		List<AttachMetadata> attachs = dao.getAttachList(board.getBoardNo());

		dao.deleteReplyList(board.getBoardNo());
		dao.deleteAllAttach(board.getBoardNo());
		dao.deleteBoard(board.getBoardNo());

		fileManager.fileRemove(attachs);
	}

	/**
	 * 게시글 번호와 첨부파일 번호로 파일 메타데이터 조회
	 * @param boardNo 게시글 번호
	 * @param attachNo 첨부파일 번호
	 * @return 파일 메타데이터
	 */
	public Optional<AttachMetadata> getAttach(AttachNum attach) {
		return dao.getAttach(attach);
	}

	/**
	 * 파일 메타데이터로 실제 첨부파일 조회
	 * @param a 파일 메타데이터
	 * @return 실제 첨부파일
	 * @throws IOException 저장 위치 사용 불가
	 */
	public FileSystemResource getAttachResource(AttachMetadata attach) throws IOException {
		return fileManager.getAttachFromSystem(attach);
	}

	/**
	 * 첨부파일 DB 등록, 서버 저장
	 * @param boardNo 게시글 번호
	 * @param files 업로드 할 파일들(1개 이상 필수)
	 * @throws IOException 저장위치 사용불가 | 유효하지 않은 파일 | ?
	 */
	public void insertAttachList(int boardNo, List<MultipartFile> files) throws IOException {
		fileManager.validateFileList(files);
		List<AttachMetadata> attach = fileManager.makeMetadataFromFile(boardNo, files);

		dao.insertAttachList(attach.stream().filter(a -> a != null).toList());
		fileManager.fileSave(files, attach);
	}


	/**
	 * 게시글 변경 전 비밀번호 일치 검사
	 * @param boardNo 게시글 번호
	 * @param password 사용자가 입력한 비밀번호
	 * @return 비밀번호 일치 여부
	 * @throws IllegalArgumentException 비밀번호 불일치
	 */
	public void matchBoardPassword(int boardNo, @NotEmpty String password) throws IllegalArgumentException {
		if (!new BCryptPasswordEncoder().matches(password, dao.getBoardPassword(boardNo)))
			throw new IllegalArgumentException("비밀번호 불일치");
	}

	/**
	 * @param boardNo 게시글 번호
	 * @param attachs 삭제할 첨부파일 목록
	 * @param files 새로 등록할 첨부파일 목록
	 * @throws IOException ?
	 * @throws NullPointerException 인자에 null이 주어짐
	 * @throws IllegalStateException 파일 이미 저장됨
	 */
	private void updateAttach(int boardNo, List<AttachNum> attachs, List<MultipartFile> files) throws IllegalStateException, NullPointerException, IOException {
		boolean attachsHasVal = attachs != null && !attachs.isEmpty(),
				filesHasVal = files != null && !files.isEmpty();
		List<AttachMetadata> metadata = dao.getAttachList(boardNo);

		if (!attachsHasVal && !filesHasVal) return;

		if (attachsHasVal) dao.deleteAttachList(attachs.stream().filter(attach -> attach != null).toList());

		if (filesHasVal) insertAttachList(boardNo, files);

		if (attachsHasVal) fileManager.fileRemove(metadata);
	}
}
