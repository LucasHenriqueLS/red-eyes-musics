package br.com.uuu.model.mongodb.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.uuu.model.mongodb.entity.Language;

@DataMongoTest
@ActiveProfiles("unit-test")
public class LanguageRepositoryTest {

	@Autowired
    private LanguageRepository languageRepository;

    private List<Language> languages;
    
    public static List<Language> getLanguages() {
    	return List.of(
    			buildLanguage("en_US", "Inglês Americano"),
    	        buildLanguage("pt_BR", "Português Brasileiro"),
    	        buildLanguage("ja_JP", "Japonês")		
    		);
    }

    @BeforeEach
    void setUp() {
        languageRepository.deleteAll();
        languages = getLanguages();
    }

	private static Language buildLanguage(String code, String name) {
		var language = new Language();
		language.setCode(code);
		language.setName(name);
        return language;
	}

	private void checkLanguage(Language entity, Language language) {
		assertThat(entity.getId()).isEqualTo(language.getId());
		assertThat(entity.getCode()).isEqualTo(language.getCode());
		assertThat(entity.getName()).isEqualTo(language.getName());
	}

	private Optional<Language> findById(String id) {
		return languageRepository.findById(id);
	}
	
	private List<Language> findAll() {
		return languageRepository.findAll();
	}

	private void save(Language language) {
		languageRepository.save(language);
	}
	
	private void deleteById(String id) {
		languageRepository.deleteById(id);
	}

	@Test
    void whenSave_thenLanguageIsSaved() {
		for (var language : languages) {
			save(language);
			assertThat(language.getId()).isNotBlank();
		}
    }

    @Test
    void whenFindById_thenReturnLanguage() {
    	for (var language : languages) {
    		save(language);
    		var optional = findById(language.getId());
    		assertThat(optional).isPresent();
    		optional.ifPresent(entity -> {
    			checkLanguage(entity, language);
    		});
    	}
    }

    @Test
    void whenFindByInvalidId_thenReturnEmpty() {
        var optional = findById("invalid_id");
        assertThat(optional).isNotPresent();
    }

    @Test
	void whenFindAll_thenReturnAllLanguages() {
    	for (var language : languages) {
    		save(language);
    	}
	    var allLanguages = findAll();
	    assertThat(allLanguages).hasSize(languages.size());
	    for (int i = 0; i < languages.size(); i++) {
	    	checkLanguage(allLanguages.get(i), languages.get(i));
	    }
	}

    @Test
    void whenDeleteById_thenLanguageIsDeleted() {
    	for (var language : languages) {
    		save(language);
    		deleteById(language.getId());
    		var optional = findById(language.getId());
    		assertThat(optional).isNotPresent();
    	}
    }

}
