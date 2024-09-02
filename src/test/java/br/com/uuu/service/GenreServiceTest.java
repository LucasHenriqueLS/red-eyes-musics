package br.com.uuu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import br.com.uuu.converter.LanguageConverter;
import br.com.uuu.json.input.language.LanguageCreateInput;
import br.com.uuu.json.input.language.LanguageUpdateInput;
import br.com.uuu.json.output.language.LanguageOutput;
import br.com.uuu.model.mongodb.entity.Genre;
import br.com.uuu.model.mongodb.entity.Language;
import br.com.uuu.model.mongodb.repository.LanguageRepository;

class GenreServiceTest {

    @Mock
    private LanguageRepository genreRepository;
    
    @Mock
    private LanguageConverter genreConverter;

    @InjectMocks
    private LanguageService genreService;

    private List<Genre> genres;
    
    private List<LanguageCreateInput> languageCreateInputs;
    
    private List<LanguageOutput> languageOutputs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        genres = new ArrayList<>();
        languageCreateInputs = new ArrayList<>();
        languageOutputs = new ArrayList<>();
        genres.add(buildLanguage("1", "en_US", "Inglês Americano"));
        genres.add(buildLanguage("2", "pt_BR", "Português Brasileiro"));
        genres.add(buildLanguage("3", "ja_JP", "Japonês"));
        languageCreateInputs.add(
    			LanguageCreateInput.builder()
 				.code("en_US")
 				.name("Inglês Americano")
 				.build());
    	languageCreateInputs.add(
    			LanguageCreateInput.builder()
 				.code("pt_BR")
 				.name("Português Brasileiro")
 				.build());
    	languageCreateInputs.add(
    			LanguageCreateInput.builder()
 				.code("ja_JP")
 				.name("Japonês")
 				.build());
    	languageOutputs.add(
    			LanguageOutput.builder()
    			.id("1")
    			.code("en_US")
    			.name("Inglês Americano")
    			.build());
    	languageOutputs.add(
    			LanguageOutput.builder()
    			.id("2")
    			.code("pt_BR")
 				.name("Português Brasileiro")
    			.build());
    	languageOutputs.add(
    			LanguageOutput.builder()
    			.id("3")
    			.code("ja_JP")
 				.name("Japonês")
    			.build());
    }

    private Language buildLanguage(String id, String code, String name) {
		var language = new Language();
		language.setId(id);
		language.setCode(code);
		language.setName(name);
        return language;
	}

    private void checkLanguage(LanguageOutput output, LanguageOutput language) {
		assertThat(output.getId()).isEqualTo(language.getId());
		assertThat(output.getCode()).isEqualTo(language.getCode());
		assertThat(output.getName()).isEqualTo(language.getName());
	}

    @Test
    void whenSaveFromInputToOutput_thenLanguageIsSaved() {
    	var language = genres.get(0);
    	var languageCreateInput = languageCreateInputs.get(0);
    	var languageOutput = languageOutputs.get(0);
        
    	when(genreConverter.toEntity(any(Language.class), any(LanguageCreateInput.class))).thenReturn(language);
        when(genreRepository.save(language)).thenReturn(language);
        when(genreConverter.toOutput(language)).thenReturn(languageOutput);

        var output = genreService.saveFromInputToOutput(languageCreateInput);

        assertThat(output).isNotNull();
        checkLanguage(output, languageOutput);
        
        verify(genreConverter).toEntity(any(Language.class), any(LanguageCreateInput.class));
        verify(genreRepository).save(language);
        verify(genreConverter).toOutput(language);
    }

    @Test
    void whenGetByIdToOutput_thenReturnLanguageOutput() {
    	var language = genres.get(0);
    	var languageOutput = languageOutputs.get(0);

        when(genreRepository.findById("1")).thenReturn(Optional.of(language));
        when(genreConverter.toOutput(language)).thenReturn(languageOutput);

        var output = genreService.getByIdToOutput("1");

        assertThat(output).isNotNull();
        checkLanguage(output, languageOutput);

        verify(genreRepository).findById("1");
        verify(genreConverter).toOutput(language);
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
    void whenGetAllToOutput_thenReturnAllLanguageOutputs() {
        when(genreRepository.findAll()).thenReturn(genres);
        when(genreConverter.toOutput(genres)).thenReturn(languageOutputs);

        var allLanguages = genreService.getAllToOutput();

        assertThat(allLanguages).hasSize(genres.size());
        assertThat(allLanguages).containsAll(languageOutputs);

        verify(genreRepository).findAll();
        verify(genreConverter).toOutput(genres);
    }

    @Test
    void whenUpdateFromInputToOutput_thenLanguageIsUpdated() {
    	var language = genres.get(0);
    	var updatedLanguage = genres.get(2);
    	var languageOutput = languageOutputs.get(2);
    	var languageUpdateInput =
    			LanguageUpdateInput.builder()
    			.code(Optional.of("ja_JP"))
    			.name(Optional.of("Japonês"))
    			.build();

    	when(genreConverter.toEntity(any(Language.class), any(LanguageUpdateInput.class))).thenReturn(updatedLanguage);
        when(genreRepository.findById("1")).thenReturn(Optional.of(language));
        when(genreRepository.save(updatedLanguage)).thenReturn(updatedLanguage);
        when(genreConverter.toOutput(updatedLanguage)).thenReturn(languageOutput);

        var output = genreService.updateFromInputToOutput("1", languageUpdateInput);

        assertThat(output).isNotNull();
        checkLanguage(output, languageOutput);

        verify(genreConverter).toEntity(any(Language.class), any(LanguageUpdateInput.class));
        verify(genreRepository).findById("1");
        verify(genreRepository).save(updatedLanguage);
        verify(genreConverter).toOutput(updatedLanguage);
    }

    @Test
    void whenDeleteLanguage_thenLanguageIsDeleted() {
        genreService.delete("1");
        verify(genreRepository).deleteById("1");
    }

}
