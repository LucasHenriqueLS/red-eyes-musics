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

import br.com.uuu.json.input.artist.ArtistCreateInput;
import br.com.uuu.json.input.artist.ArtistUpdateInput;
import br.com.uuu.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("artists")
public class ArtistController {

	@Autowired
	private ArtistService artistService;

	@GetMapping
	@Operation(description = "Recupera todos os artistas")
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(artistService.getAllToOutput());
	}

	@GetMapping("get-by-id/{id}")
	@Operation(description = "Recupera um artista pelo ID")
	public ResponseEntity<?> getById(@PathVariable String id) {
		return ResponseEntity.ok(artistService.getByIdToOutput(id));
	}

	@PostMapping
	@Operation(description = "Cria um novo artista")
	public ResponseEntity<?> save(@Valid @RequestBody ArtistCreateInput input) {
		return ResponseEntity.status(HttpStatus.CREATED).body(artistService.saveFromInputToOutput(input));
	}

	@PutMapping("{id}")
	@Operation(description = "Atualiza um artista pelo ID")
	public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody ArtistUpdateInput input) {
		return ResponseEntity.ok(artistService.updateFromInputToOutput(id, input));
	}

	@DeleteMapping("{id}")
	@Operation(description = "Deleta um artista pelo ID")
	public ResponseEntity<?> delete(@PathVariable String id) {
		artistService.delete(id);
		return ResponseEntity.ok().build();
	}

}
