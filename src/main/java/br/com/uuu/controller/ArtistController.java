package br.com.uuu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.dto.input.artist.ArtistInput;
import br.com.uuu.mongodb.entity.Artist;
import br.com.uuu.service.ArtistService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("artists")
public class ArtistController {

	@Autowired
	private ArtistService artistService;

	@GetMapping
	public List<Artist> getAll() {
		return artistService.getAll();
	}

	@GetMapping("get-by-id/{artistId}")
	public Artist getById(@PathVariable String artistId) {
		return artistService.getById(artistId);
	}

	@PostMapping
	public Artist save(@Valid @RequestBody ArtistInput input) {
		return artistService.save(input);
	}
}
