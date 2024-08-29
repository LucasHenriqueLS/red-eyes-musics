package br.com.uuu.model.mongodb.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.uuu.model.mongodb.entity.Language;

@DataMongoTest
@ActiveProfiles("test")
class LanguageRepositoryTest {

	@Autowired
    private LanguageRepository languageRepository;

    private List<Language> languages;
    
    @BeforeEach
    void setUp() {
        languageRepository.deleteAll();
        languages = new ArrayList<>();
        languages.add(buildLanguage("en_US", "Inglês Americano"));
        languages.add(buildLanguage("pt_BR", "Português Brasileiro"));
        languages.add(buildLanguage("ja_JP", "Japonês"));
    }

	private Language buildLanguage(String code, String name) {
		var language = new Language();
		language.setCode(code);
		language.setName(name);
        return language;
	}

	@Test
    void whenSaveLanguage_thenLanguageIsSaved() {
		for (var language : languages) {
			saveLanguage(language);
			assertThat(language.getId()).isNotBlank();
		}
    }

	private void saveLanguage(Language language) {
		languageRepository.save(language);
	}

    @Test
    void whenFindById_thenReturnLanguage() {
    	for (var language : languages) {
    		saveLanguage(language);
    		var optional = languageRepository.findById(language.getId());
    		assertThat(optional).isPresent();
    		optional.ifPresent(entity -> {
    			checkLanguage(entity, language);
    		});
    	}
    }

	private void checkLanguage(Language entity, Language language) {
		assertThat(entity.getCode()).isEqualTo(language.getCode());
		assertThat(entity.getName()).isEqualTo(language.getName());
	}

    @Test
    void whenFindByInvalidId_thenReturnEmpty() {
        var optional = languageRepository.findById("invalid_id");
        assertThat(optional).isNotPresent();
    }

    @Test
	void whenFindAll_thenReturnAllLanguages() {
    	for (var language : languages) {
    		saveLanguage(language);
    	}
	    var allLanguages = languageRepository.findAll();
	    assertThat(allLanguages).hasSize(languages.size());
	    for (int i = 0; i < languages.size(); i++) {
	    	checkLanguage(allLanguages.get(i), languages.get(i));
	    }
	}

    @Test
    void whenDeleteLanguage_thenLanguageIsDeleted() {
    	for (var language : languages) {
    		saveLanguage(language);
    		languageRepository.deleteById(language.getId());
    		var optional = languageRepository.findById(language.getId());
    		assertThat(optional).isNotPresent();
    	}
    }

}
