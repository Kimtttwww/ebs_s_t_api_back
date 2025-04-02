package com.eb_study.file;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class DefaultFileRenamePolish implements FileRenamePolish {
	public String fileRename() {
		LocalDateTime now = LocalDateTime.now();
		String date = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
		int random = (int) (Math.random() * 90000 + 10000);

		return String.format("%s_%d", date, random);
	}
}
