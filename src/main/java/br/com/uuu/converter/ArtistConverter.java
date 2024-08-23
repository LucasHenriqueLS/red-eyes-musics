package br.com.uuu.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.json.input.artist.ArtistCreateInput;
import br.com.uuu.model.mongodb.entity.Artist;
import br.com.uuu.service.GenreService;

@Component
public class ArtistConverter {
	
	@Autowired
	private GenreService genreService;

	public Artist toEntity(ArtistCreateInput input) {
		var artist = new Artist();

		artist.setNames(input.getNames());
		artist.setBio(input.getBio());
		
		var genreIdsNotFound = genreService.getAllIdsNotFound(input.getGenreIds());
		if (genreIdsNotFound.isEmpty()) {
			artist.setGenreIds(input.getGenreIds());
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Gêneros com os IDs %s não foram encontrados", genreIdsNotFound));
		}
		
		artist.setImageUrl(input.getImageUrl());

		return artist;
	}

}
