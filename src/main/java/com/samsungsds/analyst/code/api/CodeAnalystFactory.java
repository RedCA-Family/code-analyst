package com.samsungsds.analyst.code.api;

import com.samsungsds.analyst.code.api.impl.CodeAnalystImpl;

public class CodeAnalystFactory {
	public static CodeAnalyst create() {
		return new CodeAnalystImpl();
	}
}
