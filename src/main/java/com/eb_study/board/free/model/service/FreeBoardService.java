package com.eb_study.board.free.model.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.eb_study.board.free.model.dao.FreeBoardDao;
import com.eb_study.board.free.model.dto.AttachNum;
import com.eb_study.board.free.model.dto.AttachSelect;
import com.eb_study.board.free.model.dto.BoardDelete;
import com.eb_study.board.free.model.dto.BoardDetailSelect;
import com.eb_study.board.free.model.dto.BoardInsert;
import com.eb_study.board.free.model.dto.BoardSelect;
import com.eb_study.board.free.model.dto.BoardUpdate;
import com.eb_study.board.free.model.dto.Category;
import com.eb_study.board.free.model.dto.FreeBoardSearchOption;
import com.eb_study.board.free.model.dto.ReplyInsert;
import com.eb_study.common.FileProcessor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Service
public class FreeBoardService {
	@Autowired
	private FreeBoardDao dao;


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
	 * @throws SQLException 게시글 조회수 증가 실패
	 */
	public BoardDetailSelect getBoard(int boardNo, boolean doIncreaseViews) throws SQLException {
		if (doIncreaseViews) dao.increaseViews(boardNo);
		return dao.getBoard(boardNo);
	}

	/**
	 * 게시글 등록
	 * @param b 등록할 게시글
	 * @return 등록된 게시글의 게시글 번호
	 * @throws SQLException 게시글 등록 실패
	 * @throws IOException 첨부파일 저장 경로 접근 불가 | ?
	 * TODO 파일 업로드 검증 필요
	 */
	@Transactional(rollbackFor = Exception.class)
	public int insertBoard(BoardInsert b, List<MultipartFile> files) throws SQLException, IllegalStateException, IOException {
		boolean exist = files != null && !files.isEmpty();

		b.setAttach(exist);
		dao.insertBoard(b);

		FileProcessor processor = new FileProcessor();
		if (exist && processor.uploadFileFilter(files)) {
			List<AttachSelect> a = processor.multipartFileToAttachs(b.getBoardNo(), files);
			try {
				dao.insertAttachs(a);
			} catch (SQLException e) {	// 등록 실패시 업로드된 파일 제거
				processor.fileRemove(a);
				throw e;
			}
		}

		return b.getBoardNo();
	}

	/**
	 * 게시글 수정
	 * @param b 수정할 게시글
	 * @return 수정된 게시글의 게시글 번호
	 * @throws IllegalArgumentException 비밀번호 불일치
	 * @throws SQLException 게시글 수정 실패
	 */
	public int updateBoard(BoardUpdate b) throws IllegalArgumentException, SQLException {
		if (!matchBoardPassword(b.getBoardNo(), b.getPassword()))
			throw new IllegalArgumentException("비밀번호 불일치");
		dao.updateBoard(b);
		return b.getBoardNo();
	}

	/**
	 * 댓글 등록
	 * @param r 등록할 댓글
	 * @throws SQLException 댓글 등록 실패
	 */
	public void insertReply(ReplyInsert r) throws SQLException {
		dao.insertReply(r);
	}

	/**
	 * 게시글 및 연관 댓글 삭제
	 * @param b 삭제할 게시글
	 * @throws IllegalArgumentException 비밀번호 불일치
	 * @throws SQLException 게시글 삭제 실패
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteBoard(BoardDelete b) throws Exception {
		if (!matchBoardPassword(b.getBoardNo(), b.getPassword()))
			throw new IllegalArgumentException("비밀번호 불일치");
		dao.deleteReply(b.getBoardNo());
//		TODO 파일 삭제?
		dao.deleteBoard(b.getBoardNo());
	}

	/**
	 * 게시글 번호와 첨부파일 번호로 파일 메타데이터 조회
	 * @param boardNo 게시글 번호
	 * @param attachNo 첨부파일 번호
	 * @return 파일 메타데이터
	 */
	public AttachSelect getAttach(int boardNo, int attachNo) {
		AttachNum a = new AttachNum(boardNo, attachNo);
		return dao.getAttachs(a);
	}

	/**
	 * 파일 메타데이터로 실제 첨부파일 조회
	 * @param a 파일 메타데이터
	 * @return 실제 첨부파일
	 * @throws IOException
	 */
	public FileSystemResource getAttachResource(@NotNull AttachSelect a) throws IOException {
		return new FileProcessor().getAttachFromSystem(a);
	}


	/**
	 * 게시글 변경 전 비밀번호 일치 검사
	 * @param boardNo 게시글 번호
	 * @param password 사용자가 입력한 비밀번호
	 * @return 비밀번호 일치 여부
	 */	// TODO 검증은 누구의 역할인가?
	public boolean matchBoardPassword(int boardNo, @NotEmpty String password) {
		return new BCryptPasswordEncoder().matches(password, dao.getBoardPassword(boardNo));
	}
}
