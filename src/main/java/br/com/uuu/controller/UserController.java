package br.com.uuu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.json.input.user.UserCreateInput;
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
		return ResponseEntity.ok(userService.getAll());
	}

	@PostMapping
	@Operation(description = "Cria um novo usuário")
	public ResponseEntity<?> save(@Valid @RequestBody UserCreateInput input) {
		return ResponseEntity.ok(userService.save(input));
	}

}
