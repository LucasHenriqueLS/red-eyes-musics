package br.com.uuu.json.input.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserCreateInput {

	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "E-mail do usuário", example = "example@email.com")
	private String email;
	
	@NotBlank(message = "não pode ser nulo ou vazio")
	@Schema(description = "Senha do usuário", example = "12345678")
	private String password;

}
