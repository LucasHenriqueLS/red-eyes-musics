package br.com.uuu.converter.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.json.dto.IdDTO;
import br.com.uuu.json.dto.Identifiable;
import br.com.uuu.model.mongodb.repository.AlbumRepository;
import br.com.uuu.model.mongodb.repository.ArtistRepository;
import br.com.uuu.model.mongodb.repository.GenreRepository;
import br.com.uuu.model.mongodb.repository.LanguageRepository;
import br.com.uuu.model.mongodb.repository.UserRepository;

@Component
public class GetIdsIfAreValid {

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private GenreRepository genreRepository;
	
	@Autowired
	private AlbumRepository albumRepository;
	
	@Autowired
	private LanguageRepository languageRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private String getIdIfIsValid(String id, Boolean exists, String errorMessage) {		
		if (exists) {
			return id;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(errorMessage, id));
		}
	}

	private List<String> getIdsIfAreValid(List<String> ids, List<IdDTO<String>> allIdsIn, String errorMessage) {		
		var allIdsFound = allIdsIn.stream().map(Identifiable::id).toList();
		var idsNotFound = ids.stream().filter(id -> !allIdsFound.contains(id)).toList();

		if (idsNotFound.isEmpty()) {
			return ids;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(errorMessage, idsNotFound));
		}
	}

	public List<String> getGenreIds(List<String> genreIds) {
		return getIdsIfAreValid(genreIds, genreRepository.findAllByIdIn(genreIds), "Gêneros com os IDs %s não foram encontrados");
	}

	public List<String> getArtistIds(List<String> artistIds) {
		return getIdsIfAreValid(artistIds, artistRepository.findAllByIdIn(artistIds), "Artistas com os IDs %s não foram encontrados");
	}

	public List<String> getUserIds(List<String> userIds) {
		return getIdsIfAreValid(userIds, userRepository.findAllByIdIn(userIds), "Usuários com os IDs %s não foram encontrados");
	}

	public String getAlbumId(String albumId) {
		return getIdIfIsValid(albumId, albumRepository.existsById(albumId), "Álbum com o ID %s não foi encontrado");
	}

	public String getLanguageId(String albumId) {
		return getIdIfIsValid(albumId, languageRepository.existsById(albumId), "Idioma com o ID %s não foi encontrado");
	}

	public String getUserId(String userId) {
		return getIdIfIsValid(userId, userRepository.existsById(userId), "Usuário com o ID %s não foi encontrado");
	}

}
