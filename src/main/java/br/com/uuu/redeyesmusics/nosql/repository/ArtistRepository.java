package br.com.uuu.redeyesmusics.nosql.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.redeyesmusics.nosql.entity.Artist;

public interface ArtistRepository extends MongoRepository<Artist, String> {
	
	boolean existsById(String artistId);
}
