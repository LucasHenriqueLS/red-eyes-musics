package br.com.uuu.json.input.artist;

import java.util.List;
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
public class ArtistUpdateInput {

	@Schema(description = "Nomes do artista", example = "[\"Mamiko Noto\", \"Noto Mamiko\"]")
	private Optional<List<String>> names;

	@Schema(description = "Bio do artista", example = "Mamiko Noto é uma dubladora japonesa afiliada a Office Osawa.")
	private Optional<String> bio;

	@Schema(description = "IDs dos gêneros", example = "[\"d87179e9c79647a557f17b95\", \"77b969e7a595f1d87174579c\"]")
	private Optional<List<String>> genreIds;

	@Schema(description = "Link da imagem do artista", example = "????")	
	private Optional<String> imageUrl;

	public ArtistUpdateInput() {
		names = Optional.empty();
		bio = Optional.empty();
		genreIds = Optional.empty();
		imageUrl = Optional.empty();
	}

}
