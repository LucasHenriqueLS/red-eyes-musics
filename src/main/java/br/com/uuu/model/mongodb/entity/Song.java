package br.com.uuu.model.mongodb.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.uuu.model.mongodb.util.SongDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder

@Document(collection = "songs")
public class Song {

	@Id
	private String id;
	
	private List<String> artistIds;
	
	private List<String> composerNames;
	
	private String albumId;
	
	private List<String> genreIds;
	
	private String originalLanguageId;
	
	private Map<String, SongDetails> detailsByLanguageId;
	
	private Integer durationInSeconds;
	
	private LocalDate releaseDate;
	
	private String videoUrl;

	public Song() {
		artistIds = new ArrayList<>();
		composerNames = new ArrayList<>();
		genreIds = new ArrayList<>();
		detailsByLanguageId = new TreeMap<>();
	}

}
