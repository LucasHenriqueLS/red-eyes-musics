package br.com.uuu.json.output.language;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class LanguageOutput {

	private String id;
	
	private String code;
	
	private String name;

}
