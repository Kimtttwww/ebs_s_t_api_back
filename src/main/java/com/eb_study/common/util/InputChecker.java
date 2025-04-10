package com.eb_study.common.util;

public class InputChecker {
	public static String htmlEntityFilter(String input) {
		if (input != null) input = input
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("&", "&amp;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;")
				.trim();
		return input;
	}
}
