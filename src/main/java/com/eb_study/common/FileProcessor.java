package com.eb_study.common;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.eb_study.board.free.model.dto.AttachMetadata;

import jakarta.validation.constraints.NotNull;

@Component
public class FileProcessor {
	private final static String STORAGE = "C:/tools/uploads/ebStudy/free";


	/**
	 * @throws IOException 저장 위치 사용 불가
	 */
	public FileProcessor() throws IOException {
		File folder = new File(STORAGE);
		if (!folder.exists() && !folder.mkdirs() && folder.canRead() && folder.canWrite())
			throw new IOException("저장 위치 사용 불가");
	}


	/**
	 * multipartfile list로 attachSelect list 생성
	 * @param boardNo 게시글 번호
	 * @param files 첨부파일들
	 * @param skipNums 파일에 부여해야할 번호 중 피해야 할 번호(선택사항)
	 * @return 저장된 첨부파일 metadata들(attachSelect)
	 */
	public List<AttachMetadata> multipartFileToAttachs(int boardNo, @NotNull List<MultipartFile> files, int... skipNums) {
		List<AttachMetadata> list = new ArrayList<>(files.size());
		Iterator<MultipartFile> i = files.iterator();
		int fileIndex = 1, skipNumsIndex = 0;

		skipNums = Optional.ofNullable(skipNums).orElse(new int[0]);
		if (skipNums.length > 1) Arrays.sort(skipNums);

//		반복조건: 이미 있는 번호를 피해 모든 파일에 번호를 부여해야함
		while (i.hasNext() || skipNumsIndex < skipNums.length) {
//			피해야 할 번호인지 검사
			if (skipNumsIndex < skipNums.length && fileIndex == skipNums[skipNumsIndex]) {
				fileIndex++; skipNumsIndex++; continue;
			}

			MultipartFile file = i.next();
			String[] fileName = file.getOriginalFilename().split("[.]");
			String fileRename = fileRenamePolicy(boardNo, fileIndex);

			AttachMetadata a = AttachMetadata.builder()
					.boardNo(boardNo)
					.attachNo(fileIndex++)
					.fileOrigin(fileName[0])
					.fileRename(fileRename)
					.ext(fileName[1])
					.build();

			list.add(a);
		}

		return list;
	}

	/**
	 * 업로드하고자 하는 파일들의 유형 검사(image만 통과)
	 * @param files 업로드 하고자 하는 파일들
	 * @return 모든 파일의 유형과 image의 일치 여부
	 * @throws IOException 유효하지 않은 파일
	 */
	public boolean validateFileList(@NotNull List<MultipartFile> files) throws IOException {
		boolean valid = true;

		for (Iterator<MultipartFile> iterator = files.iterator(); valid && iterator.hasNext();) {
			MultipartFile file = iterator.next();

//			1차 검사
			valid = file != null && !file.isEmpty() &&	// 파일이 있으면서
					file.getContentType().toLowerCase().startsWith("image");	// MIME 이 image 인 경우

			Optional<MediaType> mime = MediaTypeFactory.getMediaType(file.getOriginalFilename());
//			2차 검사, 확장자로 추정한 MIME 이 image 인 경우
			valid = valid && !mime.isEmpty() && mime.get().getType().equals(MediaType.IMAGE_JPEG.getType());
		}

		if (!valid) throw new IllegalArgumentException("유효하지 않은 파일");
		return valid;
	}

	/**
	 * 파일 저장
	 * @param files 업로드 할 파일들
	 * @param a 업로드 할 파일의 메타데이터
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @apiNote a는 files의 요소들과 순서를 필히 유지해야 한다.
	 */
	public void fileSave(@NotNull List<MultipartFile> files, @NotNull List<AttachMetadata> a) throws IllegalStateException, IOException {
		for (int i = 0; i < files.size(); i++) {
			files.get(i).transferTo(new File(STORAGE, String.format("%s.%s", a.get(i).getFileRename(), a.get(i).getExt())));
		}
	}

	/**
	 * 파일 삭제
	 * @param a 업로드된 파일에 관한 정보들
	 */
	public void fileRemove(List<AttachMetadata> a) {
		if(a == null || a.isEmpty()) return;

		for (AttachMetadata attach : a) {
			new File(STORAGE , String.format("%s.%s", attach.getFileRename(), attach.getExt())).delete();
		}
	}

	/**
	 * 저장된 파일의 정보 가져오기
	 * @param a 파일 메타데이터
	 * @return 실제 파일
	 */
	public FileSystemResource getAttachFromSystem(@NotNull AttachMetadata a) {
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
