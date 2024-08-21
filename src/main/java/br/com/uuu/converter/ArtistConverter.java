package br.com.uuu.converter;

import org.springframework.stereotype.Component;

import br.com.uuu.dto.input.artist.ArtistInput;
import br.com.uuu.mongodb.entity.Artist;

@Component
public class ArtistConverter {

	public Artist toEntity(ArtistInput input) {
		var artist = new Artist();

		artist.setNames(input.getNames());

		return artist;
	}
}
