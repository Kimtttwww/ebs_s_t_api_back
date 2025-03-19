package com.eb_study.common;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.multipart.MultipartFile;

import com.eb_study.board.free.model.dto.AttachSelect;

import jakarta.validation.constraints.NotNull;

public class FileProcessor {
	private final static String STORAGE = "C:/tools/uploads/ebStudy/free";


	public FileProcessor() throws IOException {
		File folder = new File(STORAGE);
		if (!folder.exists() && !folder.mkdirs()) throw new IOException("저장 위치 배정 실패");
	}


	/**
	 * multipartfile -> attach, + save
	 * @param boardNo 게시글 번호
	 * @param files 첨부파일들
	 * @return 저장된 첨부파일 metadata들(attach)
	 */
	public List<AttachSelect> multipartFileToAttachs(int boardNo, @NotNull List<MultipartFile> files) throws IOException {
		List<AttachSelect> list = null;

		int i = 1;
		list = new ArrayList<>(files.size());

		for (MultipartFile file : files) {
			if (file != null && !file.isEmpty()) {
				String[] fileName = file.getOriginalFilename().split("[.]");
				String fileRename = fileRenamePolicy(boardNo, i);

				AttachSelect a = AttachSelect.builder()
						.boardNo(boardNo)
						.attachNo(i++)
						.fileOrigin(fileName[0])
						.fileRename(fileRename)
						.ext(fileName[1])
						.build();

				list.add(a);
				file.transferTo(new File(STORAGE, String.format("%s.%s", fileRename, fileName[1])));
			}
		}

		return list;
	}

	/**
	 * 업로드하고자 하는 파일들의 유형 검사(image만 통과)
	 * @param files 업로드 하고자 하는 파일들
	 * @return 모든 파일의 유형과 image의 일치 여부
	 */
	public boolean uploadFileFilter(List<MultipartFile> a) {
		boolean valid = true;

		for (Iterator<MultipartFile> iterator = a.iterator(); valid && iterator.hasNext();) {
			MultipartFile file = iterator.next();

//			1차 검사
			valid = file != null && !file.isEmpty() &&	// 파일이 있으면서
					file.getContentType().toLowerCase().startsWith("image");	// MIME 이 image 인 경우

			Optional<MediaType> mime = MediaTypeFactory.getMediaType(file.getOriginalFilename());
//			2차 검사, 확장자로 추정한 MIME 이 image 인 경우
			valid = valid && !mime.isEmpty() && mime.get().getType().toLowerCase().startsWith("image");
		}

		return valid;
	}

	/**
	 * 파일 삭제
	 * @param a 업로드된 파일에 관한 정보들
	 */
	public void fileRemove(List<AttachSelect> a) {
		if(a == null || a.isEmpty()) return;

		for (AttachSelect attach : a) {
			new File(STORAGE , String.format("%s.%s", attach.getFileRename(), attach.getExt())).deleteOnExit();
		}
	}

	/**
	 * 저장된 파일의 정보 가져오기
	 * @param a 파일 메타데이터
	 * @return 실제 파일
	 */
	public FileSystemResource getAttachFromSystem(@NotNull AttachSelect a) {
		File file = new File(STORAGE, String.format("%s.%s", a.getFileRename(), a.getExt()));
		if (!file.exists()) return null;

		return new FileSystemResource(file);
	}


	/**
	 * 첨부파일 rename policy
	 * @param boardNo 등록할 게시글 번호
	 * @param attachNo 첨부파일 번호
	 * @return 저장용 첨부파일 이름
	 */
	private static String fileRenamePolicy(int boardNo, int attachNo) {
		LocalDateTime now = LocalDateTime.now();
		String date = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
		int random = (int) (Math.random() * 90000 + 10000);

		return String.format("%d_%d_%s_%d", boardNo, attachNo, date, random);
	}
}
