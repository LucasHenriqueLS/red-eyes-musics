package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.uuu.converter.util.GetIdsIfAreValid;
import br.com.uuu.json.input.album.AlbumCreateInput;
import br.com.uuu.json.input.album.AlbumUpdateInput;
import br.com.uuu.json.output.album.AlbumOutput;
import br.com.uuu.model.mongodb.entity.Album;

@Component
public class AlbumConverter {

	@Autowired
	private GetIdsIfAreValid getIdsIfAreValid;

	public Album toEntity(Album album, AlbumCreateInput input) {
		album.setArtistIds(getIdsIfAreValid.getArtistIds(input.getArtistIds()));
		album.setGenreIds(getIdsIfAreValid.getGenreIds(input.getGenreIds()));

		album.setTitle(input.getTitle());
		album.setReleaseDate(input.getReleaseDate());
		album.setCoverUrl(input.getCoverUrl());
		album.setRecordCompanyName(input.getRecordCompanyName());

		return album;
	}

	public Album toEntity(Album album, AlbumUpdateInput input) {
		Optional.ofNullable(input.getArtistIds()).ifPresent(artistIds -> album.setArtistIds(getIdsIfAreValid.getArtistIds(artistIds)));
		Optional.ofNullable(input.getGenreIds()).ifPresent(genreIds -> album.setGenreIds(getIdsIfAreValid.getGenreIds(genreIds)));

		Optional.ofNullable(input.getTitle()).ifPresent(album::setTitle);
		Optional.ofNullable(input.getReleaseDate()).ifPresent(album::setReleaseDate);
		Optional.ofNullable(input.getCoverUrl()).ifPresent(album::setCoverUrl);
		Optional.ofNullable(input.getRecordCompanyName()).ifPresent(album::setRecordCompanyName);

		return album;
	}

	public List<AlbumOutput> toOutput(List<Album> albums) {
		var outputs = new ArrayList<AlbumOutput>();
		for (var album : albums) {
			outputs.add(toOutput(album));
		}
		return outputs;
	}

	public AlbumOutput toOutput(Album album) {
		return AlbumOutput.builder()
				.id(album.getId())
				.title(album.getTitle())
				.releaseDate(album.getReleaseDate())
				.artistIds(album.getArtistIds())
				.coverUrl(album.getCoverUrl())
				.genreIds(album.getGenreIds())
				.recordCompanyName(album.getRecordCompanyName())
			   .build();
	}

}
