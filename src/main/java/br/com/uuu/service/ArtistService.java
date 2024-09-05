package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.converter.ArtistConverter;
import br.com.uuu.json.dto.artist.ArtistIdDTO;
import br.com.uuu.json.input.artist.ArtistCreateInput;
import br.com.uuu.json.input.artist.ArtistUpdateInput;
import br.com.uuu.json.output.artist.ArtistOutput;
import br.com.uuu.model.mongodb.entity.Artist;
import br.com.uuu.model.mongodb.repository.ArtistRepository;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private ArtistConverter artistConverter;

	public List<Artist> getAll() {
		return artistRepository.findAll();
	}
	
	public List<ArtistOutput> getAllToOutput() {
		return artistConverter.toOutput(getAll());
	}

	public Artist getById(String id) {
		return artistRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Artista com o ID %s não foi encontrado", id)));
	}

	public ArtistOutput getByIdToOutput(String id) {
		return artistConverter.toOutput(getById(id));
	}

	public List<String> getAllIdsNotFound(List<String> ids) {
		var allIdsFound = artistRepository.findAllByIdIn(ids).stream().map(ArtistIdDTO::id).toList();
        return ids.stream().filter(id -> !allIdsFound.contains(id)).toList();
    }

	public Artist save(Artist artist) {
		return artistRepository.save(artist);
	}

	public ArtistOutput saveFromInputToOutput(ArtistCreateInput input) {
		var artist = new Artist();
		return artistConverter.toOutput(save(artistConverter.toEntity(artist, input)));
	}

	public ArtistOutput updateFromInputToOutput(String id, ArtistUpdateInput input) {
		var artist = getById(id);
		return artistConverter.toOutput(save(artistConverter.toEntity(artist, input)));
	}

	public void delete(String id) {
		artistRepository.deleteById(id);
	}

}
