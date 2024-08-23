package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.converter.ArtistConverter;
import br.com.uuu.json.dto.artist.ArtistIdDTO;
import br.com.uuu.json.input.artist.ArtistCreateInput;
import br.com.uuu.model.mongodb.entity.Artist;
import br.com.uuu.model.mongodb.repository.ArtistRepository;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private ArtistConverter artistConverter;

//	@Autowired
//	private MongoTemplate mongoTemplate;

	public List<Artist> getAll() {
		return artistRepository.findAll();
	}

//	public Artist getById(String artistId) {
//		return artistRepository.findById(artistId).orElseThrow(() -> new NotFoundException(artistId, "Artista"));
//	}

	public Artist save(ArtistCreateInput input) {
		return artistRepository.save(artistConverter.toEntity(input));
	}

//	public Boolean existsById(String artistId) {
//		return artistRepository.existsById(artistId);
//	}
//
//	public void addMusicId(String artistId, String musicId) {
//		var update = new Update().addToSet("musicsIds", musicId);
//		mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(artistId)), update, Artist.class);
//	}
//
//	public void removeMusicId(String artistId, String musicId) {
//		var update = new Update().pull("musicsIds", musicId);
//		mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(artistId)), update, Artist.class);
//	}
//
//	public void addAlbumId(String artistId, String albumId) {
//		var update = new Update().addToSet("albumsIds", albumId);
//		mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(artistId)), update, Artist.class);
//	}
//
//	public void removeAlbumId(String artistId, String albumId) {
//		var update = new Update().pull("albumsIds", albumId);
//		mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(artistId)), update, Artist.class);
//	}
	
	public List<String> getAllIdsNotFound(List<String> ids) {
		var allIdsFound = artistRepository.findAllByIdIn(ids).stream().map(ArtistIdDTO::id).toList();
        return ids.stream().filter(id -> !allIdsFound.contains(id)).toList();
    }
}
