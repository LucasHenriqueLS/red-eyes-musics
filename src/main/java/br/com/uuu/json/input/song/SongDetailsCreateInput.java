package br.com.uuu.json.input.song;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongDetailsCreateInput {

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Título da música para o código do idioma", example = "Billie Jean")
	private String title;

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Letra da música para o código do idioma", example = "Billie Jean is not my lover...")
	private String lyric;

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "ID do usuário que está enviando a música", example = "64957a557f1d87179e9c77f9")
	private String submitterId;

}
