package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.converter.AlbumConverter;
import br.com.uuu.json.input.album.AlbumCreateInput;
import br.com.uuu.model.mongodb.entity.Album;
import br.com.uuu.model.mongodb.repository.AlbumRepository;

@Service
public class AlbumService {

	@Autowired
	private AlbumRepository albumRepository;

	@Autowired
	private AlbumConverter albumConverter;

//	@Autowired
//	private ArtistService artistService;

	public List<Album> getAll() {
		return albumRepository.findAll();
	}

//	public Album getById(String albumId) {
//		return albumRepository.findById(albumId).orElseThrow(() -> new NotFoundException(albumId, "Música"));
//	}
//
//	public List<Album> getByArtistId(String artistId) {
//		return albumRepository.findByArtistId(artistId);
//	}
//
//	public List<Album> getByName(String albumName) {
//		return albumRepository.findByName(albumName);
//	}
//
//	public List<Album> getByMusicId(String musicId) {
//		return albumRepository.getByMusicId(musicId);
//	}

	public Album save(AlbumCreateInput input) {
		return albumRepository.save(albumConverter.toEntity(input));
	}

//	public Album update(String albumId, AlbumUpdateInput input) {
//		var album = getById(albumId);
//		return albumRepository.save(albumConverter.toUpdatedEntity(album, input));
//	}
//
//	public void delete(String albumId) {
//		albumRepository.deleteById(albumId);
//	}
	
	public Boolean existsById(String id) {
		return albumRepository.existsById(id);
	}
}
