package br.com.uuu.converter;

import org.springframework.stereotype.Component;

import br.com.uuu.json.input.language.LanguageCreateInput;
import br.com.uuu.model.mongodb.entity.Language;

@Component
public class LanguageConverter {
	
	public Language toEntity(LanguageCreateInput input) {
		var language = new Language();

		language.setCode(input.getCode());
		language.setName(input.getName());

		return language;
	}

}
