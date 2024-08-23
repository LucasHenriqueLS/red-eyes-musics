package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.converter.SongConverter;
import br.com.uuu.json.input.song.SongCreateInput;
import br.com.uuu.model.mongodb.entity.Song;
import br.com.uuu.model.mongodb.repository.SongRepository;

@Service
public class SongService {

	@Autowired
	private SongRepository songRepository;

	@Autowired
	private SongConverter songConverter;

//	@Autowired
//	private ArtistService artistService;

	public List<Song> getAll() {
		return songRepository.findAll();
	}
//
//	public Song getById(String musicId) {
//		return songRepository.findById(musicId).orElseThrow(() -> new NotFoundException(musicId, "Música"));
//	}
//
//	public List<Song> getByArtistId(String artistId) {
//		return songRepository.findByArtistId(artistId);
//	}
//
//	public List<Song> getByName(String musicName) {
//		return songRepository.getByName(musicName);
//	}
//
//	public List<Song> getByGenre(Genre genre) {
//		return songRepository.findByGenre(genre);
//	}

	public Song save(SongCreateInput input) {
		return songRepository.save(songConverter.toEntity(input));
	}

//	public Song update(String musicId, MusicUpdateInput input) {
//		var music = getById(musicId);
//		return songRepository.save(songConverter.toUpdatedEntity(music, input));
//	}
//
//	public void delete(String musicId) {
//		songRepository.deleteById(musicId);
//	}
}
