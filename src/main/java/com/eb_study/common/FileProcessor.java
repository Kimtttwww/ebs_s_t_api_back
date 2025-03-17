package com.eb_study.common;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.eb_study.board.free.model.dto.Attach;

public class FileProcessor {
	private final static String STORAGE = "C:/tools/uploads/ebStudy/free";


	public FileProcessor() throws IOException {
		File folder = new File(STORAGE);
		if (!folder.exists() && !folder.mkdirs()) throw new IOException("저장 위치 배정 실패");
	}


	/**
	 * multipartfile[] -> list<attach> + save
	 * @param boardNo 게시글 번호
	 * @param files 첨부파일들
	 * @return 저장된 첨부파일 metadata들
	 */
	public List<Attach> multipartFileToAttachs(int boardNo, List<MultipartFile> files) throws IllegalStateException, IOException {
		List<Attach> list = null;
		
		if (files != null) {
			int i = 1;
			list = new ArrayList<>(files.size());
			
			for (MultipartFile file : files) {
				if (file != null && !file.isEmpty()) {
					String[] fileName = file.getOriginalFilename().split("[.]");
					String fileRename = fileRenamePolicy(boardNo, i);

					Attach a = Attach.builder()
							.boardNo(boardNo)
							.attachNo(i++)
							.fileOrigin(fileName[0])
							.fileRename(fileRename)
							.ext(fileName[1])
							.build();

					list.add(a);
					file.transferTo(new File(STORAGE, fileRename));
				}
			}
		}

		return list;
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
