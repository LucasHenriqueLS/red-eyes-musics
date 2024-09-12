package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.converter.SongConverter;
import br.com.uuu.json.input.song.SongCreateInput;
import br.com.uuu.json.input.song.SongUpdateInput;
import br.com.uuu.json.output.song.SongOutput;
import br.com.uuu.model.mongodb.entity.Song;
import br.com.uuu.model.mongodb.repository.SongRepository;

@Service
public class SongService {

	@Autowired
	private SongRepository songRepository;

	@Autowired
	private SongConverter songConverter;

	public List<Song> getAll() {
		return songRepository.findAll();
	}

	public List<SongOutput> getAllToOutput() {
		return songConverter.toOutput(getAll());
	}

	public Song getById(String id) {
		return songRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Música com o ID %s não foi encontrado", id)));
	}

	public SongOutput getByIdToOutput(String id) {
		return songConverter.toOutput(getById(id));
	}

	public Boolean existsById(String id) {
		return songRepository.existsById(id);
	}

	public Song save(Song song) {
		return songRepository.save(song);
	}

	public SongOutput saveFromInputToOutput(SongCreateInput input) {
		var song = new Song();
		return songConverter.toOutput(save(songConverter.toEntity(song, input)));
	}

	public SongOutput updateFromInputToOutput(String id, SongUpdateInput input) {
		var song = getById(id);
		return songConverter.toOutput(save(songConverter.toEntity(song, input)));
	}

	public void delete(String id) {
		songRepository.deleteById(id);
	}

}
