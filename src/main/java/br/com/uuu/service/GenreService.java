package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.json.dto.genre.GenreIdDTO;
import br.com.uuu.mongodb.repository.GenreRepository;

@Service
public class GenreService {

	@Autowired
	private GenreRepository genreRepository;
	
	public List<String> getAllIdsNotIn(List<String> ids) {
        return genreRepository.findAllByIdIn(ids).stream().map(GenreIdDTO::getId).toList();
    }

}
