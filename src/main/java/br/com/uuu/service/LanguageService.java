package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.converter.LanguageConverter;
import br.com.uuu.json.input.language.LanguageCreateInput;
import br.com.uuu.model.mongodb.entity.Language;
import br.com.uuu.model.mongodb.repository.LanguageRepository;

@Service
public class LanguageService {

	@Autowired
	private LanguageRepository languageRepository;
	
	@Autowired
	private LanguageConverter languageConverter;

	public List<Language> getAll() {
		return languageRepository.findAll();
	}

	public Language save(LanguageCreateInput input) {
		return languageRepository.save(languageConverter.toEntity(input));
	}

}
