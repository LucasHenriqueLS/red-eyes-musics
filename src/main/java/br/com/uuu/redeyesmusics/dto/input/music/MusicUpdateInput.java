package br.com.uuu.redeyesmusics.dto.input.music;

import java.util.List;
import java.util.Map;

import br.com.uuu.redeyesmusics.nosql.util.Genre;
import br.com.uuu.redeyesmusics.nosql.util.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MusicUpdateInput {
	
	@Schema(description = "ID do artista", example = "64957a557f1d87179e9c77b9")
	private String artistId;

	@Schema(description = "Lista de gêneros", example = "[J_POP, J_ROCK]")
	private List<Genre> genres;

	@Schema(description = "Idioma original da música", example = "JAPANESE")
	private Language originalLanguage;

	@Schema(description = "Nome da música para cada idioma mapeado", example = "{\"BRAZILIAN_PORTUGUESE\": \"Tingimento Índigo\",\"ENGLISH\": \"Indigo Dye\"}")
	private Map<Language, String> updatedNames;

	@Schema(description = "Letra da música para cada idioma mapeado", example = "{\"BRAZILIAN_PORTUGUESE\": \"Por toda eternidade, eu vi sonhos vazios, sem nunca reclamar...\n\n...Tingido de anil...\",\"ENGLISH\": \"Unable to even have a shallow dream or grieve eternally...\n\n...Dyed in indigo...\"}")
	private Map<Language, String> updatedLyrics;
	
	@Schema(description = "Lista de nomes dos compositores", example = "[Mamiko Noto]")
	private List<String> composersNames;
	
	@NotBlank(message = "Informe o ID do usuário que está enviando uma revisão para a música")
	@Schema(description = "ID do usuário que está enviando uma revisão para a música", example = "64957a557f1d87179e9c77f9")
	private String proofreaderId;
}
