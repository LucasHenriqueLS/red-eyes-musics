package br.com.uuu.json.input.artist;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ArtistInput {

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Nomes do artista", example = "[\"Mamiko Noto\", \"Noto Mamiko\"]")
	private List<String> names;
}
