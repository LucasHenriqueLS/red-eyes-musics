package br.com.uuu.model.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.model.mongodb.entity.Song;

public interface SongRepository extends MongoRepository<Song, String> {

//	List<Song> findByArtistIdsContaining(String artistId);
//
//	List<Song> findByGenreIdsContaining(String genreId);
//	
//	@Query("{$where: 'for (var name in this.nameByLanguages) if (this.nameByLanguages[name] === \"?0\") return true; return false;'}")
//	List<Song> getByName(String musicName);
}
