package br.com.uuu.model.mongodb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.json.dto.IdDTO;
import br.com.uuu.model.mongodb.entity.Genre;

public interface GenreRepository extends MongoRepository<Genre, String> {

	List<IdDTO<String>> findAllByIdIn(List<String> ids);

}
