package br.com.uuu.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.json.input.song.SongCreateInput;
import br.com.uuu.mongodb.entity.Song;
import br.com.uuu.service.AlbumService;
import br.com.uuu.service.ArtistService;
import br.com.uuu.service.GenreService;

@Component
public class SongConverter {

	@Autowired
	private ArtistService artistService;
	
	@Autowired
	private AlbumService albumService;
	
	@Autowired
	private GenreService genreService;
	
	@Autowired
	private DetailsByLanguageConverter detailsByLanguageConverter;
	
	public Song toEntity(SongCreateInput input) {
		var song = new Song();

		var artistIdsNotFound = artistService.getAllIdsNotFound(input.getArtistIds());
		if (artistIdsNotFound.isEmpty()) {
			song.setArtistIds(input.getArtistIds());
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Artistas com os IDs %s não foram encontrados", artistIdsNotFound));
		}

		if (input.getComposerNames() != null && !input.getComposerNames().isEmpty()) {
			song.setComposerNames(input.getComposerNames());			
		} else {
			song.getComposerNames().add("Desconhecido");
		}

		var albumId = input.getAlbumId();
		if (albumId != null) {
			if (albumService.existsById(albumId)) {
				song.setAlbumId(albumId);
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Álbum com o ID %s não foi encontrado", albumId));
			}			
		}
		
		var genreIdsNotFound = genreService.getAllIdsNotFound(input.getGenreIds());
		if (genreIdsNotFound.isEmpty()) {
			song.setGenreIds(input.getGenreIds());
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Gêneros com os IDs %s não foram encontrados", genreIdsNotFound));
		}

		song.setOriginalLanguageId(input.getOriginalLanguageId());
		
		input.getDetailsByLanguageCode().forEach((language, details) ->  {
			song.getDetailsByLanguageCode().put(language, detailsByLanguageConverter.toEntity(details));
		});

		song.setDurationInSeconds(input.getDurationInSeconds());
		song.setReleaseDate(input.getReleaseDate());
		song.setVideoLink(input.getVideoLink());

		return song;
	}
	
//	public Song toUpdatedEntity(Song music, MusicUpdateInput input) {
//
//		if (input.getUpdatedNameByLanguages() != null && !input.getUpdatedNameByLanguages().isEmpty()) {
//			input.getUpdatedNameByLanguages().keySet().forEach(language -> {
//				if (!music.getNameByLanguages().containsKey(language)) {
//					music.getSubmitterIdByLanguages().put(language, input.getProofreaderId());
//				} else {
//					addProofreaderIdByLanguage(music.getProofreadersIdsByLanguages(), input.getProofreaderId(), language);
//				}
//			});
//		}
//
//		if (input.getArtistId() != null) {
//			artistService.removeMusicId(music.getArtistId(), music.getId());
//			artistService.addMusicId(input.getArtistId(), music.getId());
//
//			music.setArtistId(input.getArtistId());
//		}
//		if (input.getGenres() != null && !input.getGenres().isEmpty()) {
//			music.setGenres(input.getGenres());
//		}
//		if (input.getOriginalLanguage() != null) {
//			music.setOriginalLanguage(input.getOriginalLanguage());
//		}
//		if (input.getUpdatedNameByLanguages() != null && !input.getUpdatedNameByLanguages().isEmpty()) {
//			input.getUpdatedNameByLanguages().forEach((language, name) -> {
//				music.getNameByLanguages().put(language, name);				
//			});
//		}
//		if (input.getUpdatedLyricByLanguages() != null && !input.getUpdatedLyricByLanguages().isEmpty()) {
//			input.getUpdatedLyricByLanguages().forEach((language, lyric) -> {
//				music.getLyricByLanguages().put(language, lyric);				
//			});
//		}
//		if (input.getComposersNames() != null && !input.getComposersNames().isEmpty()) {
//			music.setComposersNames(input.getComposersNames());
//		}
//
//		addProofreaderIdByLanguage(music.getProofreadersIdsByLanguages(), input.getProofreaderId(), music.getOriginalLanguage());
//
//		return music;
//	}
//
//	private void addProofreaderIdByLanguage(Map<Language, List<String>> proofreadersIdsByLanguages, String proofreaderId, Language language) {
//		if (!proofreadersIdsByLanguages.containsKey(language)) {
//			proofreadersIdsByLanguages.put(language, new ArrayList<>());
//		}
//		proofreadersIdsByLanguages.get(language).add(proofreaderId);
//	}
}
