package br.com.uuu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.json.input.genre.GenreCreateInput;
import br.com.uuu.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("genres")
public class GenreController {

	@Autowired
	private GenreService genreService;
	
	@GetMapping
	@Operation(description = "Recupera todos os gêneros")
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(genreService.getAll());
	}

	@PostMapping
	@Operation(description = "Cria um novo gênero")
	public ResponseEntity<?> save(@Valid @RequestBody GenreCreateInput input) {
		return ResponseEntity.ok(genreService.save(input));
	}
}
