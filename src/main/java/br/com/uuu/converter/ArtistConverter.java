package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.uuu.converter.util.GetIdsIfAreValid;
import br.com.uuu.json.input.artist.ArtistCreateInput;
import br.com.uuu.json.input.artist.ArtistUpdateInput;
import br.com.uuu.json.output.artist.ArtistOutput;
import br.com.uuu.model.mongodb.entity.Artist;

@Component
public class ArtistConverter {
	
	@Autowired
	private GetIdsIfAreValid getIdsIfAreValid;

	public Artist toEntity(Artist artist, ArtistCreateInput input) {
		artist.setGenreIds(getIdsIfAreValid.getGenreIds(input.getGenreIds()));

		artist.setNames(input.getNames());
		artist.setBio(input.getBio());
		artist.setImageUrl(input.getImageUrl());

		return artist;
	}

	public Artist toEntity(Artist artist, ArtistUpdateInput input) {
		Optional.ofNullable(input.getGenreIds()).ifPresent(genreIds -> artist.setGenreIds(getIdsIfAreValid.getGenreIds(genreIds)));

		Optional.ofNullable(input.getNames()).ifPresent(artist::setNames);
		Optional.ofNullable(input.getBio()).ifPresent(artist::setBio);
		Optional.ofNullable(input.getImageUrl()).ifPresent(artist::setImageUrl);

		return artist;
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
				.names(artist.getNames())
				.bio(artist.getBio())
				.genreIds(artist.getGenreIds())
				.imageUrl(artist.getImageUrl())
			   .build();
	}

}
