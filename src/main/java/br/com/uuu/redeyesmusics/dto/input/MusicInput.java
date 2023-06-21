package br.com.uuu.redeyesmusics.dto.input;

import br.com.uuu.redeyesmusics.nosql.util.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class MusicInput {
	
	@NotBlank
	private String artistId;
	
	@NotNull
	private Language originalLanguage;
	
	private String composerId;
	
	@NotBlank
	private String submitterId;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String lyric;
}
