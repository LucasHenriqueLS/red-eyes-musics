package br.com.uuu.json.input.genre;

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
public class GenreCreateInput {

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Nome do gênero", example = "J-Rock")
	private String name;

	@Schema(description = "Descrição do gênero", example = "Rock japonês, também conhecido pela abreviatura J-rock é a música rock proveniente do Japão.")
	private String description;

}
