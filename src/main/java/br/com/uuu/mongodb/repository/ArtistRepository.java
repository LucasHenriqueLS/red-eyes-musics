package br.com.uuu.mongodb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.json.dto.artist.ArtistIdDTO;
import br.com.uuu.mongodb.entity.Artist;

public interface ArtistRepository extends MongoRepository<Artist, String> {

	List<ArtistIdDTO> findAllByIdIn(List<String> ids);

}
