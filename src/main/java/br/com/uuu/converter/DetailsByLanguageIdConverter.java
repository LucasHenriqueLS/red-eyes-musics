package br.com.uuu.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.json.input.song.DetailsByLanguageIdCreateInput;
import br.com.uuu.model.mongodb.util.DetailsByLanguageId;
import br.com.uuu.service.UserService;

@Component
public class DetailsByLanguageIdConverter {

	@Autowired
	private UserService userService;
	
	public DetailsByLanguageId toEntity(DetailsByLanguageIdCreateInput input) {
		var detailsByLanguageCode = new DetailsByLanguageId();

		detailsByLanguageCode.setTitle(input.getTitle());
		detailsByLanguageCode.setLyric(input.getLyric());

		var submitterId = input.getSubmitterId();
		if (submitterId != null) {
			if (userService.existsById(submitterId)) {
				detailsByLanguageCode.setSubmitterId(input.getSubmitterId());
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Usuário com o ID %s não foi encontrado", submitterId));
			}			
		}

		return detailsByLanguageCode;
	}

}
