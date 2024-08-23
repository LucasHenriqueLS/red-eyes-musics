package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.converter.GenreConverter;
import br.com.uuu.json.dto.genre.GenreIdDTO;
import br.com.uuu.json.input.genre.GenreCreateInput;
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

	public Genre save(GenreCreateInput input) {
		return genreRepository.save(genreConverter.toEntity(input));
	}
	
	public List<String> getAllIdsNotFound(List<String> ids) {
		var allIdsFound = genreRepository.findAllByIdIn(ids).stream().map(GenreIdDTO::id).toList();
        return ids.stream().filter(id -> !allIdsFound.contains(id)).toList();
    }

}
