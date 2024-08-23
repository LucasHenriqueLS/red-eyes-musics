package br.com.uuu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.json.input.song.SongCreateInput;
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
		return ResponseEntity.ok(songService.getAll());
	}

//	@GetMapping("get-by-id/{musicId}")
//	@Operation(description = "Recuperar uma música pelo ID")
//	public Song getById(@PathVariable String musicId) {
//		return songService.getById(musicId);
//	}
//
//	@GetMapping("get-by-name/{musicName}")
//	@Operation(description = "Recuperar músicas pelo nome")
//	public List<Song> getByName(@PathVariable String musicName) {
//		return songService.getByName(musicName);
//	}
//
//	@GetMapping("get-by-artist-id/{artistId}")
//	@Operation(description = "Recuperar músicas pelo ID do artista")
//	public List<Song> getByArtistId(@PathVariable String artistId) {
//		return songService.getByArtistId(artistId);
//	}
//
//	@GetMapping("get-by-genre/{musicGenre}")
//	@Operation(description = "Recuperar músicas pelo gênero")
//	public List<Song> getByGenre(@PathVariable Genre musicGenre) {
//		return songService.getByGenre(musicGenre);
//	}

	@PostMapping
	@Operation(description = "Cria uma nova música")
	public ResponseEntity<?> save(@Valid @RequestBody SongCreateInput input) {
		return ResponseEntity.ok(songService.save(input));
	}

//	@PutMapping("{musicId}")
//	@Operation(description = "Atualizar uma música pelo ID")
//	public Song update(@Valid @PathVariable String musicId, @Valid @RequestBody MusicUpdateInput input) {
//		return songService.update(musicId, input);
//	}
//
//	@DeleteMapping("{musicId}")
//	@Operation(description = "Deletar uma música pelo ID")
//	public void delete(@Valid @PathVariable String musicId) {
//		songService.delete(musicId);
//	}
}
