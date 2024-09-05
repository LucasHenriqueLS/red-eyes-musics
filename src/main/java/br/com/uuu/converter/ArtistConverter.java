package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.json.input.artist.ArtistCreateInput;
import br.com.uuu.json.input.artist.ArtistUpdateInput;
import br.com.uuu.json.output.artist.ArtistOutput;
import br.com.uuu.model.mongodb.entity.Artist;
import br.com.uuu.service.GenreService;

@Component
public class ArtistConverter {
	
	@Autowired
	private GenreService genreService;

	public Artist toEntity(Artist artist, ArtistCreateInput input) {
		artist.setNames(input.getNames());
		artist.setBio(input.getBio());

		setGenresIfIsValid(artist, input.getGenreIds());

		artist.setImageUrl(input.getImageUrl());

		return artist;
	}

	public Artist toEntity(Artist artist, ArtistUpdateInput input) {
		input.getNames().ifPresent(artist::setNames);
		input.getBio().ifPresent(artist::setBio);
		input.getGenreIds().ifPresent(genreIds -> setGenresIfIsValid(artist, genreIds));
		input.getImageUrl().ifPresent(artist::setImageUrl);

		return artist;
	}

	private void setGenresIfIsValid(Artist artist, List<String> genreIds) {
		var genreIdsNotFound = genreService.getAllIdsNotFound(genreIds);
		if (genreIdsNotFound.isEmpty()) {
			artist.setGenreIds(genreIds);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Gêneros com os IDs %s não foram encontrados", genreIdsNotFound));
		}
	}

	public List<ArtistOutput> toOutput(List<Artist> artists) {
		var outputs = new ArrayList<ArtistOutput>();
		for (var artist : artists) {
			outputs.add(toOutput(artist));
		}
		return outputs;
	}

	public ArtistOutput toOutput(Artist artist) {
		return ArtistOutput.builder()
				.id(artist.getId())
				.names(artist.getGenreIds())
				.bio(artist.getBio())
				.genreIds(artist.getGenreIds())
				.imageUrl(artist.getImageUrl())
				.build();
	}

}
