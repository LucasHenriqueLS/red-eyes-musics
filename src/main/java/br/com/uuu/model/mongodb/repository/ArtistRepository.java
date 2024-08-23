package br.com.uuu.model.mongodb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.json.dto.artist.ArtistIdDTO;
import br.com.uuu.model.mongodb.entity.Artist;

public interface ArtistRepository extends MongoRepository<Artist, String> {

	List<ArtistIdDTO> findAllByIdIn(List<String> ids);

}
