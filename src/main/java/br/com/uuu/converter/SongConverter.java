package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.uuu.converter.util.GetIdsIfAreValid;
import br.com.uuu.json.input.song.SongCreateInput;
import br.com.uuu.json.input.song.SongUpdateInput;
import br.com.uuu.json.output.song.SongOutput;
import br.com.uuu.model.mongodb.entity.Song;
import br.com.uuu.model.mongodb.util.SongDetails;

@Component
public class SongConverter {

	@Autowired
	private SongDetailsConverter songDetailsConverter;
	
	@Autowired
	private GetIdsIfAreValid getIdsIfAreValid;
	
	public Song toEntity(Song song, SongCreateInput input) {
		song.setArtistIds(getIdsIfAreValid.getArtistIds(input.getArtistIds()));
		setComposerNamesIfIsValid(song, input.getComposerNames());
		song.setAlbumId(getIdsIfAreValid.getAlbumId(input.getAlbumId()));
		song.setGenreIds(getIdsIfAreValid.getGenreIds(input.getGenreIds()));
		song.setOriginalLanguageId(getIdsIfAreValid.getLanguageId(input.getOriginalLanguageId()));

		input.getDetailsByLanguageId().forEach((language, songDetailsInput) ->  {
			song.getDetailsByLanguageId().put(getIdsIfAreValid.getLanguageId(language), songDetailsConverter.toEntity(new SongDetails(), songDetailsInput));
		});

		song.setDurationInSeconds(input.getDurationInSeconds());
		song.setReleaseDate(input.getReleaseDate());
		song.setVideoUrl(input.getVideoUrl());

		return song;
	}

	public Song toEntity(Song song, SongUpdateInput input) {
		Optional.ofNullable(input.getArtistIds()).ifPresent(artistIds -> song.setArtistIds(getIdsIfAreValid.getArtistIds(artistIds)));
		Optional.ofNullable(input.getComposerNames()).ifPresent(composerNames -> setComposerNamesIfIsValid(song, composerNames));
		Optional.ofNullable(input.getAlbumId()).ifPresent(albumId -> song.setAlbumId(getIdsIfAreValid.getAlbumId(albumId)));
		Optional.ofNullable(input.getGenreIds()).ifPresent(genreIds -> song.setGenreIds(getIdsIfAreValid.getGenreIds(genreIds)));
		Optional.ofNullable(input.getOriginalLanguageId()).ifPresent(originalLanguageId -> song.setOriginalLanguageId(getIdsIfAreValid.getLanguageId(originalLanguageId)));

		Optional.ofNullable(input.getDetailsByLanguageId()).ifPresent(detailsByLanguageId ->
			detailsByLanguageId.forEach((language, songDetailsInput) ->  {
				song.getDetailsByLanguageId().put(getIdsIfAreValid.getLanguageId(language), songDetailsConverter.toEntity(new SongDetails(), songDetailsInput));
			})
		);

		Optional.ofNullable(input.getDurationInSeconds()).ifPresent(song::setDurationInSeconds);
		Optional.ofNullable(input.getReleaseDate()).ifPresent(song::setReleaseDate);
		Optional.ofNullable(input.getVideoUrl()).ifPresent(song::setVideoUrl);

		return song;
	}


	private void setComposerNamesIfIsValid(Song song, List<String> composerNames) {
		if (composerNames != null && !composerNames.isEmpty()) {
			song.setComposerNames(composerNames);			
		} else {
			song.getComposerNames().add("Desconhecido");
		}
	}

	public List<SongOutput> toOutput(List<Song> songs) {
		var outputs = new ArrayList<SongOutput>();
		for (var song : songs) {
			outputs.add(toOutput(song));
		}
		return outputs;
	}

	public SongOutput toOutput(Song song) {
		return SongOutput.builder()
				.id(song.getId())
				.artistIds(song.getArtistIds())
				.composerNames(song.getComposerNames())
				.albumId(song.getAlbumId())
				.genreIds(song.getGenreIds())
				.originalLanguageId(song.getOriginalLanguageId())
				.detailsByLanguageId(
					song.getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> songDetailsConverter.toOutput(entry.getValue())
					))
				)
				.durationInSeconds(song.getDurationInSeconds())
				.releaseDate(song.getReleaseDate())
				.videoUrl(song.getVideoUrl())
			   .build();
	}

}
