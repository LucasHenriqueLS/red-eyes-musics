package br.com.uuu.model.mongodb.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.uuu.model.mongodb.entity.Album;

@DataMongoTest
@ActiveProfiles("unit-test")
public class AlbumRepositoryTest {

	@Autowired
    private AlbumRepository albumRepository;

    private static List<Album> albums;
    
    public static List<Album> getAlbums() {
    	return List.of(
	    		buildAlbum("Thriller", LocalDate.parse("1982-11-30"), List.of("a87179e9c79647a557f17b95"), "https://example.com/images/thriller.jpg", List.of("b969e7a595f1d87174579c", "c87179e9c79647a557f17b95"), "Epic Records"),
	    		buildAlbum("Thank U, Next", LocalDate.parse("2019-02-08"), List.of("d87179e9c79647a557f17b95"), "https://example.com/images/thank_u_next.jpg", List.of("e969e7a595f1d87174579c", "f87179e9c79647a557f17b95"), "Republic Records"),
	    		buildAlbum("Inception (Original Motion Picture Soundtrack)", LocalDate.parse("2010-07-13"), List.of("g87179e9c79647a557f17b95"), "https://example.com/images/inception_soundtrack.jpg", List.of("h969e7a595f1d87174579c", "i87179e9c79647a557f17b95"), "Reprise Records")		
    		);
    }

    @BeforeEach
    void setUp() {
        albumRepository.deleteAll();
        albums = getAlbums();
    }

	private static Album buildAlbum(String title, LocalDate releaseDate, List<String> artistIds, String coverUrl, List<String> genreIds, String recordCompanyName) {
		var album = new Album();
		album.setTitle(title);
		album.setReleaseDate(releaseDate);
		album.setArtistIds(artistIds);
		album.setCoverUrl(coverUrl);
		album.setGenreIds(genreIds);
		album.setRecordCompanyName(recordCompanyName);
        return album;
	}

	private void checkAlbum(Album entity, Album album) {
		assertThat(entity.getId()).isEqualTo(album.getId());
		assertThat(entity.getTitle()).isEqualTo(album.getTitle());
		assertThat(entity.getReleaseDate()).isEqualTo(album.getReleaseDate());
		assertThat(entity.getArtistIds()).isEqualTo(album.getArtistIds());
		assertThat(entity.getCoverUrl()).isEqualTo(album.getCoverUrl());
		assertThat(entity.getGenreIds()).isEqualTo(album.getGenreIds());
		assertThat(entity.getRecordCompanyName()).isEqualTo(album.getRecordCompanyName());
	}

	private Optional<Album> findById(String id) {
		return albumRepository.findById(id);
	}

	private List<Album> findAll() {
		return albumRepository.findAll();
	}

	private void save(Album album) {
		albumRepository.save(album);
	}

	private void deleteById(String id) {
		albumRepository.deleteById(id);
	}

	@Test
    void whenSave_thenAlbumIsSaved() {
		for (var album : albums) {
			save(album);
			assertThat(album.getId()).isNotBlank();
		}
    }

    @Test
    void whenFindById_thenReturnAlbum() {
    	for (var album : albums) {
    		save(album);
    		var optional = findById(album.getId());
    		assertThat(optional).isPresent();
    		optional.ifPresent(entity -> {
    			checkAlbum(entity, album);
    		});
    	}
    }

    @Test
    void whenFindByInvalidId_thenReturnEmpty() {
        var optional = findById("invalid_id");
        assertThat(optional).isNotPresent();
    }

    @Test
	void whenFindAll_thenReturnAllAlbum() {
    	for (var album : albums) {
    		save(album);
    	}
	    var allAlbum = findAll();
	    assertThat(allAlbum).hasSize(albums.size());
	    for (int i = 0; i < albums.size(); i++) {
	    	checkAlbum(allAlbum.get(i), albums.get(i));
	    }
	}

    @Test
    void whenDeleteById_thenAlbumIsDeleted() {
    	for (var album : albums) {
    		save(album);
    		deleteById(album.getId());
    		var optional = findById(album.getId());
    		assertThat(optional).isNotPresent();
    	}
    }

}
