package br.com.uuu.json.output.album;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class AlbumOutput {

	public String id;
	
	private String title;

	private LocalDate releaseDate;

	private List<String> artistIds;

	private String coverUrl;

	private List<String> genreIds;

	private String recordCompanyName;

}
