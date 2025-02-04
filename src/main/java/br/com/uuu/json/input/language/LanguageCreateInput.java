package br.com.uuu.json.input.language;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageCreateInput {

	@NotBlank(message = "não pode ser nulo, vazio ou conter somente espaços em branco")
	@Schema(description = "Código do idioma", example = "en_US")
	private String code;
	
	@NotBlank(message = "não pode ser nulo, vazio ou conter somente espaços em branco")
	@Schema(description = "Nome do idioma", example = "Inglês Americano")
	private String name;

}
