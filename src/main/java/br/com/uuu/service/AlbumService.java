package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.converter.AlbumConverter;
import br.com.uuu.json.input.album.AlbumCreateInput;
import br.com.uuu.json.input.album.AlbumUpdateInput;
import br.com.uuu.json.output.album.AlbumOutput;
import br.com.uuu.model.mongodb.entity.Album;
import br.com.uuu.model.mongodb.repository.AlbumRepository;

@Service
public class AlbumService {

	@Autowired
	private AlbumRepository albumRepository;

	@Autowired
	private AlbumConverter albumConverter;

	public List<Album> getAll() {
		return albumRepository.findAll();
	}

	public List<AlbumOutput> getAllToOutput() {
		return albumConverter.toOutput(getAll());
	}

	public Album getById(String id) {
		return albumRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Álbum com o ID %s não foi encontrado", id)));
	}

	public AlbumOutput getByIdToOutput(String id) {
		return albumConverter.toOutput(getById(id));
	}

	public Boolean existsById(String id) {
		return albumRepository.existsById(id);
	}

	public Album save(Album album) {
		return albumRepository.save(album);
	}

	public AlbumOutput saveFromInputToOutput(AlbumCreateInput input) {
		var album = new Album();
		return albumConverter.toOutput(save(albumConverter.toEntity(album, input)));
	}

	public AlbumOutput updateFromInputToOutput(String id, AlbumUpdateInput input) {
		var album = getById(id);
		return albumConverter.toOutput(save(albumConverter.toEntity(album, input)));
	}

	public void delete(String id) {
		albumRepository.deleteById(id);
	}

}
