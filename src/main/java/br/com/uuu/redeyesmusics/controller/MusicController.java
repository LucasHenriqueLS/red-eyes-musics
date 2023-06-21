package br.com.uuu.redeyesmusics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "musics")
public class MusicController {

	@Autowired
	private MusicService musicService;
	
	@GetMapping
	public List<Music> getAll() {
		return musicService.getAll();
	}
	
	@GetMapping("get-by-id/{musicId}")
	public Music getById(@PathVariable("musicId") String musicId) {
		return musicService.getById(musicId);
	}
	
	@GetMapping("get-by-name/{musicName}")
	public List<Music> getByName(@PathVariable("musicName") String musicName) {
		return musicService.getByName(musicName);
	}
	
	@GetMapping("get-by-artist-id/{artistId}")
	public List<Music> getByArtistId(@PathVariable("artistId") String artistId) {
		return musicService.getByArtistId(artistId);
	}
	
	@GetMapping("get-by-genre/{musicGenre}")
	public List<Music> getByGenre(@PathVariable("musicGenre") Genre musicGenre) {
		return musicService.getByGenre(musicGenre);
	}
	
	@PostMapping
	public Music save(@Valid @RequestBody MusicCreateInput input) {
		return musicService.save(input);
	}
	
	@PutMapping("{musicId}")
	public Music update(@Valid @PathVariable("musicId") String musicId, @RequestBody MusicUpdateInput input) {
		return musicService.update(musicId, input);
	}
}
