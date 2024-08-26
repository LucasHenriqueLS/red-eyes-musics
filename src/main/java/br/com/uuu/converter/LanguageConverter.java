package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import br.com.uuu.json.input.language.LanguageCreateInput;
import br.com.uuu.json.output.language.LanguageOutput;
import br.com.uuu.model.mongodb.entity.Language;

@Component
public class LanguageConverter {
	
	public Language toEntity(LanguageCreateInput input) {
		var language = new Language();

		language.setCode(input.getCode());
		language.setName(input.getName());

		return language;
	}
	
	public List<LanguageOutput> toOutput(List<Language> languages) {
		var outputs = new ArrayList<LanguageOutput>();
		for (var language : languages) {
			outputs.add(toOutput(language));
		}
		return outputs;
	}

	public LanguageOutput toOutput(Language language) {
		return LanguageOutput.builder()
				.id(language.getId())
				.code(language.getCode())
				.name(language.getName())
				.build();
	}

}
