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

	@NotBlank(message = "não pode ser nulo, vazio ou conter somente espaços em branco")
	@Schema(description = "Nome do gênero", example = "Pop")
	private String name;

	@Schema(description = "Descrição do gênero", example = "O gênero Pop é conhecido por suas melodias cativantes e estrutura musical voltada para o público em geral. Abrange uma variedade de estilos e é popular globalmente.")
	private String description;

}
