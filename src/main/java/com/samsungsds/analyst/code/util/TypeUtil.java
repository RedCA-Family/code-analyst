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
package com.samsungsds.analyst.code.util;

public class TypeUtil {
	public static String formatClassNameFromSlashToDot(String className) {
		if(className == null) return className;
		return className.replaceAll("/", "\\.");
	}
	
	public static String[] formatClassNamesFormSlashToDot(String[] classNames) {
		if(classNames == null) return classNames;
		
		String[] formattedClassNames = new String[classNames.length];
		for (int i = 0; i < classNames.length ; i++) {
			formattedClassNames[i] = formatClassNameFromSlashToDot(classNames[i]);
		}
		
		return formattedClassNames;
	}
	
	public static boolean isPrimitiveType(String typeString) {
		String[] primitiveTypes = {"boolean", "char", "byte", "short", "int", "long", "float", "double"};
		for (String type : primitiveTypes) {
			if(type.equals(typeString)) {
				return true;
			}
		}
		return false;
	}
}
