package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.converter.LanguageConverter;
import br.com.uuu.json.input.language.LanguageCreateInput;
import br.com.uuu.json.input.language.LanguageUpdateInput;
import br.com.uuu.json.output.language.LanguageOutput;
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
	
	public List<LanguageOutput> getAllToOutput() {
		return languageConverter.toOutput(getAll());
	}

	public Language getById(String id) {
		return languageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Idioma com o ID %s não foi encontrado", id)));
	}

	public LanguageOutput getByIdToOutput(String id) {
		return languageConverter.toOutput(getById(id));
	}

	public Boolean existsById(String id) {
		return languageRepository.existsById(id);
	}

	public Language save(Language language) {
		return languageRepository.save(language);
	}

	public LanguageOutput saveFromInputToOutput(LanguageCreateInput input) {
		var language = new Language();
		return languageConverter.toOutput(save(languageConverter.toEntity(language, input)));
	}

	public LanguageOutput updateFromInputToOutput(String id, LanguageUpdateInput input) {
		var language = getById(id);
		return languageConverter.toOutput(save(languageConverter.toEntity(language, input)));
	}

	public void delete(String id) {
		languageRepository.deleteById(id);
	}

}
