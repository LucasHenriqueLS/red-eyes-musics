package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.uuu.converter.util.GetIdsIfAreValid;
import br.com.uuu.json.input.song.SongDetailsCreateInput;
import br.com.uuu.json.input.song.SongDetailsUpdateInput;
import br.com.uuu.json.output.song.SongDetailsOutput;
import br.com.uuu.model.mongodb.util.SongDetails;

@Component
public class SongDetailsConverter {

	@Autowired
	private GetIdsIfAreValid getIdsIfAreValid;
	
	public SongDetails toEntity(SongDetails songDetails, SongDetailsCreateInput input) {
		songDetails.setSubmitterId(getIdsIfAreValid.getUserId(input.getSubmitterId()));

		songDetails.setTitle(input.getTitle());
		songDetails.setLyric(input.getLyric());

		return songDetails;
	}

	public SongDetails toEntity(SongDetails songDetails, SongDetailsUpdateInput input) {
		Optional.ofNullable(input.getSubmitterId()).ifPresent(submitterId -> songDetails.setSubmitterId(getIdsIfAreValid.getUserId(submitterId)));
		Optional.ofNullable(input.getProofreaderIds()).ifPresent(proofreaderId -> songDetails.setProofreaderIds(getIdsIfAreValid.getUserIds(proofreaderId)));

		Optional.ofNullable(input.getTitle()).ifPresent(songDetails::setTitle);
		Optional.ofNullable(input.getLyric()).ifPresent(songDetails::setLyric);

		return songDetails;
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
