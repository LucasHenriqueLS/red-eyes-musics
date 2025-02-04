package br.com.uuu.json.input.user;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UserCreateInput {

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Nome do usuário", example = "user")
	private String username;

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "E-mail do usuário", example = "example@email.com")
	private String email;

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Senha do usuário", example = "12345678")
	private String password;

	@Schema(description = "Url da imagem de perfil", example = "https://example.com/images/user.jpg")
	private String profileImageUrl;

	@Size(min = 1, message = "não pode ser vazio se não for nulo")
	@Schema(description = "IDs dos gêneros favoritos", example = "[\"d87179e9c79647a557f17b95\", \"77b969e7a595f1d87174579c\"]")
	private List<String> favoriteGenreIds;

	@Size(min = 1, message = "não pode ser vazio se não for nulo")
	@Schema(description = "IDs dos artistas seguidos", example = "[\"64957a557f1d87179e9c77b9\", \"a557f1d87179e9c77b964957\"], \"179e9c77b964957a557f1d87\"")
	private List<String> followingArtistIds;

}
