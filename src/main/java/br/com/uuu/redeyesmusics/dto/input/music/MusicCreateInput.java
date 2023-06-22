package br.com.uuu.redeyesmusics.dto.input.music;

import java.util.List;
import java.util.Map;

import br.com.uuu.redeyesmusics.nosql.util.Genre;
import br.com.uuu.redeyesmusics.nosql.util.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MusicCreateInput {
	
	@NotBlank
	private String artistId;
	
	@NotNull
	private List<Genre> genres;
	
	@NotNull
	private Language originalLanguage;
	
	@NotNull
	private Map<Language, String> nameByLanguages;
	
	@NotNull
	private Map<Language, String> lyricByLanguages;
	
	private List<String> composersIds;
	
	@NotBlank
	private String submitterId;
}
