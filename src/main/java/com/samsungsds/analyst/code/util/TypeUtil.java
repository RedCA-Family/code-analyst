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
