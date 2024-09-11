package br.com.uuu.model.mongodb.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
public class ArtistRepositoryTest {

	@Autowired
    private ArtistRepository artistRepository;

    private List<Artist> artists;
    
    public static List<Artist> getArtists() {
    	return List.of(
    			buildArtist(List.of("Michael Jackson", "Michael Joseph Jackson"), "Michael Jackson foi um cantor, compositor e dançarino norte-americano, amplamente considerado o Rei do Pop e um dos artistas mais influentes da história da música.", List.of("b969e7a595f1d87174579c", "c87179e9c79647a557f17b95"), "https://example.com/images/michael_jackson.jpg"),
    			buildArtist(List.of("Ariana Grande, Ariana Grande-Butera"), "Ariana Grande é uma cantora e atriz norte-americana reconhecida por sua poderosa voz e influente presença na cultura pop.", List.of("e969e7a595f1d87174579c", "f87179e9c79647a557f17b95"), "https://example.com/images/ariana_grande.jpg"),
    			buildArtist(List.of("Hans Zimmer", "Hans Florian Zimmer"), "Hans Zimmer é um renomado compositor de trilhas sonoras para cinema, famoso por seu trabalho em filmes como 'O Rei Leão' e 'Duna'.", List.of("h969e7a595f1d87174579c", "i87179e9c79647a557f17b95"), "https://example.com/images/hans_zimmer.jpg")
    		);
    }

    @BeforeEach
    void setUp() {
        artistRepository.deleteAll();
        artists = getArtists();        
    }

	private static Artist buildArtist(List<String> names, String bio, List<String> genreIds, String imageUrl) {
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
