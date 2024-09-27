package br.com.uuu.model.mongodb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.json.dto.IdDTO;
import br.com.uuu.model.mongodb.entity.Artist;

public interface ArtistRepository extends MongoRepository<Artist, String> {

	List<IdDTO<String>> findAllByIdIn(List<String> ids);

}
