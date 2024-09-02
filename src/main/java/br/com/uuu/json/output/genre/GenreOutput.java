package br.com.uuu.json.output.genre;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class GenreOutput {

	private String id;
	
	private String name;
	
	private String description;

}
