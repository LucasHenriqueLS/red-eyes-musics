package br.com.uuu.redeyesmusics.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.uuu.redeyesmusics.dto.error.exception.NotFoundException;
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

		if (artistService.existsById(input.getArtistId())) {
			music.setArtistId(input.getArtistId());
		} else {
			throw new NotFoundException(input.getArtistId(), "Artista");
		}

		music.setGenres(input.getGenres());
		music.setOriginalLanguage(input.getOriginalLanguage());
		music.setNameByLanguages(input.getNameByLanguages());
		music.setLyricByLanguages(input.getLyricByLanguages());
		
		if (input.getComposersNames() != null && !input.getComposersNames().isEmpty()) {
			music.setComposersNames(input.getComposersNames());			
		} else {
			music.getComposersNames().add("Desconhecido");
		}

		music.getNameByLanguages().keySet().forEach(language -> {
			music.getSubmitterIdByLanguages().put(language, input.getSubmitterId());
		});

		return music;
	}
	
	public Music toUpdatedEntity(Music music, MusicUpdateInput input) {

		input.getUpdatedNames().keySet().forEach(language -> {
			if (!music.getNameByLanguages().containsKey(language)) {
				music.getSubmitterIdByLanguages().put(language, input.getProofreaderId());
			} else {
				music.getProofreadersIdsByLanguages().get(language).add(input.getProofreaderId());				
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
				music.getNameByLanguages().put(language, name);				
			});
		}
		if (input.getUpdatedLyrics() != null && !input.getUpdatedLyrics().isEmpty()) {
			input.getUpdatedLyrics().forEach((language, lyric) -> {
				music.getLyricByLanguages().put(language, lyric);				
			});
		}
		if (input.getComposersIds() != null && !input.getComposersIds().isEmpty()) {
			music.setComposersNames(input.getComposersIds());
		}
		
		return music;
	}
}
