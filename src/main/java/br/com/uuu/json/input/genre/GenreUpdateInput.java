package br.com.uuu.json.input.genre;

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
public class GenreUpdateInput {

	@Pattern(regexp = ".*\\S.*", message = "não pode ser vazio ou conter somente espaços em branco se não for nulo")
	@Schema(description = "Nome do gênero", example = "Pop")
	private String name;
	
	@Schema(description = "Descrição do gênero", example = "O gênero Pop é conhecido por suas melodias cativantes e estrutura musical voltada para o público em geral. Abrange uma variedade de estilos e é popular globalmente.")
	private String description;

}
