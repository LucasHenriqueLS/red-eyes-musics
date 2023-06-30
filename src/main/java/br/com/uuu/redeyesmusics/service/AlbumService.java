package br.com.uuu.redeyesmusics.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.redeyesmusics.converter.AlbumConverter;
import br.com.uuu.redeyesmusics.dto.error.exception.NotFoundException;
import br.com.uuu.redeyesmusics.dto.input.album.AlbumCreateInput;
import br.com.uuu.redeyesmusics.dto.input.album.AlbumUpdateInput;
import br.com.uuu.redeyesmusics.nosql.entity.Album;
import br.com.uuu.redeyesmusics.nosql.repository.AlbumRepository;

@Service
public class AlbumService {

	@Autowired
	private AlbumRepository albumRepository;
	
	@Autowired
	private AlbumConverter albumConverter;
	
	@Autowired
	private ArtistService artistService;
	
	public List<Album> getAll() {
		return albumRepository.findAll();
	}
	
	public Album getById(String albumId) {
		return albumRepository.findById(albumId).orElseThrow(() -> new NotFoundException(albumId, "Música"));
	}

	public List<Album> getByArtistId(String artistId) {
		return albumRepository.findByArtistId(artistId);
	}
	
	public List<Album> getByName(String albumName) {
		return albumRepository.findByName(albumName);
	}
	
	public List<Album> getByMusicId(String musicId) {
		return albumRepository.getByMusicId(musicId);
	}
	
	public Album save(AlbumCreateInput input) {
		var album = albumRepository.save(albumConverter.toEntity(input));
		artistService.addAlbumId(input.getArtistId(), album.getId());
		return album;
	}
	
	public Album update(String albumId, AlbumUpdateInput input) {
		var album = getById(albumId);
		return albumRepository.save(albumConverter.toUpdatedEntity(album, input));
	}
	
	public void delete(String albumId) {
		albumRepository.deleteById(albumId);
	}
}
