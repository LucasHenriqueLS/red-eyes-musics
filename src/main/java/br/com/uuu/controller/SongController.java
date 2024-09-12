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

import br.com.uuu.json.input.song.SongCreateInput;
import br.com.uuu.json.input.song.SongUpdateInput;
import br.com.uuu.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("songs")
public class SongController {

	@Autowired
	private SongService songService;

	@GetMapping
	@Operation(description = "Recupera todas as músicas")
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(songService.getAllToOutput());
	}

	@GetMapping("get-by-id/{id}")
	@Operation(description = "Recupera uma música pelo ID")
	public ResponseEntity<?> getById(@PathVariable String id) {
		return ResponseEntity.ok(songService.getByIdToOutput(id));
	}

	@PostMapping
	@Operation(description = "Cria uma nova música")
	public ResponseEntity<?> save(@Valid @RequestBody SongCreateInput input) {
		return ResponseEntity.status(HttpStatus.CREATED).body(songService.saveFromInputToOutput(input));
	}

	@PutMapping("{id}")
	@Operation(description = "Atualiza uma música pelo ID")
	public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody SongUpdateInput input) {
		return ResponseEntity.ok(songService.updateFromInputToOutput(id, input));
	}

	@DeleteMapping("{id}")
	@Operation(description = "Deleta uma música pelo ID")
	public ResponseEntity<?> delete(@PathVariable String id) {
		songService.delete(id);
		return ResponseEntity.ok().build();
	}
}
