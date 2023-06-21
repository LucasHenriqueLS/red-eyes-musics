package br.com.uuu.redeyesmusics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.redeyesmusics.dto.input.artist.ArtistInput;
import br.com.uuu.redeyesmusics.nosql.entity.Artist;
import br.com.uuu.redeyesmusics.service.ArtistService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("artist")
public class ArtistController {

	@Autowired
	private ArtistService artistService;
	
	@PostMapping
	public Artist save(@Valid @RequestBody ArtistInput input) {
		return artistService.save(input);
	}
}
