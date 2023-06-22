package br.com.uuu.redeyesmusics.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.redeyesmusics.converter.ArtistConverter;
import br.com.uuu.redeyesmusics.dto.error.exception.NotFoundException;
import br.com.uuu.redeyesmusics.dto.input.artist.ArtistInput;
import br.com.uuu.redeyesmusics.nosql.entity.Artist;
import br.com.uuu.redeyesmusics.nosql.repository.ArtistRepository;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;
	
	@Autowired
	private ArtistConverter artistConverter;
	
	public List<Artist> getAll() {
		return artistRepository.findAll();
	}
	
	public Artist getById(String artistId) {
		return artistRepository.findById(artistId).orElseThrow(() -> new NotFoundException(artistId, "Artista"));
	}
	
	public Artist save(ArtistInput input) {
		return artistRepository.save(artistConverter.toEntity(input));
	}
	
	public Boolean existsById(String artistId) {
		return artistRepository.existsById(artistId);
	}
}
