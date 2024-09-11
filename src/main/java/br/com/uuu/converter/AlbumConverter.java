package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.json.input.album.AlbumCreateInput;
import br.com.uuu.json.input.album.AlbumUpdateInput;
import br.com.uuu.json.output.album.AlbumOutput;
import br.com.uuu.model.mongodb.entity.Album;
import br.com.uuu.service.ArtistService;
import br.com.uuu.service.GenreService;

@Component
public class AlbumConverter {

	@Autowired
	private ArtistService artistService;
	
	@Autowired
	private GenreService genreService;

	public Album toEntity(Album album, AlbumCreateInput input) {
		setArtistsIfIsValid(album, input.getArtistIds());
		setGenresIfIsValid(album, input.getGenreIds());

		album.setTitle(input.getTitle());
		album.setReleaseDate(input.getReleaseDate());
		album.setCoverUrl(input.getCoverUrl());
		album.setRecordCompanyName(input.getRecordCompanyName());

		return album;
	}

	public Album toEntity(Album album, AlbumUpdateInput input) {
		Optional.ofNullable(input.getArtistIds()).ifPresent(artistIds -> setArtistsIfIsValid(album, artistIds));
		Optional.ofNullable(input.getGenreIds()).ifPresent(genreIds -> setGenresIfIsValid(album, genreIds));

		Optional.ofNullable(input.getTitle()).ifPresent(album::setTitle);
		Optional.ofNullable(input.getReleaseDate()).ifPresent(album::setReleaseDate);
		Optional.ofNullable(input.getCoverUrl()).ifPresent(album::setCoverUrl);
		Optional.ofNullable(input.getRecordCompanyName()).ifPresent(album::setRecordCompanyName);

		return album;
	}

	private void setArtistsIfIsValid(Album album, List<String> artistIds) {
		var artistIdsNotFound = artistService.getAllIdsNotFound(artistIds);
		if (artistIdsNotFound.isEmpty()) {
			album.setArtistIds(artistIds);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Artistas com os IDs %s não foram encontrados", artistIdsNotFound));
		}
	}

	private void setGenresIfIsValid(Album album, List<String> genreIds) {
		var genreIdsNotFound = genreService.getAllIdsNotFound(genreIds);
		if (genreIdsNotFound.isEmpty()) {
			album.setGenreIds(genreIds);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Gêneros com os IDs %s não foram encontrados", genreIdsNotFound));
		}
	}

	public List<AlbumOutput> toOutput(List<Album> albums) {
		var outputs = new ArrayList<AlbumOutput>();
		for (var album : albums) {
			outputs.add(toOutput(album));
		}
		return outputs;
	}

	public AlbumOutput toOutput(Album album) {
		return AlbumOutput.builder()
				.id(album.getId())
				.title(album.getTitle())
				.releaseDate(album.getReleaseDate())
				.artistIds(album.getArtistIds())
				.coverUrl(album.getCoverUrl())
				.genreIds(album.getGenreIds())
				.recordCompanyName(album.getRecordCompanyName())
			   .build();
	}

}
