package br.com.uuu.redeyesmusics.converter;

import org.springframework.stereotype.Component;

import br.com.uuu.redeyesmusics.dto.input.ArtistInput;
import br.com.uuu.redeyesmusics.nosql.entity.Artist;

@Component
public class ArtistConverter {

	public Artist toEntity(ArtistInput input) {
		var artist = new Artist();
		artist.setName(input.getName());
		artist.setOtherNames(input.getOtherNames());
		
		return artist;
	}
}
