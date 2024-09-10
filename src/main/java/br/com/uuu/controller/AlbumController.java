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

import br.com.uuu.json.input.album.AlbumCreateInput;
import br.com.uuu.json.input.album.AlbumUpdateInput;
import br.com.uuu.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("albums")
public class AlbumController {

	@Autowired
	private AlbumService albumService;

	@GetMapping
	@Operation(description = "Recupera todos os álbuns")
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(albumService.getAllToOutput());
	}

	@GetMapping("get-by-id/{id}")
	@Operation(description = "Recupera um álbum pelo ID")
	public ResponseEntity<?> getById(@PathVariable String id) {
		return ResponseEntity.ok(albumService.getByIdToOutput(id));
	}

	@PostMapping
	@Operation(description = "Cria um novo álbum")
	public ResponseEntity<?> save(@Valid @RequestBody AlbumCreateInput input) {
		return ResponseEntity.status(HttpStatus.CREATED).body(albumService.saveFromInputToOutput(input));
	}

	@PutMapping("{id}")
	@Operation(description = "Atualiza um álbum pelo ID")
	public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody AlbumUpdateInput input) {
		return ResponseEntity.ok(albumService.updateFromInputToOutput(id, input));
	}

	@DeleteMapping("{id}")
	@Operation(description = "Deleta um álbum pelo ID")
	public ResponseEntity<?> delete(@PathVariable String id) {
		albumService.delete(id);
		return ResponseEntity.ok().build();
	}

}
