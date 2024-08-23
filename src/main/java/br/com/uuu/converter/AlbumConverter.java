package br.com.uuu.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.model.mongodb.entity.Album;
import br.com.uuu.service.ArtistService;
import br.com.uuu.service.GenreService;

@Component
public class AlbumConverter {

	@Autowired
	private ArtistService artistService;
	
	@Autowired
	private GenreService genreService;

	public Album toEntity(br.com.uuu.json.input.album.AlbumCreateInput input) {
		var album = new Album();

		album.setTitle(input.getTitle());
		album.setReleaseDate(input.getReleaseDate());

		var artistIdsNotFound = artistService.getAllIdsNotFound(input.getArtistIds());
		if (artistIdsNotFound.isEmpty()) {
			album.setArtistIds(input.getArtistIds());
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Artistas com os IDs %s não foram encontrados", artistIdsNotFound));
		}

		album.setCoverUrl(input.getCoverUrl());

		var genreIdsNotFound = genreService.getAllIdsNotFound(input.getGenreIds());
		if (genreIdsNotFound.isEmpty()) {
			album.setGenreIds(input.getGenreIds());
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Gêneros com os IDs %s não foram encontrados", genreIdsNotFound));
		}

		album.setRecordCompanyName(input.getRecordCompanyName());

		return album;
	}

}
