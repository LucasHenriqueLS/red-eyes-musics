package br.com.uuu.mongodb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import br.com.uuu.mongodb.entity.Song;

public interface SongRepository extends MongoRepository<Song, String> {

	List<Song> findByArtistIdsContaining(String artistId);

	List<Song> findByGenreIdsContaining(String genreId);
	
	@Query("{$where: 'for (var name in this.nameByLanguages) if (this.nameByLanguages[name] === \"?0\") return true; return false;'}")
	List<Song> getByName(String musicName);
}
