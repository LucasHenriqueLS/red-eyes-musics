package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.model.mongodb.entity.Language;
import br.com.uuu.model.mongodb.repository.LanguageRepository;

@Service
public class LanguageService {

	@Autowired
	private LanguageRepository languageRepository;

	public List<Language> getAll() {
		return languageRepository.findAll();
	}

	public Language getById(String id) {
		return languageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Idioma com o ID %s não foi encontrado", id)));
	}

	public Boolean existsById(String id) {
		return languageRepository.existsById(id);
	}

	public Language save(Language language) {
		return languageRepository.save(language);
	}
	
	public Language update(String id, Language language) {
		ifNotExistsThrowNotFound(id);

		language.setId(id);

		return save(language);
	}

	public void delete(String id) {
		ifNotExistsThrowNotFound(id);

		languageRepository.deleteById(id);
	}
	
	public void ifNotExistsThrowNotFound(String id) {
		if (!existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Idioma com o ID %s não foi encontrado", id));
		}
	}

}
