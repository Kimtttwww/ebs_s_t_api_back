package com.eb_study.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.eb_study.board.free.model.dto.AttachMetadata;

@Component
public class DefaultFileManager implements FileManager {
	private final String STORAGE;

	private final FileRenamePolish fileRenamePolish;


	/**
	 * @throws IOException 저장 위치 사용 불가
	 */
	public DefaultFileManager(@Value("${property.file.free}") String storage, @Autowired FileRenamePolish polish) throws IOException {
		File folder = new File(storage);
		if (!folder.exists() && !folder.mkdirs() && folder.canRead() && folder.canWrite())
			throw new IOException("저장 위치 사용 불가");
		this.fileRenamePolish = polish;
		this.STORAGE = storage;
	}


	public List<AttachMetadata> makeMetadataFromFile(int boardNo, List<MultipartFile> files) {
		List<AttachMetadata> list = new ArrayList<>(files.size());
		Iterator<MultipartFile> iterator = files.iterator();

//		반복조건: 이미 있는 번호를 피해 모든 파일에 번호를 부여해야함
		for (int i = 1; iterator.hasNext(); i++) {
			MultipartFile file = iterator.next();

			if (file == null || file.isEmpty()) {
				list.add(null);
				continue;
			}

			String[] fileName = file.getOriginalFilename().split("[.]");
			AttachMetadata a = AttachMetadata.builder()
					.boardNo(boardNo)
					.attachNo(i)
					.fileOrigin(fileName[0])
					.fileRename(fileRenamePolish.fileRename())
					.ext(fileName[1])
					.build();
			list.add(a);
		}
		return list;
	}

	public void validateFileList(List<MultipartFile> files) throws IllegalArgumentException {
		boolean valid = true;

		for (Iterator<MultipartFile> iterator = files.iterator(); valid && iterator.hasNext();) {
			MultipartFile file = iterator.next();

//			TODO valid 분리?
//			1차 검사
			valid = file != null && !file.isEmpty() &&	// 파일이 있으면서
					file.getContentType().toLowerCase().startsWith("image");	// MIME 이 image 인 경우

			Optional<MediaType> mime = MediaTypeFactory.getMediaType(file.getOriginalFilename());
//			2차 검사, 확장자로 추정한 MIME 이 image 인 경우
			valid = valid && !mime.isEmpty() && mime.get().getType().equals(MediaType.IMAGE_JPEG.getType());
		}

		if (!valid) throw new IllegalArgumentException("유효하지 않은 파일");
	}

	/**
	 * @apiNote attachs는 files의 요소들과 순서를 필히 유지해야 한다.<p><b>어느 한쪽이 없으면 저장하지 않고, 다르게 있다면 논리적 오류 발생<b><p>
	 */
	public void fileSave(List<MultipartFile> files, List<AttachMetadata> attachs) throws IOException, IllegalStateException, NullPointerException {
		if (files == null || attachs == null) throw new NullPointerException("files & attachs can't NULL");
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			AttachMetadata attach = attachs.get(i);
			if (file != null && !file.isEmpty() && attach != null)
				file.transferTo(new File(STORAGE, String.format("%s.%s", attach.getFileRename(), attach.getExt())));
		}
	}

	public void fileRemove(List<AttachMetadata> attachs) {
		if(attachs == null || attachs.isEmpty()) return;

		for (AttachMetadata attach : attachs) {
			if (attach != null)
			new File(STORAGE , String.format("%s.%s", attach.getFileRename(), attach.getExt())).delete();
		}
	}

	public FileSystemResource getAttachFromSystem(AttachMetadata attachs) {
		if (attachs == null) throw new NullPointerException("파일이 갖고 싶으면 메타데이터를 주세여");
		File file = new File(STORAGE, String.format("%s.%s", attachs.getFileRename(), attachs.getExt()));
		if (!file.exists()) return null;

		return new FileSystemResource(file);
	}
}
