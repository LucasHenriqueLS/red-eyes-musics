package br.com.uuu.json.input.album;

import java.time.LocalDate;
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
public class AlbumUpdateInput {

	@Schema(description = "Título do álbum", example = "Temple of Love")
	private Optional<String> title;

	@Schema(description = "Data de lançamento do álbum", example = "2006-06-30")
	private Optional<LocalDate> releaseDate;

	@Schema(description = "IDs dos artistas", example = "[\"64957a557f1d87179e9c77b9\", \"95f1d87179e7a54579c77b96\"]")
	private Optional<List<String>> artistIds;

	@Schema(description = "Link da imagem da capa do álbum", example = "????")
	private Optional<String> coverUrl;

	@Schema(description = "IDs dos gêneros", example = "[\"d87179e9c79647a557f17b95\", \"77b969e7a595f1d87174579c\"]")
	private Optional<List<String>> genreIds;

	@Schema(description = "Nome da gravadora", example = "Sony Music")
	private Optional<String> recordCompanyName;
	
	public AlbumUpdateInput() {
		title = Optional.empty();
		releaseDate = Optional.empty();
		artistIds = Optional.empty();
		coverUrl = Optional.empty();
		genreIds = Optional.empty();
		recordCompanyName = Optional.empty();
	}
}
