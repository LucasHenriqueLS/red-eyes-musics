package br.com.uuu.model.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.model.mongodb.entity.Album;

public interface AlbumRepository extends MongoRepository<Album, String> {

//	List<Album> findByArtistId(String artistId);
//
//	List<Album> findByName(String name);
//	
//	@Query("{$where: 'for (var diskName in this.musicsIdsByDiskNames) for (var musicId in this.musicsIdsByDiskNames[diskName]) if (musicId === \"?0\") return true; return false;'}")
//	List<Album> getByMusicId(String musicId);
}
