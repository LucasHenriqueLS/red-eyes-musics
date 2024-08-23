package br.com.uuu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.json.input.artist.ArtistCreateInput;
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
		return ResponseEntity.ok(artistService.getAll());
	}

//	@GetMapping("get-by-id/{artistId}")
//	public Artist getById(@PathVariable String artistId) {
//		return artistService.getById(artistId);
//	}

	@PostMapping
	@Operation(description = "Cria um novo artista")
	public ResponseEntity<?> save(@Valid @RequestBody ArtistCreateInput input) {
		return ResponseEntity.ok(artistService.save(input));
	}
}
