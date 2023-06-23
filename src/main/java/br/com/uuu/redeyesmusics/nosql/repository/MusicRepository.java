package br.com.uuu.redeyesmusics.nosql.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import br.com.uuu.redeyesmusics.nosql.entity.Music;
import br.com.uuu.redeyesmusics.nosql.util.Genre;

public interface MusicRepository extends MongoRepository<Music, String> {

	List<Music> findByArtistId(String artistId);

	List<Music> findByGenres(Genre musicGenre);
	
	@Query("{$where: 'for (var name in this.nameByLanguages) { if (this.nameByLanguages[name] === \"?0\") { return true; } } return false;'}")
//	@Query("{$where: 'for (var name in this.nameByLanguages) { this.nameByLanguages[name] === \"?0\" ? true : false'}")
	List<Music> getByName(String musicName);
}
