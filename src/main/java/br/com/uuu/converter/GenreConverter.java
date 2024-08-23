package br.com.uuu.converter;

import org.springframework.stereotype.Component;

import br.com.uuu.json.input.genre.GenreCreateInput;
import br.com.uuu.model.mongodb.entity.Genre;

@Component
public class GenreConverter {
	
	public Genre toEntity(GenreCreateInput input) {
		var genre = new Genre();

		genre.setName(input.getName());
		genre.setDescription(input.getDescription());

		return genre;
	}

}
