package br.com.uuu.model.mongodb.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.uuu.model.mongodb.entity.Artist;

@DataMongoTest
@ActiveProfiles("unit-test")
class ArtistRepositoryTest {

	@Autowired
    private ArtistRepository artistRepository;

    private List<Artist> artists;
    
    @BeforeEach
    void setUp() {
        artistRepository.deleteAll();
        artists = new ArrayList<>();
        artists.add(buildArtist(List.of("Mamiko Noto", "Noto Mamiko"), "????", List.of("J-Pop", "J-Rock"), "????"));
        artists.add(buildArtist(List.of("Skillet"), "????", List.of("Rock"), "????"));
        artists.add(buildArtist(List.of("Michael Jackson", "Michael Joseph Jackson"), "????", List.of("Pop"), "????"));
    }

	private Artist buildArtist(List<String> names, String bio, List<String> genreIds, String imageUrl) {
		var artist = new Artist();
		artist.setNames(names);
		artist.setBio(bio);
		artist.setGenreIds(genreIds);
		artist.setImageUrl(imageUrl);
        return artist;
	}

	private void checkArtist(Artist entity, Artist artist) {
		assertThat(entity.getId()).isEqualTo(artist.getId());
		assertThat(entity.getNames()).isEqualTo(artist.getNames());
		assertThat(entity.getBio()).isEqualTo(artist.getBio());
		assertThat(entity.getGenreIds()).isEqualTo(artist.getGenreIds());
		assertThat(entity.getImageUrl()).isEqualTo(artist.getImageUrl());
	}

	private Optional<Artist> findById(String id) {
		return artistRepository.findById(id);
	}

	private List<Artist> findAll() {
		return artistRepository.findAll();
	}

	private void save(Artist artist) {
		artistRepository.save(artist);
	}

	private void deleteById(String id) {
		artistRepository.deleteById(id);
	}

	@Test
    void whenSave_thenArtistIsSaved() {
		for (var artist : artists) {
			save(artist);
			assertThat(artist.getId()).isNotBlank();
		}
    }

    @Test
    void whenFindById_thenReturnArtist() {
    	for (var artist : artists) {
    		save(artist);
    		var optional = findById(artist.getId());
    		assertThat(optional).isPresent();
    		optional.ifPresent(entity -> {
    			checkArtist(entity, artist);
    		});
    	}
    }

    @Test
    void whenFindByInvalidId_thenReturnEmpty() {
        var optional = findById("invalid_id");
        assertThat(optional).isNotPresent();
    }

    @Test
	void whenFindAll_thenReturnAllArtist() {
    	for (var artist : artists) {
    		save(artist);
    	}
	    var allArtist = findAll();
	    assertThat(allArtist).hasSize(artists.size());
	    for (int i = 0; i < artists.size(); i++) {
	    	checkArtist(allArtist.get(i), artists.get(i));
	    }
	}

    @Test
    void whenDeleteById_thenArtistIsDeleted() {
    	for (var artist : artists) {
    		save(artist);
    		deleteById(artist.getId());
    		var optional = findById(artist.getId());
    		assertThat(optional).isNotPresent();
    	}
    }

}
