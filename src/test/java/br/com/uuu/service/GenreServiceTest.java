package br.com.uuu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.converter.GenreConverter;
import br.com.uuu.json.input.genre.GenreCreateInput;
import br.com.uuu.json.input.genre.GenreUpdateInput;
import br.com.uuu.json.output.genre.GenreOutput;
import br.com.uuu.model.mongodb.entity.Genre;
import br.com.uuu.model.mongodb.repository.GenreRepository;

class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;
    
    @Mock
    private GenreConverter genreConverter;

    @InjectMocks
    private GenreService genreService;

    private List<Genre> genres;
    
    private List<GenreCreateInput> genreCreateInputs;
    
    private List<GenreOutput> genreOutputs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        genres = new ArrayList<>();
        genreCreateInputs = new ArrayList<>();
        genreOutputs = new ArrayList<>();
        genres.add(buildGenre("1", "Clássica", "Música de concerto, chamada popularmente de música clássica ou música erudita, é a principal variedade de música produzida ou enraizada nas tradições da música secular e litúrgica ocidental."));
        genres.add(buildGenre("2", "Pop", "A música pop é um gênero da música popular que se originou durante a década de 1950 nos Estados Unidos e Reino Unido."));
        genres.add(buildGenre("3", "J-Rock", "Rock japonês, também conhecido pela abreviatura J-rock é a música rock proveniente do Japão."));
        genres.forEach(genre -> {
        	genreCreateInputs.add(buildGenreCreateInput(genre.getName(), genre.getDescription()));
        	genreOutputs.add(buildGenreOutput(genre.getId(), genre.getName(), genre.getDescription()));
        });
    }

    private Genre buildGenre(String id, String name, String description) {
    	var genre = new Genre();
    	genre.setId(id);
    	genre.setDescription(description);
    	genre.setName(name);
        return genre;
	}

    private GenreCreateInput buildGenreCreateInput(String name, String description) {
        return GenreCreateInput.builder()
 				.name(name)
 				.description(description)
 				.build();
	}

    private GenreOutput buildGenreOutput(String id, String name, String description) {
        return GenreOutput.builder()
    			.id(id)
    			.name(name)
 				.description(description)
 				.build();
	}

    private void checkGenre(GenreOutput output, GenreOutput genre) {
		assertThat(output.getId()).isEqualTo(genre.getId());
		assertThat(output.getName()).isEqualTo(genre.getName());
		assertThat(output.getDescription()).isEqualTo(genre.getDescription());
	}

    @Test
    void whenSaveFromInputToOutput_thenGenreIsSaved() {
    	var genre = genres.get(0);
    	var genreCreateInput = genreCreateInputs.get(0);
    	var genreOutput = genreOutputs.get(0);
        
    	when(genreConverter.toEntity(any(Genre.class), eq(genreCreateInput))).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreConverter.toOutput(genre)).thenReturn(genreOutput);

        var output = genreService.saveFromInputToOutput(genreCreateInput);

        assertThat(output).isNotNull();
        checkGenre(output, genreOutput);
        
        verify(genreConverter).toEntity(any(Genre.class), eq(genreCreateInput));
        verify(genreRepository).save(genre);
        verify(genreConverter).toOutput(genre);
    }

    @Test
    void whenGetByIdToOutput_thenReturnGenreOutput() {
    	var genre = genres.get(0);
    	var genreOutput = genreOutputs.get(0);

        when(genreRepository.findById("1")).thenReturn(Optional.of(genre));
        when(genreConverter.toOutput(genre)).thenReturn(genreOutput);

        var output = genreService.getByIdToOutput("1");

        assertThat(output).isNotNull();
        checkGenre(output, genreOutput);

        verify(genreRepository).findById("1");
        verify(genreConverter).toOutput(genre);
    }

    @Test
    void whenInvalidGetByIdToOutput_thenThrowException() {
        when(genreRepository.findById("4")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            genreService.getByIdToOutput("4");
        });

        verify(genreRepository).findById("4");
    }

    @Test
    void whenGetAllToOutput_thenReturnAllGenreOutputs() {
        when(genreRepository.findAll()).thenReturn(genres);
        when(genreConverter.toOutput(genres)).thenReturn(genreOutputs);

        var allGenres = genreService.getAllToOutput();

        assertThat(allGenres).hasSize(genres.size());
        assertThat(allGenres).containsAll(genreOutputs);

        verify(genreRepository).findAll();
        verify(genreConverter).toOutput(genres);
    }

    @Test
    void whenUpdateFromInputToOutput_thenGenreIsUpdated() {
    	var genre = genres.get(0);
    	var updatedGenre = genres.get(2);
    	var genreOutput = genreOutputs.get(2);
    	var genreUpdateInput =
    			GenreUpdateInput.builder()
    			.name(Optional.of("J-Rock"))
    			.description(Optional.of("Rock japonês, também conhecido pela abreviatura J-rock é a música rock proveniente do Japão."))
    			.build();

    	when(genreConverter.toEntity(genre, genreUpdateInput)).thenReturn(updatedGenre);
        when(genreRepository.findById("1")).thenReturn(Optional.of(genre));
        when(genreRepository.save(updatedGenre)).thenReturn(updatedGenre);
        when(genreConverter.toOutput(updatedGenre)).thenReturn(genreOutput);

        var output = genreService.updateFromInputToOutput("1", genreUpdateInput);

        assertThat(output).isNotNull();
        checkGenre(output, genreOutput);

        verify(genreConverter).toEntity(genre, genreUpdateInput);
        verify(genreRepository).findById("1");
        verify(genreRepository).save(updatedGenre);
        verify(genreConverter).toOutput(updatedGenre);
    }

    @Test
    void whenDeleteLanguage_thenGenreIsDeleted() {
        genreService.delete("1");
        verify(genreRepository).deleteById("1");
    }

}
