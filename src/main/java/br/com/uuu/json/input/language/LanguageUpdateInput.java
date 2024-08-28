package br.com.uuu.json.input.language;

import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class LanguageUpdateInput {

	@Schema(description = "Código do idioma", example = "ja_JP")
	private Optional<String> code;
	
	@Schema(description = "Nome do idioma", example = "Japonês")
	private Optional<String> name;
	
	public LanguageUpdateInput() {
		code = Optional.empty();
		name = Optional.empty();
	}

}
