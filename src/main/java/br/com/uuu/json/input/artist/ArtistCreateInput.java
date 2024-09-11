package br.com.uuu.json.input.artist;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
public class ArtistCreateInput {

	@NotEmpty(message = "não pode ser nulo ou vazio")
	@Schema(description = "Nomes do artista", example = "[\"Michael Jackson\", \"Michael Joseph Jackson\"]")
	private List<String> names;

	@Schema(description = "Bio do artista", example = "Michael Jackson foi um cantor, compositor e dançarino norte-americano, amplamente considerado o Rei do Pop e um dos artistas mais influentes da história da música.")
	private String bio;

	@NotEmpty(message = "não pode ser nulo ou vazio")
	@Schema(description = "IDs dos gêneros", example = "[\"d87179e9c79647a557f17b95\", \"77b969e7a595f1d87174579c\"]")
	private List<String> genreIds;

	@Schema(description = "Link da imagem do artista", example = "https://example.com/images/michael_jackson.jpg")	
	private String imageUrl;

}
