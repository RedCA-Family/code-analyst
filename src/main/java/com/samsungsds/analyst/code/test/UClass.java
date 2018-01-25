package com.samsungsds.analyst.code.test;

public class UClass {
	public static final String UNUSED_CONSTANT = "unusedConstantValue";
	private static final String USED_CONSTANT = "usedConstantValue";
	
	public String unusedPublicField;
	String unusedNonPublicField;

	String usedField;
	
	public void unusedPublicMethod() {
		
	}
	
	void unusedNonPublicMethod() {
		
	}
	
	String usedMethod(String p) {
		String usedLocalVariable = USED_CONSTANT;
		String unusedLocalVariable = "unusedLocalValue";
		int usedLocalIntVariable = 1;
		
		System.out.println(usedField);
		System.out.println(usedLocalIntVariable);
		
		return usedLocalVariable;
	}
	
	
	public static void main(String[] args) {
		UClass thisClass = new UClass();
		
		String localVar = "localValue";
		
		thisClass.usedMethod(localVar);
	}
}
