package br.com.uuu.util;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;
import java.util.Map;

import org.springframework.test.web.servlet.ResultActions;

public class Checker {

	public static <T> void checkList(ResultActions response, List<T> list, String jsonPath, TriConsumer<ResultActions, T, String> checker) {
        try {
			response
			    .andExpect(jsonPath(String.format("%s", jsonPath)).isArray())
			    .andExpect(jsonPath(String.format("%s.length()", jsonPath)).value(list.size()));

	        for (int i = 0; i < list.size(); i++) {
	            checker.accept(response, list.get(i), String.format("%s[%d]", jsonPath, i));
	        }
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

	public static <K, V> void checkMap(ResultActions response, Map<K, V> map, String jsonPath, TriConsumer<ResultActions, V, String> checker) {
		try {
			response
	        	.andExpect(jsonPath(jsonPath).isMap())
	        	.andExpect(jsonPath(String.format("%s.length()", jsonPath)).value(map.size()));
	        for (var entry : map.entrySet()) {
	        	checker.accept(response, entry.getValue(), String.format("%s.%s", jsonPath, entry.getKey()));	        	
	        }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

	public static <T> void check(ResultActions response, T value, String jsonPath) {
    	try {
			response.andExpect(jsonPath(jsonPath).value(value));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
	
	public static Boolean jsonPathExists(ResultActions response, String jsonPath) throws Exception {
        try {
            response.andExpect(jsonPath(jsonPath).exists());
            return Boolean.TRUE;
        } catch (AssertionError e) {
            return Boolean.FALSE;
        }
    }

}
