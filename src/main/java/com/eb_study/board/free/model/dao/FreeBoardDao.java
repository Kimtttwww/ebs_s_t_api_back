package com.eb_study.board.free.model.dao;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.eb_study.board.free.model.dto.AttachMetadata;
import com.eb_study.board.free.model.dto.AttachNum;
import com.eb_study.board.free.model.dto.BoardInsert;
import com.eb_study.board.free.model.dto.BoardSelect;
import com.eb_study.board.free.model.dto.BoardUpdate;
import com.eb_study.board.free.model.dto.Category;
import com.eb_study.board.free.model.dto.FreeBoardSearchOption;
import com.eb_study.board.free.model.dto.ReplyInsert;
import com.eb_study.board.free.model.dto.ReplySelect;

@Mapper
public interface FreeBoardDao {
	/**
	 * 게시글 목록 조회
	 * @param option 검색 조건
	 * @return 게시글 목록
	 */
	public List<BoardSelect> getBoardList(FreeBoardSearchOption option);

	/**
	 * 카테고리 목록 조회
	 * @return 카테고리 목록
	 */
	@Select("SELECT * FROM CATEGORY")
	public List<Category> getCategoryList();

	/**
	 * 게시글 조회
	 * @param boardNo 게시글 번호
	 * @return 게시글
	 */
	public Optional<BoardSelect> getBoard(int boardNo);

	/**
	 * 모든/검색되는 게시글 갯수 조회
	 * @param option 검색조건
	 * @return 모든/검색되는 게시글 갯수
	 */
	public int getAllBoardCount(FreeBoardSearchOption option);

	/**
	 * 게시글 비밀번호 조회
	 * @param boardNo 조회할 게시글 번호
	 * @return 게시글 비밀번호
	 */
	@Select("SELECT password FROM BOARD WHERE board_no = #{boardNo}")
	public String getBoardPassword(int boardNo);

	/**
	 * 첨부파일 조회
	 * @param a boardNo와 attachNo가 담긴 AttachNum
	 * @return DB에서 가져온 파일 메타데이터 
	 */
	public Optional<AttachMetadata> getAttach(AttachNum attach);

	/**
	 * 첨부파일 목록 조회
	 * @param boardNo 게시글 번호
	 * @return DB에서 가져온 파일 메타데이터들
	 */
	public List<AttachMetadata> getAttachList(int boardNo);

	/**
	 * 댓글 목록 조회
	 * @param boardNo 게시글 번호
	 * @return 댓글 목록
	 */
	@Select("SELECT * FROM REPLY WHERE board_no = #{boardNo} ORDER BY created")
	public List<ReplySelect> getReplyList(int boardNo);


	/**
	 * 조회되는 게시글의 조회수 증가
	 * @param boardNo 증가될 게시글 번호
	 */
	@Update("UPDATE BOARD SET VIEWS = VIEWS + 1 WHERE board_no = #{boardNo}")
	public void increaseViews(int boardNo);

	/**
	 * 게시글 등록
	 * @param b 등록할 게시글
	 */
	public void insertBoard(BoardInsert board);

	/**
	 * 게시글 수정
	 * @param b 수정할 게시글
	 */
	public void updateBoard(BoardUpdate board);

	/**
	 * 댓글 등록
	 * @param r 등록할 댓글
	 */
	public void insertReply(ReplyInsert reply);

	/**
	 * 게시글의 모든 댓글 삭제
	 * @param boardNo 삭제할 댓글의 게시글 번호
	 */
	@Delete("DELETE FROM REPLY WHERE board_no = #{boardNo}")
	public void deleteReplyList(int boardNo);

	/**
	 * 게시글 삭제
	 * @param b 삭제할 게시글
	 */
	@Delete("DELETE FROM BOARD WHERE board_no = #{boardNo}")
	public void deleteBoard(int boardNo);

	/**
	 * 첨부파일들 등록
	 * @param a 첨부파일들
	 */
	public void insertAttachList(List<AttachMetadata> attachs);

	/**
	 * 해당 게시글의 모든 첨부파일 삭제
	 * @param boardNo 삭제할 첨부파일의 게시글 번호
	 */
	@Delete("DELETE FROM ATTACH WHERE board_no = #{boardNo}")
	public void deleteAllAttach(int boardNo);

	/**
	 * 해당 게시글의 특정 첨부파일 삭제
	 * @param a 삭제할 첨부파일 목록
	 */
	public void deleteAttachList(List<AttachNum> attachs);
}
