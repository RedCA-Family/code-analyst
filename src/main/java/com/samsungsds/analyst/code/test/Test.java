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

import java.io.File;
import java.io.IOException;

public class Test {
	public void duplatedMethod(String input) {
		if (input != null) {
			System.out.println("Hello, World");
		}
		if (input != null) {
			System.out.println("Hello, World");
			System.out.println("Hello, World");
		}
		if (input != null) {
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
		}
		if (input != null) {
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
		}
		if (input != null) {
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
		}
		
		try {
			File tmp = File.createTempFile("test", "");
			
			tmp.delete();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void duplatedMethod2(String input) {
		if (input != null) {
			System.out.println("Hello, World");
		}
		if (input != null) {
			System.out.println("Hello, World");
			System.out.println("Hello, World");
		}
		if (input != null) {
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
		}
		if (input != null) {
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
		}
		if (input != null) {
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
			System.out.println("Hello, World");
		}
	}
}
