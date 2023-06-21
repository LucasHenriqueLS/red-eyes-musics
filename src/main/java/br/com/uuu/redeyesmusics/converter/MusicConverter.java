package br.com.uuu.redeyesmusics.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.uuu.redeyesmusics.dto.input.music.MusicCreateInput;
import br.com.uuu.redeyesmusics.dto.input.music.MusicUpdateInput;
import br.com.uuu.redeyesmusics.nosql.entity.Music;
import br.com.uuu.redeyesmusics.service.ArtistService;

@Component
public class MusicConverter {
	
	@Autowired
	private ArtistService artistService;

	public Music toEntity(MusicCreateInput input) {
		var music = new Music();
		music.setId(String.format("%s_%s", artistService.getById(input.getArtistId()).getName(), input.getNames().get(input.getOriginalLanguage())));
		music.setArtistId(input.getArtistId());
		music.setGenres(input.getGenres());
		music.setOriginalLanguage(input.getOriginalLanguage());
		music.setNames(input.getNames());
		music.setLyrics(input.getLyrics());
		
		if (input.getComposersIds() != null && !input.getComposersIds().isEmpty()) {
			music.setComposersIds(input.getComposersIds());			
		} else {
			music.getComposersIds().add("Desconhecido");
		}
		music.getNames().keySet().forEach(language -> {
			music.getSubmittersIds().put(language, input.getSubmitterId());
		});
		return music;
	}
	
	public Music toUpdatedEntity(Music music, MusicUpdateInput input) {

		input.getUpdatedNames().keySet().forEach(language -> {
			if (!music.getNames().containsKey(language)) {
				music.getSubmittersIds().put(language, input.getProofreaderId());
			} else {
				music.getProofreadersIds().get(language).add(input.getProofreaderId());				
			}
		});

		if (input.getGenres() != null && !input.getGenres().isEmpty()) {
			music.setGenres(input.getGenres());
		}
		if (input.getOriginalLanguage() != null) {
			music.setOriginalLanguage(input.getOriginalLanguage());
		}
		if (input.getUpdatedNames() != null && !input.getUpdatedNames().isEmpty()) {
			input.getUpdatedNames().forEach((language, name) -> {
				music.getNames().put(language, name);				
			});
		}
		if (input.getUpdatedLyrics() != null && !input.getUpdatedLyrics().isEmpty()) {
			input.getUpdatedLyrics().forEach((language, lyric) -> {
				music.getLyrics().put(language, lyric);				
			});
		}
		if (input.getComposersIds() != null && !input.getComposersIds().isEmpty()) {
			music.setComposersIds(input.getComposersIds());
		}
		
		return music;
	}
}
