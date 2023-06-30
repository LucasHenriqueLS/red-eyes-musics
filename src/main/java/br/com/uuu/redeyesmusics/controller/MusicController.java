package br.com.uuu.redeyesmusics.controller;

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

import br.com.uuu.redeyesmusics.dto.input.music.MusicCreateInput;
import br.com.uuu.redeyesmusics.dto.input.music.MusicUpdateInput;
import br.com.uuu.redeyesmusics.nosql.entity.Music;
import br.com.uuu.redeyesmusics.nosql.util.Genre;
import br.com.uuu.redeyesmusics.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "musics")
public class MusicController {

	@Autowired
	private MusicService musicService;
	
	@GetMapping
	@Operation(description = "Recuperar todas as músicas")
	public List<Music> getAll() {
		return musicService.getAll();
	}
	
	@GetMapping("get-by-id/{musicId}")
	@Operation(description = "Recuperar uma música pelo ID")
	public Music getById(@PathVariable("musicId") String musicId) {
		return musicService.getById(musicId);
	}
	
	@GetMapping("get-by-name/{musicName}")
	@Operation(description = "Recuperar músicas pelo nome")
	public List<Music> getByName(@PathVariable("musicName") String musicName) {
		return musicService.getByName(musicName);
	}
	
	@GetMapping("get-by-artist-id/{artistId}")
	@Operation(description = "Recuperar músicas pelo ID do artista")
	public List<Music> getByArtistId(@PathVariable("artistId") String artistId) {
		return musicService.getByArtistId(artistId);
	}
	
	@GetMapping("get-by-genre/{musicGenre}")
	@Operation(description = "Recuperar músicas pelo gênero")
	public List<Music> getByGenre(@PathVariable("musicGenre") Genre musicGenre) {
		return musicService.getByGenre(musicGenre);
	}
	
	@PostMapping
	@Operation(description = "Criar uma nova música")
	public Music save(@Valid @RequestBody MusicCreateInput input) {
		return musicService.save(input);
	}
	
	@PutMapping("{musicId}")
	@Operation(description = "Atualizar uma música pelo ID")
	public Music update(@Valid @PathVariable("musicId") String musicId, @Valid @RequestBody MusicUpdateInput input) {
		return musicService.update(musicId, input);
	}
	
	@DeleteMapping("{musicId}")
	@Operation(description = "Deletar uma música pelo ID")
	public void delete(@Valid @PathVariable("musicId") String musicId) {
		musicService.delete(musicId);
	}
}
