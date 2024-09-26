package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.json.input.song.SongCreateInput;
import br.com.uuu.json.input.song.SongUpdateInput;
import br.com.uuu.json.output.song.SongOutput;
import br.com.uuu.model.mongodb.entity.Song;
import br.com.uuu.model.mongodb.util.SongDetails;
import br.com.uuu.service.AlbumService;
import br.com.uuu.service.ArtistService;
import br.com.uuu.service.GenreService;
import br.com.uuu.service.LanguageService;

@Component
public class SongConverter {

	@Autowired
	private ArtistService artistService;
	
	@Autowired
	private AlbumService albumService;
	
	@Autowired
	private GenreService genreService;
	
	@Autowired
	private LanguageService languageService;
	
	@Autowired
	private SongDetailsConverter songDetailsConverter;
	
	public Song toEntity(Song song, SongCreateInput input) {
		setArtistsIfIsValid(song, input.getArtistIds());
		setComposerNamesIfIsValid(song, input.getComposerNames());
		setAlbumIfIsValid(song, input.getAlbumId());
		setGenresIfIsValid(song, input.getGenreIds());
		setOriginalLanguageIfIsValid(song, input.getOriginalLanguageId());

		input.getDetailsByLanguageId().forEach((language, songDetailsInput) ->  {
			song.getDetailsByLanguageId().put(language, songDetailsConverter.toEntity(new SongDetails(), songDetailsInput));
		});

		song.setDurationInSeconds(input.getDurationInSeconds());
		song.setReleaseDate(input.getReleaseDate());
		song.setVideoUrl(input.getVideoUrl());

		return song;
	}

	public Song toEntity(Song song, SongUpdateInput input) {
		Optional.ofNullable(input.getArtistIds()).ifPresent(artistIds -> setArtistsIfIsValid(song, artistIds));
		Optional.ofNullable(input.getComposerNames()).ifPresent(composerNames -> setComposerNamesIfIsValid(song, composerNames));
		Optional.ofNullable(input.getAlbumId()).ifPresent(albumId -> setAlbumIfIsValid(song, albumId));
		Optional.ofNullable(input.getGenreIds()).ifPresent(genreIds -> setGenresIfIsValid(song, genreIds));
		Optional.ofNullable(input.getOriginalLanguageId()).ifPresent(originalLanguageId -> setOriginalLanguageIfIsValid(song, originalLanguageId));

		Optional.ofNullable(input.getDetailsByLanguageId()).ifPresent(detailsByLanguageId ->
			detailsByLanguageId.forEach((language, songDetailsInput) ->  {
				song.getDetailsByLanguageId().put(language, songDetailsConverter.toEntity(new SongDetails(), songDetailsInput));
			})
		);

		Optional.ofNullable(input.getDurationInSeconds()).ifPresent(song::setDurationInSeconds);
		Optional.ofNullable(input.getReleaseDate()).ifPresent(song::setReleaseDate);
		Optional.ofNullable(input.getVideoLink()).ifPresent(song::setVideoUrl);

		return song;
	}

	private void setArtistsIfIsValid(Song song, List<String> artistIds) {
		var artistIdsNotFound = artistService.getAllIdsNotFound(artistIds);
		if (artistIdsNotFound.isEmpty()) {
			song.setArtistIds(artistIds);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Artistas com os IDs %s não foram encontrados", artistIdsNotFound));
		}
	}

	private void setComposerNamesIfIsValid(Song song, List<String> composerNames) {
		if (composerNames != null && !composerNames.isEmpty()) {
			song.setComposerNames(composerNames);			
		} else {
			song.getComposerNames().add("Desconhecido");
		}
	}

	private void setAlbumIfIsValid(Song song, String albumId) {
		if (albumId != null) {
			if (albumService.existsById(albumId)) {
				song.setAlbumId(albumId);
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Álbum com o ID %s não foi encontrado", albumId));
			}		
		}
	}

	private void setGenresIfIsValid(Song song, List<String> genreIds) {
		var genreIdsNotFound = genreService.getAllIdsNotFound(genreIds);
		if (genreIdsNotFound.isEmpty()) {
			song.setGenreIds(genreIds);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Gêneros com os IDs %s não foram encontrados", genreIdsNotFound));
		}
	}

	private void setOriginalLanguageIfIsValid(Song song, String originalLanguageId) {
		if (languageService.existsById(originalLanguageId)) {
			song.setOriginalLanguageId(originalLanguageId);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Idioma com o ID %s não foi encontrado", originalLanguageId));
		}
	}

	public List<SongOutput> toOutput(List<Song> songs) {
		var outputs = new ArrayList<SongOutput>();
		for (var song : songs) {
			outputs.add(toOutput(song));
		}
		return outputs;
	}

	public SongOutput toOutput(Song song) {
		return SongOutput.builder()
				.id(song.getId())
				.artistIds(song.getArtistIds())
				.composerNames(song.getComposerNames())
				.albumId(song.getAlbumId())
				.genreIds(song.getGenreIds())
				.originalLanguageId(song.getOriginalLanguageId())
				.detailsByLanguageId(
					song.getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						Map.Entry::getKey, entry -> songDetailsConverter.toOutput(entry.getValue())
					))
				)
				.durationInSeconds(song.getDurationInSeconds())
				.releaseDate(song.getReleaseDate())
				.videoUrl(song.getVideoUrl())
			   .build();
	}

}
