/*
Copyright 2018 Samsung SDS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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
