package br.com.uuu.json.output.song;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class SongOutput {

	public String id;
	
	private List<String> artistIds;

	private List<String> composerNames;

	private String albumId;
	
	private List<String> genreIds;

	private String originalLanguageId;

	private Map<String, SongDetailsOutput> detailsByLanguageId;

	private Integer durationInSeconds;

	private LocalDate releaseDate;
	
	private String videoUrl;

}
