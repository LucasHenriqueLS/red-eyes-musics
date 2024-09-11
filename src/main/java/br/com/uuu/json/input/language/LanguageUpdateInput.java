package br.com.uuu.json.input.language;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
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

	@Pattern(regexp = ".*\\S.*", message = "não pode ser vazio ou conter somente espaços em branco se não for nulo")
	@Schema(description = "Código do idioma", example = "en_US")
	private String code;

	@Pattern(regexp = ".*\\S.*", message = "não pode ser vazio ou conter somente espaços em branco se não for nulo")
	@Schema(description = "Nome do idioma", example = "Inglês Americano")
	private String name;

}
