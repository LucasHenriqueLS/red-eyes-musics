package br.com.uuu.json.input.album;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

	@NotBlank(message = "não pode ser nulo, vazio ou conter somente espaços em branco")
	@Schema(description = "Título do álbum", example = "Thriller")
	private String title;

	@NotNull(message = "não pode ser nulo")
	@PastOrPresent(message = "deve ser a data atual ou uma data passada")
	@Schema(description = "Data de lançamento do álbum", example = "1982-11-30")
	private LocalDate releaseDate;

	@NotEmpty(message = "não pode ser nulo ou vazio")
	@Schema(description = "IDs dos artistas", example = "[\"a87179e9c79647a557f17b95\"]")
	private List<String> artistIds;

	@Schema(description = "Link da imagem da capa do álbum", example = "https://example.com/images/thriller.jpg")
	private String coverUrl;

	@NotEmpty(message = "não pode ser nulo ou vazio")
	@Schema(description = "IDs dos gêneros", example = "[\"b969e7a595f1d87174579c\", \"c87179e9c79647a557f17b95\"]")
	private List<String> genreIds;

	@Schema(description = "Nome da gravadora", example = "Epic Records")
	private String recordCompanyName;

}
