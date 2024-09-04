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

import br.com.uuu.json.input.genre.GenreCreateInput;
import br.com.uuu.json.input.genre.GenreUpdateInput;
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
		return ResponseEntity.ok(genreService.getAllToOutput());
	}

	@GetMapping("get-by-id/{id}")
	@Operation(description = "Recupera um gênero pelo ID")
	public ResponseEntity<?> getById(@PathVariable String id) {
		return ResponseEntity.ok(genreService.getByIdToOutput(id));
	}

	@PostMapping
	@Operation(description = "Cria um novo gênero")
	public ResponseEntity<?> save(@Valid @RequestBody GenreCreateInput input) {
		return ResponseEntity.status(HttpStatus.CREATED).body(genreService.saveFromInputToOutput(input));
	}

	@PutMapping("{id}")
	@Operation(description = "Atualiza um gênero pelo ID")
	public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody GenreUpdateInput input) {
		return ResponseEntity.ok(genreService.updateFromInputToOutput(id, input));
	}
	
	@DeleteMapping("{id}")
	@Operation(description = "Deleta um gênero pelo ID")
	public ResponseEntity<?> delete(@PathVariable String id) {
		genreService.delete(id);
		return ResponseEntity.ok().build();
	}
}
