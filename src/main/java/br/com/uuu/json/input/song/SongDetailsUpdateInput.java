package br.com.uuu.json.input.song;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class SongDetailsUpdateInput {

	@Pattern(regexp = ".*\\S.*", message = "não pode ser vazio ou conter somente espaços em branco se não for nulo")
	@Schema(description = "Título da música para o código do idioma", example = "Billie Jean")
	private String title;

	@Pattern(regexp = ".*\\S.*", message = "não pode ser vazio ou conter somente espaços em branco se não for nulo")
	@Schema(description = "Letra da música para o código do idioma", example = "Billie Jean is not my lover...")
	private String lyric;

	@Pattern(regexp = ".*\\S.*", message = "não pode ser vazio ou conter somente espaços em branco se não for nulo")
	@Schema(description = "ID do usuário que está enviando a música", example = "64957a557f1d87179e9c77f9")
	private String submitterId;

	@Pattern(regexp = ".*\\S.*", message = "não pode ser vazio ou conter somente espaços em branco se não for nulo")
	@Schema(description = "ID do usuário que está revisando a música", example = "57f1d87179e9c77f964957a5")
	private List<String> proofreaderIds;

}
