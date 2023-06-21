package br.com.uuu.redeyesmusics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.redeyesmusics.dto.input.MusicInput;
import br.com.uuu.redeyesmusics.nosql.entity.Music;
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
	
	@PostMapping
	public Music save(@Valid @RequestBody MusicInput input) {
		return musicService.save(input);
	}
}
