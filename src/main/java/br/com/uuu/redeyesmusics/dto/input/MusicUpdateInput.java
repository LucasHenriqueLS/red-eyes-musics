package br.com.uuu.redeyesmusics.dto.input;

import java.util.List;
import java.util.Map;

import br.com.uuu.redeyesmusics.nosql.util.Genre;
import br.com.uuu.redeyesmusics.nosql.util.Language;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class MusicUpdateInput {

	private List<Genre> genres;

	private Language originalLanguage;

	private Map<Language, String> names;

	private Map<Language, String> lyrics;
	
	private List<String> composersIds;
	
	@NotBlank
	private String proofreaderId;
	
	private List<Language> updatedLanguages;
}
