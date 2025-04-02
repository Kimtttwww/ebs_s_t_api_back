package com.eb_study.file;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import com.eb_study.board.free.model.dto.AttachMetadata;

public interface FileManager {
	/**
	 * multipartfile list로 attachSelect list 생성
	 * @param boardNo 게시글 번호
	 * @param files 첨부파일들
	 * @return 저장된 첨부파일 metadata들(attachSelect)
	 */
	List<AttachMetadata> makeMetadataFromFile(int boardNo, List<MultipartFile> files);

	/**
	 * 업로드하고자 하는 파일들의 유형 검사(image만 통과)
	 * @param files 업로드 하고자 하는 파일들
	 * @return 모든 파일의 유형과 image의 일치 여부
	 * @throws IOException 유효하지 않은 파일
	 */
	public void validateFileList(List<MultipartFile> files);

	/**
	 * 파일 저장
	 * @param files 업로드 할 파일들
	 * @param attachs 업로드 할 파일의 메타데이터
	 * @throws NullPointerException 인자에 null이 주어짐
	 * @throws IOException ?
	 * @throws IllegalStateException 파일 이미 저장됨
	 */
	public void fileSave(List<MultipartFile> files, List<AttachMetadata> attachs) throws IOException, IllegalStateException, NullPointerException;

	/**
	 * 파일 삭제
	 * @param attachs 업로드된 파일에 관한 정보들
	 */
	public void fileRemove(List<AttachMetadata> attachs);

	/**
	 * 저장된 파일의 정보 가져오기
	 * @param attachs 파일 메타데이터
	 * @return 실제 파일
	 */
	public FileSystemResource getAttachFromSystem(AttachMetadata attachs);
}
