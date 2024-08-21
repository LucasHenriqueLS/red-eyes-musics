package br.com.uuu.json.input.song;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SongCreateInput {

	@NotEmpty(message = "não pode ser nulo ou vazio")
	@Schema(description = "IDs dos artistas", example = "[\"64957a557f1d87179e9c77b9\", \"95f1d87179e7a54579c77b96\"]")
	private List<String> artistIds;

	@Schema(description = "Lista de nomes dos compositores", example = "[\"Mamiko Noto\"]")
	private List<String> composerNames;

	@Schema(description = "ID do álbum", example = "17b959647ad87179e9c7557f")
	private String albumId;
	
	@NotEmpty(message = "não pode ser nulo ou vazio")
	@Schema(description = "IDs dos gêneros", example = "[\"d87179e9c79647a557f17b95\", \"77b969e7a595f1d87174579c\"]")
	private List<String> genreIds;

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Id do idioma original da música", example = "f17b9179e9c79645da557877")
	private String originalLanguageId;

	@NotNull(message = "não pode ser nulo")
	@Schema(description = "Detalhes da música para cada código de idioma mapeado", example = "{\"ja_JP\" : {\"title\" : \"Aizome\", \"lyric\" : \"Asaki yumemiji towa ni nageki mo sesu...\\n\\n...Ai ni somete...\", \"submitterId\" : \"64957a557f1d87179e9c77f9\"}}")
	private Map<String, DetailsByLanguageCodeCreateInput> detailsByLanguageCode;

	@Positive(message = "deve ser maior que zero")
	@Schema(description = "Duração da música em segundos", example = "300")
	private Integer durationInSeconds;
	
	@PastOrPresent(message = "deve ser a data atual ou uma data passada")
	@Schema(description = "Data de lançamento da música", example = "2024-01-01")
	private LocalDate releaseDate;
	
	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Link do vídeo da música", example = "https://www.youtube.com/watch?v=TWC1rRn0D4I")	
	private String videoLink;

}
