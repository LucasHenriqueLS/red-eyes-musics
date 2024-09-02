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

import br.com.uuu.model.mongodb.entity.Genre;

@DataMongoTest
@ActiveProfiles("unit-test")
class GenreRepositoryTest {

	@Autowired
    private GenreRepository genreRepository;

    private List<Genre> genres;
    
    @BeforeEach
    void setUp() {
        genreRepository.deleteAll();
        genres = new ArrayList<>();
        genres.add(buildGenre("Clássica", "Música de concerto, chamada popularmente de música clássica ou música erudita, é a principal variedade de música produzida ou enraizada nas tradições da música secular e litúrgica ocidental."));
        genres.add(buildGenre("Pop", "A música pop é um gênero da música popular que se originou durante a década de 1950 nos Estados Unidos e Reino Unido."));
        genres.add(buildGenre("J-Rock", "Rock japonês, também conhecido pela abreviatura J-rock é a música rock proveniente do Japão."));
    }

	private Genre buildGenre(String name, String description) {
		var genre = new Genre();
		genre.setName(name);
		genre.setDescription(description);
        return genre;
	}

	private void checkGenre(Genre entity, Genre genre) {
		assertThat(entity.getId()).isEqualTo(genre.getId());
		assertThat(entity.getName()).isEqualTo(genre.getName());
		assertThat(entity.getDescription()).isEqualTo(genre.getDescription());
	}

	private Optional<Genre> findById(String id) {
		return genreRepository.findById(id);
	}
	
	private List<Genre> findAll() {
		return genreRepository.findAll();
	}

	private void save(Genre genre) {
		genreRepository.save(genre);
	}
	
	private void deleteById(String id) {
		genreRepository.deleteById(id);
	}

	@Test
    void whenSave_thenGenreIsSaved() {
		for (var genre : genres) {
			save(genre);
			assertThat(genre.getId()).isNotBlank();
		}
    }

    @Test
    void whenFindById_thenReturnGenre() {
    	for (var genre : genres) {
    		save(genre);
    		var optional = findById(genre.getId());
    		assertThat(optional).isPresent();
    		optional.ifPresent(entity -> {
    			checkGenre(entity, genre);
    		});
    	}
    }

    @Test
    void whenFindByInvalidId_thenReturnEmpty() {
        var optional = findById("invalid_id");
        assertThat(optional).isNotPresent();
    }

    @Test
	void whenFindAll_thenReturnAllGenres() {
    	for (var genre : genres) {
    		save(genre);
    	}
	    var allGenres = findAll();
	    assertThat(allGenres).hasSize(genres.size());
	    for (int i = 0; i < genres.size(); i++) {
	    	checkGenre(allGenres.get(i), genres.get(i));
	    }
	}

    @Test
    void whenDeleteById_thenGenreIsDeleted() {
    	for (var genre : genres) {
    		save(genre);
    		deleteById(genre.getId());
    		var optional = findById(genre.getId());
    		assertThat(optional).isNotPresent();
    	}
    }

}
