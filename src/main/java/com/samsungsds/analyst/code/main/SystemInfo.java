package com.samsungsds.analyst.code.main;

public class SystemInfo {
	public static void print() {
		System.out.println(os());
		System.out.println(java());
	}

	protected static String java() {
		StringBuilder sb = new StringBuilder();

		sb.append("[JRE : ");
		sb.append("Java ");
		sb.append(System.getProperty("java.version"));
		sb.append(" ");
		sb.append(System.getProperty("java.vendor"));

		String bits = System.getProperty("sun.arch.data.model");

		if ("32".equals(bits) || "64".equals(bits)) {
			sb.append(" (").append(bits).append("-bit)");
		}
		sb.append("]");

		return sb.toString();
	}

	protected static String os() {
		StringBuilder sb = new StringBuilder();

		sb.append("[OS : ");
		sb.append(System.getProperty("os.name"));
		sb.append(" ");
		sb.append(System.getProperty("os.version"));
		sb.append(" ");
		sb.append(System.getProperty("os.arch"));
		sb.append("]");

		return sb.toString();
	}
}
