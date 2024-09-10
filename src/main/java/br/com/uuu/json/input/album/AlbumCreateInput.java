package br.com.uuu.json.input.album;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
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
public class AlbumCreateInput {

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Título do álbum", example = "Temple of Love")
	private String title;

	@PastOrPresent(message = "deve ser a data atual ou uma data passada")
	@Schema(description = "Data de lançamento do álbum", example = "2006-06-30")
	private LocalDate releaseDate;

	@NotEmpty(message = "não pode ser nulo ou vazio")
	@Schema(description = "IDs dos artistas", example = "[\"64957a557f1d87179e9c77b9\", \"95f1d87179e7a54579c77b96\"]")
	private List<String> artistIds;

	@Schema(description = "Link da imagem da capa do álbum", example = "????")
	private String coverUrl;

	@NotEmpty(message = "não pode ser nulo ou vazio")
	@Schema(description = "IDs dos gêneros", example = "[\"d87179e9c79647a557f17b95\", \"77b969e7a595f1d87174579c\"]")
	private List<String> genreIds;

	@Schema(description = "Nome da gravadora", example = "Sony Music")
	private String recordCompanyName;

}
