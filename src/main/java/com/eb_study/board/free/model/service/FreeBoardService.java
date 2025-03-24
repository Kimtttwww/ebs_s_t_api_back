package com.eb_study.board.free.model.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.eb_study.common.FileProcessor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FreeBoardService {
	private final FreeBoardDao dao;

	private final FileProcessor processor;


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
		return dao.getAllBoardCount(option).get();
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
	public BoardSelect getBoard(int boardNo, boolean doIncreaseViews) throws SQLException {
		if (doIncreaseViews) dao.increaseViews(boardNo);
		return dao.getBoard(boardNo).get();
	}

	/**
	 * 게시글 등록
	 * @param b 등록할 게시글
	 * @return 등록된 게시글의 게시글 번호
	 * @throws SQLException 게시글 등록 실패
	 * @throws IOException 첨부파일 저장 경로 접근 불가 | ?
	 */
	@Transactional(rollbackFor = Exception.class)
	public int insertBoard(BoardInsert b, List<MultipartFile> files) throws SQLException, IOException {
		boolean exist = files != null && !files.isEmpty();

		b.setAttach(exist);
		dao.insertBoard(b);

		if (exist) insertAttachList(b.getBoardNo(), files);

		return b.getBoardNo();
	}

	/**
	 * 게시글 수정
	 * @param b 수정할 게시글
	 * @param files 새로 등록할 첨부파일들
	 * @return 수정된 게시글의 게시글 번호
	 * @throws IllegalArgumentException 비밀번호 불일치 | ?
	 * @throws IOException 저장위치 사용 불가 | ?
	 * @throws SQLException 게시글 수정 실패
	 */
	@Transactional(rollbackFor = Exception.class)
	public int updateBoard(BoardUpdate b, List<MultipartFile> files) throws IllegalArgumentException, IOException, SQLException {
		if (!matchBoardPassword(b.getBoardNo(), b.getPassword()))
			throw new IllegalArgumentException("비밀번호 불일치");

		dao.updateBoard(b);
		updateAttach(b.getBoardNo(), b.getAttach(), files);

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
	public void deleteBoard(BoardDelete b) throws IllegalArgumentException, SQLException {
		if (!matchBoardPassword(b.getBoardNo(), b.getPassword()))
			throw new IllegalArgumentException("비밀번호 불일치");
		List<AttachMetadata> a = dao.getAllAttachFromBoard(b.getBoardNo());

		dao.deleteAllReply(b.getBoardNo());
		dao.deleteAllAttach(b.getBoardNo());
		dao.deleteBoard(b.getBoardNo());

		processor.fileRemove(a);
	}

	/**
	 * 게시글 번호와 첨부파일 번호로 파일 메타데이터 조회
	 * @param boardNo 게시글 번호
	 * @param attachNo 첨부파일 번호
	 * @return 파일 메타데이터
	 */
	public AttachMetadata getAttach(AttachNum a) {
		return dao.getAttach(a);
	}

	/**
	 * 파일 메타데이터로 실제 첨부파일 조회
	 * @param a 파일 메타데이터
	 * @return 실제 첨부파일
	 * @throws IOException 저장 위치 사용 불가
	 */
	public FileSystemResource getAttachResource(@NotNull AttachMetadata a) throws IOException {
		return processor.getAttachFromSystem(a);
	}

	/**
	 * 첨부파일 DB 등록, 서버 저장
	 * @param boardNo 게시글 번호
	 * @param files 업로드 할 파일들(1개 이상 필수)
	 * @throws IOException 저장위치 사용불가 | 유효하지 않은 파일 | ?
	 * @throws SQLException 주어진 첨부파일 전부/일부 등록 실패
	 */
	public void insertAttachList(int boardNo, @NotNull @NotEmpty List<MultipartFile> files) throws IOException, SQLException {
		processor.validateFileList(files);
		List<AttachMetadata> a = processor.multipartFileToAttachs(boardNo, files);

		dao.insertAttachList(a);
		processor.fileSave(files, a);
	}


	/**
	 * 게시글 변경 전 비밀번호 일치 검사
	 * @param boardNo 게시글 번호
	 * @param password 사용자가 입력한 비밀번호
	 * @return 비밀번호 일치 여부
	 */	// TODO ? 인자 검증은 누구의 역할인가?
	public boolean matchBoardPassword(int boardNo, @NotEmpty String password) {
		return new BCryptPasswordEncoder().matches(password, dao.getBoardPassword(boardNo).get());
	}

	/**
	 * @param boardNo 게시글 번호
	 * @param afterAttach 수정하지 않고 남길 기존의 첨부파일
	 * @param files 새로 등록할 첨부파일들
	 * @throws IOException 저장위치 사용 불가 | ?
	 * @throws SQLException 주어진 첨부파일들 전부/일부 삭제 실패
	 */
	private void updateAttach(int boardNo, List<AttachNum> afterAttach, List<MultipartFile> files) throws IOException, SQLException {
		List<AttachMetadata> beforeAttach = dao.getAllAttachFromBoard(boardNo);
		boolean beforeHasSomething = beforeAttach != null && !beforeAttach.isEmpty(),
		afterHasSomething = afterAttach != null && !afterAttach.isEmpty(),
		filesHasSomething = files != null && !files.isEmpty();

//		beforeAttach, afterAttach, files 셋 다 없으면 안해도 됨
		if (!beforeHasSomething && !afterHasSomething && !filesHasSomething) return;

		if (beforeHasSomething && beforeAttach.size() != Optional.ofNullable(afterAttach).orElse(new ArrayList<>()).size()) {	// 삭제할 게 있으면
//			기존 attmeta 와 변경한 attmeta을 비교하여 삭제할 list<attmeta> 생성(bef - aft)
			if (afterHasSomething) beforeAttach.removeIf(a -> afterAttach.contains(a));

//			DB 삭제 및 저장된 파일 삭제
			dao.deleteAttachList(beforeAttach.parallelStream().map(a -> (AttachNum) a).toList());
			processor.fileRemove(beforeAttach);
		}

		if (filesHasSomething) {	// 추가할 게 있으면
			processor.validateFileList(files);

//			새로 등록할 files로 기존에 존재하던 첨부파일(aft) 번호를 피해서 list<attmeta>를 만든다
			List<AttachMetadata> newAttach = processor.multipartFileToAttachs(boardNo, files,
					afterHasSomething ? afterAttach.parallelStream().mapToInt(AttachNum::getAttachNo).toArray() : null);

//			등록 및 저장
			dao.insertAttachList(newAttach);
			processor.fileSave(files, newAttach);
		}
	}
}
