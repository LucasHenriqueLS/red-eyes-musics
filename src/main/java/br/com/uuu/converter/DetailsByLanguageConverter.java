package br.com.uuu.converter;

import org.springframework.stereotype.Component;

import br.com.uuu.json.input.song.DetailsByLanguageCodeCreateInput;
import br.com.uuu.mongodb.util.DetailsByLanguageCode;

@Component
public class DetailsByLanguageConverter {

	public DetailsByLanguageCode toEntity(DetailsByLanguageCodeCreateInput input) {
		var detailsByLanguageCode = new DetailsByLanguageCode();

		detailsByLanguageCode.setTitle(input.getTitle());
		detailsByLanguageCode.setLyric(input.getLyric());
		detailsByLanguageCode.setSubmitterId(input.getSubmitterId());

		return detailsByLanguageCode;
	}

}
