package br.com.uuu.redeyesmusics.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
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
	
	public void addMusicId(String artistId, String musicId) {
		var update = new Update().addToSet("musicsIds", musicId);
		mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(artistId)), update, Artist.class);
	}
	
	public void removeMusicId(String artistId, String musicId) {
		var update = new Update().pull("musicsIds", musicId);
		mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(artistId)), update, Artist.class);
	}
	
	public void addAlbumId(String artistId, String albumId) {
		var update = new Update().addToSet("albumsIds", albumId);
		mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(artistId)), update, Artist.class);
	}
	
	public void removeAlbumId(String artistId, String albumId) {
		var update = new Update().pull("albumsIds", albumId);
		mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(artistId)), update, Artist.class);
	}
}
