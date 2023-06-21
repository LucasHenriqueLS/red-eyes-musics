package br.com.uuu.redeyesmusics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.redeyesmusics.converter.ArtistConverter;
import br.com.uuu.redeyesmusics.dto.input.artist.ArtistInput;
import br.com.uuu.redeyesmusics.nosql.entity.Artist;
import br.com.uuu.redeyesmusics.nosql.repository.ArtistRepository;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;
	
	@Autowired
	private ArtistConverter artistConverter;
	
	public Artist getById(String id) {
		var artist = artistRepository.findById(id);
		if (artist.isEmpty()) {
//			throws Exception("Artista não existe!");
		}
		return artist.get();
	}
	
	public Artist save(ArtistInput input) {
		return artistRepository.save(artistConverter.toEntity(input));
	}
}
