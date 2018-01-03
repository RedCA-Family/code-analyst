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
