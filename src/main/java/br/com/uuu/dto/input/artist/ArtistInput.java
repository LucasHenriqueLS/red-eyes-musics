package br.com.uuu.dto.input.artist;

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
	@Schema(description = "Nome do artista", example = "Mamiko Noto")
	private String name;

	@Schema(description = "Outros nomes do artista", example = "[\"Noto Mamiko\"]")
	private List<String> otherNames;
}
