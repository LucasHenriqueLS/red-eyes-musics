package br.com.uuu.redeyesmusics.nosql.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import br.com.uuu.redeyesmusics.nosql.entity.Music;
import br.com.uuu.redeyesmusics.nosql.util.Genre;

public interface MusicRepository extends MongoRepository<Music, String> {

	List<Music> findByArtistId(String artistId);

	@Query("{'genres' : ?0}")
	List<Music> getByGenre(Genre musicGenre);

	@Query("{'nameByLanguages.JAPANESE' : ?0}")
	List<Music> getByName(String musicName);
}
