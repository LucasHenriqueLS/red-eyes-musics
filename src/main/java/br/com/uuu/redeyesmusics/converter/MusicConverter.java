package br.com.uuu.redeyesmusics.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.uuu.redeyesmusics.dto.input.MusicInput;
import br.com.uuu.redeyesmusics.nosql.entity.Music;
import br.com.uuu.redeyesmusics.service.ArtistService;

@Component
public class MusicConverter {
	
	@Autowired
	private ArtistService artistService;

	public Music toEntity(MusicInput input) {
		var music = new Music();
		music.setId(String.format("%s_%s", artistService.getById(input.getArtistId()).getName(), input.getName()));
		music.setArtistId(input.getArtistId());
		music.setOriginalLanguage(input.getOriginalLanguage());
		if (input.getComposerId() == null) {
			music.setComposerId("Desconhecido");
		}
		music.setComposerId(input.getComposerId());
		music.setSubmitterId(input.getSubmitterId());
		music.setOriginalLanguage(input.getOriginalLanguage());
		music.getNames().put(input.getOriginalLanguage(), input.getName());
		music.getLyrics().put(input.getOriginalLanguage(), input.getLyric());
		
		return music;
	}
}
