package br.com.uuu.redeyesmusics.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.redeyesmusics.converter.MusicConverter;
import br.com.uuu.redeyesmusics.dto.error.exception.NotFoundException;
import br.com.uuu.redeyesmusics.dto.input.music.MusicCreateInput;
import br.com.uuu.redeyesmusics.dto.input.music.MusicUpdateInput;
import br.com.uuu.redeyesmusics.nosql.entity.Music;
import br.com.uuu.redeyesmusics.nosql.repository.MusicRepository;
import br.com.uuu.redeyesmusics.nosql.util.Genre;

@Service
public class MusicService {

	@Autowired
	private MusicRepository musicRepository;
	
	@Autowired
	private MusicConverter musicConverter;
	
	public List<Music> getAll() {
		return musicRepository.findAll();
	}
	
	public Music getById(String musicId) {
		return musicRepository.findById(musicId).orElseThrow(() -> new NotFoundException(musicId, "Música"));
	}

	public List<Music> getByArtistId(String artistId) {
		return musicRepository.findByArtistId(artistId);
	}
	
	public List<Music> getByName(String musicName) {
		return musicRepository.getByName(musicName);
	}
	
	public List<Music> getByGenre(Genre musicGenre) {
		return musicRepository.getByGenre(musicGenre);
	}
	
	public Music save(MusicCreateInput input) {
		return musicRepository.save(musicConverter.toEntity(input));
	}
	
	public Music update(String musicId, MusicUpdateInput input) {
		var music = getById(musicId);
		return musicRepository.save(musicConverter.toUpdatedEntity(music, input));
	}
	
	public void delete(String musicId) {
		musicRepository.deleteById(musicId);
	}
}
