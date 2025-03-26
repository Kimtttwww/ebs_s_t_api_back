package com.eb_study.board.free.model.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.eb_study.board.free.model.dto.AttachMetadata;
import com.eb_study.board.free.model.dto.AttachNum;
import com.eb_study.board.free.model.dto.BoardInsert;
import com.eb_study.board.free.model.dto.BoardSelect;
import com.eb_study.board.free.model.dto.BoardUpdate;
import com.eb_study.board.free.model.dto.Category;
import com.eb_study.board.free.model.dto.FreeBoardSearchOption;
import com.eb_study.board.free.model.dto.ReplyInsert;
import com.eb_study.board.free.model.dto.ReplySelect;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FreeBoardDao {
	private final SqlSession conn;

	private final String mapper = "freeBoardMapper.";


	/**
	 * 게시글 목록 조회
	 * @param option 검색 조건
	 * @return 게시글 목록
	 */
	public List<BoardSelect> getBoardList(FreeBoardSearchOption option) {
		return conn.selectList(mapper + "getBoardList", option);
	}

	/**
	 * 카테고리 목록 조회
	 * @return 카테고리 목록
	 */
	public List<Category> getCategoryList() {
		return conn.selectList(mapper + "getCategoryList");
	}

	/**
	 * 게시글 조회
	 * @param boardNo 게시글 번호
	 * @return 게시글
	 */
	public Optional<BoardSelect> getBoard(int boardNo) {
		return Optional.ofNullable(conn.selectOne(mapper + "getBoard", boardNo));
	}

	/**
	 * 모든/검색되는 게시글 갯수 조회
	 * @param option 검색조건
	 * @return 모든/검색되는 게시글 갯수
	 */
	public Optional<Integer> getAllBoardCount(FreeBoardSearchOption option) {
		return Optional.ofNullable(conn.selectOne(mapper + "getAllBoardCount", option));
	}

	/**
	 * 게시글 비밀번호 조회
	 * @param boardNo 조회할 게시글 번호
	 * @return 게시글 비밀번호
	 */
	public Optional<String> getBoardPassword(int boardNo) {
		return Optional.ofNullable(conn.selectOne(mapper + "getBoardPassword", boardNo));
	}

	/**
	 * 첨부파일 조회
	 * @param a boardNo와 attachNo가 담긴 AttachNum
	 * @return DB에서 가져온 파일 메타데이터 
	 */
	public Optional<AttachMetadata> getAttach(AttachNum a) {
		return Optional.ofNullable(conn.selectOne(mapper + "getAttachs", a));
	}

	/**
	 * 첨부파일 목록 조회
	 * @param boardNo 게시글 번호
	 * @return DB에서 가져온 파일 메타데이터들
	 */
	public List<AttachMetadata> getAttachList(int boardNo) {
		return conn.selectList(mapper + "getAttachs", new AttachNum(boardNo, 0));
	}

	/**
	 * 댓글 목록 조회
	 * @param boardNo 게시글 번호
	 * @return 댓글 목록
	 */
	public List<ReplySelect> getReplyList(int boardNo) {
		return conn.selectList(mapper + "getReplyList", boardNo);
	}


	/**
	 * 조회되는 게시글의 조회수 증가
	 * @param boardNo 증가될 게시글 번호
	 * @throws SQLException 조회수 증가 실패
	 */
	public void increaseViews(int boardNo) throws SQLException {
		boolean result = conn.update(mapper + "increaseViews", boardNo) > 0;
		if (!result) throw new SQLException("조회수 증가 실패");
	}

	/**
	 * 게시글 등록
	 * @param b 등록할 게시글
	 * @throws SQLException 게시글 등록 실패
	 */
	public void insertBoard(BoardInsert b) throws SQLException {
		boolean result = conn.insert(mapper + "insertBoard", b) > 0;
		if (!result) throw new SQLException("게시글 등록 실패");
	}

	/**
	 * 게시글 수정
	 * @param b 수정할 게시글
	 * @throws SQLException 게시글 수정 실패
	 */
	public void updateBoard(BoardUpdate b) throws SQLException {
		boolean result = conn.update(mapper + "updateBoard", b) > 0;
		if (!result) throw new SQLException("게시글 수정 실패");
	}

	/**
	 * 댓글 등록
	 * @param r 등록할 댓글
	 * @throws SQLException 댓글 등록 실패
	 */
	public void insertReply(ReplyInsert r) throws SQLException {
		boolean result = conn.insert(mapper + "insertReply", r) > 0;
		if (!result) throw new SQLException("댓글 등록 실패");
	}

	/**
	 * 게시글의 모든 댓글 삭제
	 * @param boardNo 삭제할 댓글의 게시글 번호
	 */
	public void deleteAllReply(int boardNo) {
		conn.delete(mapper + "deleteReply", boardNo);
	}

	/**
	 * 게시글 삭제
	 * @param b 삭제할 게시글
	 * @throws SQLException 게시글 삭제 실패
	 */
	public void deleteBoard(int boardNo) throws SQLException {
		boolean result = conn.delete(mapper + "deleteBoard", boardNo) > 0;
		if (!result) throw new SQLException("게시글 삭제 실패");
	}

	/**
	 * 등록할 게시글의 첨부파일들 등록
	 * @param a 첨부파일들
	 * @throws SQLException 주어진 첨부파일 전부/일부 업로드 실패
	 */
	public void insertAttachList(List<AttachMetadata> a) throws SQLException {
		boolean result = conn.insert(mapper + "insertAttachs", a) == a.size();
		if (!result) throw new SQLException("주어진 첨부파일 전부/일부 업로드 실패");
	}

	/**
	 * 모든 첨부파일 삭제
	 * @param boardNo 삭제할 첨부파일의 게시글 번호
	 */
	public void deleteAllAttach(int boardNo) {
		conn.delete(mapper + "deleteAttachs", boardNo);
	}

	/**
	 * @param a 삭제할 첨부파일 목록
	 * @throws SQLException 주어진 첨부파일들 전부/일부 삭제 실패
	 */
	public void deleteAttachList(List<AttachNum> a) throws SQLException {
		boolean result = conn.delete(mapper + "deleteAttachList", a) == a.size();
		if (!result) throw new SQLException("주어진 첨부파일들 전부/일부 삭제 실패");
	}
}
