package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.json.input.song.SongDetailsCreateInput;
import br.com.uuu.json.input.song.SongDetailsUpdateInput;
import br.com.uuu.json.output.song.SongDetailsOutput;
import br.com.uuu.model.mongodb.util.SongDetails;
import br.com.uuu.service.UserService;

@Component
public class SongDetailsConverter {

	@Autowired
	private UserService userService;
	
	public SongDetails toEntity(SongDetails songDetails, SongDetailsCreateInput input) {
		setSubmitterIfIsValid(songDetails, input.getSubmitterId());

		songDetails.setTitle(input.getTitle());
		songDetails.setLyric(input.getLyric());

		return songDetails;
	}

	public SongDetails toEntity(SongDetails songDetails, SongDetailsUpdateInput input) {
		Optional.ofNullable(input.getSubmitterId()).ifPresent(submitterId -> setSubmitterIfIsValid(songDetails, submitterId));
		Optional.ofNullable(input.getProofreaderIds()).ifPresent(proofreaderId -> setProofreadersIfIsValid(songDetails, proofreaderId));

		Optional.ofNullable(input.getTitle()).ifPresent(songDetails::setTitle);
		Optional.ofNullable(input.getLyric()).ifPresent(songDetails::setLyric);

		return songDetails;
	}

	private void setSubmitterIfIsValid(SongDetails songDetails, String submitterId) {
		if (userService.existsById(submitterId)) {
			songDetails.setSubmitterId(submitterId);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Usuário com o ID %s não foi encontrado", submitterId));
		}			
	}

	private void setProofreadersIfIsValid(SongDetails songDetails, List<String> proofreaderIds) {
		var proofreaderIdsNotFound = userService.getAllIdsNotFound(proofreaderIds);
		if (proofreaderIdsNotFound.isEmpty()) {
			songDetails.setProofreaderIds(proofreaderIdsNotFound);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Usuários com os IDs %s não foram encontrados", proofreaderIdsNotFound));
		}			
	}
	
	public List<SongDetailsOutput> toOutput(List<SongDetails> songDetailsList) {
		var outputs = new ArrayList<SongDetailsOutput>();
		for (var songDetails : songDetailsList) {
			outputs.add(toOutput(songDetails));
		}
		return outputs;
	}

	public SongDetailsOutput toOutput(SongDetails songDetails) {
		return SongDetailsOutput.builder()
				.title(songDetails.getTitle())
				.lyric(songDetails.getLyric())
				.submitterId(songDetails.getSubmitterId())
				.proofreaderIds(songDetails.getProofreaderIds())
			   .build();
	}

}
