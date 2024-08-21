package br.com.uuu.json.input.song;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DetailsByLanguageCodeCreateInput {

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Título da música para o código do idioma", example = "Aizome")
	private String title;

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Letra da música para o código do idioma", example = "Asaki yumemiji towa ni nageki mo sesu...\\n\\n...Ai ni somete...")
	private String lyric;

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "ID do usuário que está enviando a música", example = "64957a557f1d87179e9c77f9")
	private String submitterId;

}
