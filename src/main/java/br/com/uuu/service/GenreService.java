package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.converter.GenreConverter;
import br.com.uuu.json.dto.genre.GenreIdDTO;
import br.com.uuu.json.input.genre.GenreCreateInput;
import br.com.uuu.json.input.genre.GenreUpdateInput;
import br.com.uuu.json.output.genre.GenreOutput;
import br.com.uuu.model.mongodb.entity.Genre;
import br.com.uuu.model.mongodb.repository.GenreRepository;

@Service
public class GenreService {

	@Autowired
	private GenreRepository genreRepository;

	@Autowired
	private GenreConverter genreConverter;

	public List<Genre> getAll() {
		return genreRepository.findAll();
	}

	public List<GenreOutput> getAllToOutput() {
		return genreConverter.toOutput(getAll());
	}

	public Genre getById(String id) {
		return genreRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Gênero com o ID %s não foi encontrado", id)));
	}

	public GenreOutput getByIdToOutput(String id) {
		return genreConverter.toOutput(getById(id));
	}

	public Boolean existsById(String id) {
		return genreRepository.existsById(id);
	}

	public Genre save(Genre genre) {
		return genreRepository.save(genre);
	}

	public GenreOutput saveFromInputToOutput(GenreCreateInput input) {
		var genre = new Genre();
		return genreConverter.toOutput(save(genreConverter.toEntity(genre, input)));
	}

	public GenreOutput updateFromInputToOutput(String id, GenreUpdateInput input) {
		var genre = getById(id);
		return genreConverter.toOutput(save(genreConverter.toEntity(genre, input)));
	}

	public void delete(String id) {
		genreRepository.deleteById(id);
	}

	public List<String> getAllIdsNotFound(List<String> ids) {
		var allIdsFound = genreRepository.findAllByIdIn(ids).stream().map(GenreIdDTO::id).toList();
        return ids.stream().filter(id -> !allIdsFound.contains(id)).toList();
    }

}
