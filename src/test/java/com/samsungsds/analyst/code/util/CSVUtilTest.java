package com.samsungsds.analyst.code.util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class CSVUtilTest {

	@Test
	public void testWithNormalString() {
		// arrange
		String normalString = "aaa";

		// act
		String result = CSVUtil.getCSVStyleString(normalString);

		// assert
		assertThat(result, is("aaa"));
	}

	@Test
	public void testWithSingleQuoteAndCommaString() {
		// arrange
		String normalString = "'a', 'b'";

		// act
		String result = CSVUtil.getCSVStyleString(normalString);

		// assert
		assertThat(result, is("\"'a', 'b'\""));
	}

	@Test
	public void testWithDoubleQuote() {
		// arrange
		String normalString = "\"abc\"";

		// act
		String result = CSVUtil.getCSVStyleString(normalString);

		// assert
		assertThat(result, is("\"\"\"abc\"\"\""));
	}

	@Test
	public void testWithCommaString() {
		// arrange
		String normalString = "a, b";

		// act
		String result = CSVUtil.getCSVStyleString(normalString);

		// assert
		assertThat(result, is("\"a, b\""));
	}

	@Test
	public void testWithNewLineString() {
		// arrange
		String normalString = "aaa\nbbb";

		// act
		String result = CSVUtil.getCSVStyleString(normalString);

		// assert
		assertThat(result, is("\"aaa\nbbb\""));
	}

}
