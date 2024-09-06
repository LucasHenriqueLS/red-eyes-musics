package br.com.uuu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import br.com.uuu.model.mongodb.entity.Language;
import br.com.uuu.model.mongodb.repository.LanguageRepository;

class LanguageServiceTest {

    @Mock
    private LanguageRepository languageRepository;
    
    @Mock
    private LanguageConverter languageConverter;

    @InjectMocks
    private LanguageService languageService;

    private List<Language> languages;
    
    private List<LanguageCreateInput> languageCreateInputs;
    
    private List<LanguageOutput> languageOutputs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        languages = List.of(
        		buildLanguage("1", "en_US", "Inglês Americano"),
                buildLanguage("2", "pt_BR", "Português Brasileiro"),
                buildLanguage("3", "ja_JP", "Japonês")
        	);
        languageCreateInputs = languages.stream().map(entity ->
        	LanguageCreateInput.builder()
        	.code(entity.getCode())
        	.name(entity.getName())
        	.build()
        ).toList();
        languageOutputs = languages.stream().map(entity ->
        	LanguageOutput.builder()
        	.id(entity.getId())
        	.code(entity.getCode())
        	.name(entity.getName())
        	.build()
        ).toList();
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
    void whenSaveFromInputToOutputThenLanguageIsSaved() {
    	var language = languages.get(0);
    	var languageCreateInput = languageCreateInputs.get(0);
    	var languageOutput = languageOutputs.get(0);
        
    	when(languageConverter.toEntity(any(Language.class), any(LanguageCreateInput.class))).thenReturn(language);
        when(languageRepository.save(language)).thenReturn(language);
        when(languageConverter.toOutput(language)).thenReturn(languageOutput);

        var output = languageService.saveFromInputToOutput(languageCreateInput);

        assertThat(output).isNotNull();
        checkLanguage(output, languageOutput);
        
        verify(languageConverter).toEntity(any(Language.class), any(LanguageCreateInput.class));
        verify(languageRepository).save(language);
        verify(languageConverter).toOutput(language);
    }

    @Test
    void whenGetByIdToOutputThenReturnLanguageOutput() {
    	var language = languages.get(0);
    	var languageOutput = languageOutputs.get(0);

        when(languageRepository.findById("1")).thenReturn(Optional.of(language));
        when(languageConverter.toOutput(language)).thenReturn(languageOutput);

        var output = languageService.getByIdToOutput("1");

        assertThat(output).isNotNull();
        checkLanguage(output, languageOutput);

        verify(languageRepository).findById("1");
        verify(languageConverter).toOutput(language);
    }

    @Test
    void whenInvalidGetByIdToOutputThenThrowException() {
        when(languageRepository.findById("4")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            languageService.getByIdToOutput("4");
        });

        verify(languageRepository).findById("4");
    }

    @Test
    void whenGetAllToOutputThenReturnAllLanguageOutputs() {
        when(languageRepository.findAll()).thenReturn(languages);
        when(languageConverter.toOutput(languages)).thenReturn(languageOutputs);

        var allLanguages = languageService.getAllToOutput();

        assertThat(allLanguages).hasSize(languages.size());
        assertThat(allLanguages).containsAll(languageOutputs);

        verify(languageRepository).findAll();
        verify(languageConverter).toOutput(languages);
    }

    @Test
    void whenUpdateFromInputToOutputThenLanguageIsUpdated() {
    	var language = languages.get(0);
    	var updatedLanguage = languages.get(2);
    	var languageOutput = languageOutputs.get(2);
    	var languageUpdateInput =
    			LanguageUpdateInput.builder()
    			.code(Optional.of("ja_JP"))
    			.name(Optional.of("Japonês"))
    			.build();

    	when(languageConverter.toEntity(any(Language.class), any(LanguageUpdateInput.class))).thenReturn(updatedLanguage);
        when(languageRepository.findById("1")).thenReturn(Optional.of(language));
        when(languageRepository.save(updatedLanguage)).thenReturn(updatedLanguage);
        when(languageConverter.toOutput(updatedLanguage)).thenReturn(languageOutput);

        var output = languageService.updateFromInputToOutput("1", languageUpdateInput);

        assertThat(output).isNotNull();
        checkLanguage(output, languageOutput);

        verify(languageConverter).toEntity(any(Language.class), any(LanguageUpdateInput.class));
        verify(languageRepository).findById("1");
        verify(languageRepository).save(updatedLanguage);
        verify(languageConverter).toOutput(updatedLanguage);
    }

    @Test
    void whenDeleteLanguageThenLanguageIsDeleted() {
        languageService.delete("1");
        verify(languageRepository).deleteById("1");
    }

}
