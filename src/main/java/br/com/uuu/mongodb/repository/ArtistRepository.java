package br.com.uuu.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.mongodb.entity.Artist;

public interface ArtistRepository extends MongoRepository<Artist, String> {
	
	boolean existsById(String artistId);
}
