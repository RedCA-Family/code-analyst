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
