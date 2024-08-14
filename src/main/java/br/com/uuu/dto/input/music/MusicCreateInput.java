package br.com.uuu.dto.input.music;

import java.util.List;
import java.util.Map;

import br.com.uuu.mongodb.util.Genre;
import br.com.uuu.mongodb.util.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MusicCreateInput {

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "ID do artista", example = "64957a557f1d87179e9c77b9")
	private String artistId;

	@NotNull(message = "não pode ser nulo")
	@Schema(description = "Lista de gêneros", example = "[\"J_POP\", \"J_ROCK\"]")
	private List<Genre> genres;

	@NotNull(message = "não pode ser nulo")
	@Schema(description = "Idioma original da música", example = "JAPANESE")
	private Language originalLanguage;

	@NotNull(message = "não pode ser nulo ")
	@Schema(description = "Nome da música para cada idioma mapeado", example = "{\"JAPANESE\" : \"Aizome\"}")
	private Map<Language, String> nameByLanguages;

	@NotNull(message = "não pode ser nulo")
	@Schema(description = "Letra da música para cada idioma mapeado", example = "{\"JAPANESE\" : \"Asaki yumemiji towa ni nageki mo sesu...\\n\\n...Ai ni somete...\"}")
	private Map<Language, String> lyricByLanguages;

	@Schema(description = "Lista de nomes dos compositores", example = "[\"Mamiko Noto\"]")
	private List<String> composersNames;

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "ID do usuário que está enviando a música", example = "64957a557f1d87179e9c77f9")
	private String submitterId;
}
