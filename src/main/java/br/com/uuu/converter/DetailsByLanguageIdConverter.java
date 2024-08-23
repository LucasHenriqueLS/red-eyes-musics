package br.com.uuu.converter;

import org.springframework.stereotype.Component;

import br.com.uuu.json.input.song.DetailsByLanguageIdCreateInput;
import br.com.uuu.model.mongodb.util.DetailsByLanguageId;

@Component
public class DetailsByLanguageIdConverter {

	public DetailsByLanguageId toEntity(DetailsByLanguageIdCreateInput input) {
		var detailsByLanguageCode = new DetailsByLanguageId();

		detailsByLanguageCode.setTitle(input.getTitle());
		detailsByLanguageCode.setLyric(input.getLyric());
		detailsByLanguageCode.setSubmitterId(input.getSubmitterId());

		return detailsByLanguageCode;
	}

}
