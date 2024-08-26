package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.model.mongodb.entity.Language;
import br.com.uuu.model.mongodb.repository.LanguageRepository;

@Service
public class LanguageService {

	@Autowired
	private LanguageRepository languageRepository;

	public List<Language> getAll() {
		return languageRepository.findAll();
	}

	public Language save(Language language) {
		return languageRepository.save(language);
	}

	public Boolean existsById(String id) {
		return languageRepository.existsById(id);
	}

}
