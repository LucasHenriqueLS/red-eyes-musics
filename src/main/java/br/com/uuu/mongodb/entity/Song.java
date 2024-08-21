package br.com.uuu.mongodb.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.uuu.mongodb.util.DetailsByLanguageCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

@Document(collection = "songs")
public class Song {

	@Id
	private String id;
	
	private List<String> artistIds;
	
	private List<String> composerNames;
	
	private String albumId;
	
	private List<String> genreIds;
	
	private String originalLanguageId;
	
	private Map<String, DetailsByLanguageCode> detailsByLanguageCode;
	
	private Integer durationInSeconds;
	
	private LocalDate releaseDate;
	
	private String videoLink;
	
	private LocalDateTime createdAt;
	
	public Song() {
		artistIds = new ArrayList<>();
		composerNames = new ArrayList<>();
		genreIds = new ArrayList<>();
		detailsByLanguageCode = new LinkedHashMap<>();
	}

}
