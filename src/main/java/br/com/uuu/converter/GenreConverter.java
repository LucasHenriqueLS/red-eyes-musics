package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.uuu.json.input.genre.GenreCreateInput;
import br.com.uuu.json.input.genre.GenreUpdateInput;
import br.com.uuu.json.output.genre.GenreOutput;
import br.com.uuu.model.mongodb.entity.Genre;

@Component
public class GenreConverter {

	public Genre toEntity(Genre genre, GenreCreateInput input) {
		genre.setName(input.getName());
		genre.setDescription(input.getDescription());

		return genre;
	}

	public Genre toEntity(Genre genre, GenreUpdateInput input) {
		Optional.ofNullable(input.getName()).ifPresent(genre::setName);
		Optional.ofNullable(input.getDescription()).ifPresent(genre::setDescription);

		return genre;
	}

	public List<GenreOutput> toOutput(List<Genre> genres) {
		var outputs = new ArrayList<GenreOutput>();
		for (var genre : genres) {
			outputs.add(toOutput(genre));
		}
		return outputs;
	}

	public GenreOutput toOutput(Genre genre) {
		return GenreOutput.builder()
				.id(genre.getId())
				.name(genre.getName())
				.description(genre.getDescription())
			   .build();
	}

}
