package br.com.uuu.json.input.artist;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
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
public class ArtistUpdateInput {

	@Size(min = 1, message = "não pode ser vazio se não for nulo")
	@Schema(description = "Nomes do artista", example = "[\"Michael Jackson\", \"Michael Joseph Jackson\"]")
	private List<String> names;

	@Schema(description = "Bio do artista", example = "Michael Jackson foi um cantor, compositor e dançarino norte-americano, amplamente considerado o Rei do Pop e um dos artistas mais influentes da história da música.")
	private String bio;

	@Size(min = 1, message = "não pode ser vazio se não for nulo")
	@Schema(description = "IDs dos gêneros", example = "[\"d87179e9c79647a557f17b95\", \"77b969e7a595f1d87174579c\"]")
	private List<String> genreIds;

	@Schema(description = "Link da imagem do artista", example = "https://example.com/images/michael_jackson.jpg")	
	private String imageUrl;

}
