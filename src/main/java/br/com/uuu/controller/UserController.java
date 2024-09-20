package br.com.uuu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.json.input.user.UserCreateInput;
import br.com.uuu.json.input.user.UserUpdateInput;
import br.com.uuu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping
	@Operation(description = "Recupera todos os usuários")
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(userService.getAllToOutput());
	}

	@GetMapping("get-by-id/{id}")
	@Operation(description = "Recupera um usuário pelo ID")
	public ResponseEntity<?> getById(@PathVariable String id) {
		return ResponseEntity.ok(userService.getByIdToOutput(id));
	}

	@PostMapping
	@Operation(description = "Cria um novo usuário")
	public ResponseEntity<?> save(@Valid @RequestBody UserCreateInput input) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveFromInputToOutput(input));
	}

	@PutMapping("{id}")
	@Operation(description = "Atualiza um novo usuário pelo ID")
	public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody UserUpdateInput input) {
		return ResponseEntity.ok(userService.updateFromInputToOutput(id, input));
	}

	@DeleteMapping("{id}")
	@Operation(description = "Deleta um usuário pelo ID")
	public ResponseEntity<?> delete(@PathVariable String id) {
		userService.delete(id);
		return ResponseEntity.ok().build();
	}

}
