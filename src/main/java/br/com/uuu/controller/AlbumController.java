package br.com.uuu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.dto.input.music.MusicCreateInput;
import br.com.uuu.dto.input.music.MusicUpdateInput;
import br.com.uuu.mongodb.entity.Music;
import br.com.uuu.mongodb.util.Genre;
import br.com.uuu.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("albums")
public class AlbumController {

	@Autowired
	private MusicService musicService;

	@GetMapping
	@Operation(description = "Recuperar todas as músicas")
	public List<Music> getAll() {
		return musicService.getAll();
	}

	@GetMapping("get-by-id/{musicId}")
	@Operation(description = "Recuperar uma música pelo ID")
	public Music getById(@PathVariable String musicId) {
		return musicService.getById(musicId);
	}

	@GetMapping("get-by-name/{musicName}")
	@Operation(description = "Recuperar músicas pelo nome")
	public List<Music> getByName(@PathVariable String musicName) {
		return musicService.getByName(musicName);
	}

	@GetMapping("get-by-artist-id/{artistId}")
	@Operation(description = "Recuperar músicas pelo ID do artista")
	public List<Music> getByArtistId(@PathVariable String artistId) {
		return musicService.getByArtistId(artistId);
	}

	@GetMapping("get-by-genre/{musicGenre}")
	@Operation(description = "Recuperar músicas pelo gênero")
	public List<Music> getByGenre(@PathVariable Genre musicGenre) {
		return musicService.getByGenre(musicGenre);
	}

	@PostMapping
	@Operation(description = "Criar uma nova música")
	public Music save(@Valid @RequestBody MusicCreateInput input) {
		return musicService.save(input);
	}

	@PutMapping("{musicId}")
	@Operation(description = "Atualizar uma música pelo ID")
	public Music update(@Valid @PathVariable String musicId, @Valid @RequestBody MusicUpdateInput input) {
		return musicService.update(musicId, input);
	}

	@DeleteMapping("{musicId}")
	@Operation(description = "Deletar uma música pelo ID")
	public void delete(@Valid @PathVariable String musicId) {
		musicService.delete(musicId);
	}
}
