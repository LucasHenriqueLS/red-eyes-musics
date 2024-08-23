package br.com.uuu.json.input.language;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LanguageCreateInput {

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Código do idioma", example = "ja_JP")
	private String code;
	
	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Nome do idioma", example = "Japonês")
	private String name;

}
