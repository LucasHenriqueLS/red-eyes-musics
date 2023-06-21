package br.com.uuu.redeyesmusics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.redeyesmusics.dto.input.MusicInput;
import br.com.uuu.redeyesmusics.nosql.entity.Music;
import br.com.uuu.redeyesmusics.service.MusicService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "music")
public class MusicController {

	@Autowired
	private MusicService musicService;
	
	@PostMapping
	public Music save(@Valid @RequestBody MusicInput input) {
		return musicService.save(input);
	}
}
