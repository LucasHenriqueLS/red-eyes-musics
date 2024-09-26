package br.com.uuu.util;

import static org.assertj.core.api.Assertions.assertThat;


public class TestUtils {

	public static <T> void checkExpectedResults(T result, T expected) {
    	assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}

}
