package br.com.uuu.redeyesmusics.nosql.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.redeyesmusics.nosql.entity.Music;
import br.com.uuu.redeyesmusics.nosql.util.Genre;

public interface MusicRepository extends MongoRepository<Music, String> {

	List<Music> getByArtistId(String artistId);

	List<Music> getByGenre(Genre musicGenre);

	List<Music> getByName(String musicName);
}
