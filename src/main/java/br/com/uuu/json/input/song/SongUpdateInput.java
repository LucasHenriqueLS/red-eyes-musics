package br.com.uuu.json.input.song;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
public class SongUpdateInput {

	@Size(min = 1, message = "não pode ser vazio se não for nulo")
	@Schema(description = "IDs dos artistas", example = "[\"64957a557f1d87179e9c77b9\"]")
	private List<String> artistIds;

	@Size(min = 1, message = "não pode ser vazio se não for nulo")
	@Schema(description = "Nomes dos compositores", example = "[\"Michael Jackson\"]")
	private List<String> composerNames;

	@Pattern(regexp = ".*\\S.*", message = "não pode ser vazio ou conter somente espaços em branco se não for nulo")
	@Schema(description = "ID do álbum", example = "17b959647ad87179e9c7557f")
	private String albumId;

	@Size(min = 1, message = "não pode ser vazio se não for nulo")
	@Schema(description = "IDs dos gêneros", example = "[\"d87179e9c79647a557f17b95\", \"77b969e7a595f1d87174579c\"]")
	private List<String> genreIds;

	@Pattern(regexp = ".*\\S.*", message = "não pode ser vazio ou conter somente espaços em branco se não for nulo")
	@Schema(description = "Id do idioma original da música", example = "f17b9179e9c79645da557877")
	private String originalLanguageId;

	@Schema(description = "Detalhes da música para cada ID de idioma mapeado", example = "{\"66c8b94ff5249d656c735e3a\" : {\"title\" : \"Billie Jean\", \"lyric\" : \"Billie Jean is not my lover...\", \"submitterId\" : \"64957a557f1d87179e9c77f9\"}}")
	private Map<String, SongDetailsCreateInput> detailsByLanguageId;

	@Positive(message = "deve ser maior que zero")
	@Schema(description = "Duração da música em segundos", example = "294")
	private Integer durationInSeconds;
	
	@PastOrPresent(message = "deve ser a data atual ou uma data passada")
	@Schema(description = "Data de lançamento da música", example = "1982-01-02")
	private LocalDate releaseDate;
	
	@Pattern(regexp = ".*\\S.*", message = "não pode ser vazio ou conter somente espaços em branco se não for nulo")
	@Schema(description = "Link do vídeo da música", example = "https://www.youtube.com/watch?v=Zi_XLOBDo_Y")	
	private String videoLink;

}
