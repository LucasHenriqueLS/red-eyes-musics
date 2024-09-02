package br.com.uuu.json.input.genre;

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
public class GenreUpdateInput {

	@Schema(description = "Nome do gênero", example = "J-Rock")
	private Optional<String> name;
	
	@Schema(description = "Descrição do gênero", example = "Rock japonês, também conhecido pela abreviatura J-rock é a música rock proveniente do Japão.")
	private Optional<String> description;
	
	public GenreUpdateInput() {
		name = Optional.empty();
		description = Optional.empty();
	}

}
