package br.com.uuu.mongodb.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.uuu.mongodb.util.Genre;
import br.com.uuu.mongodb.util.Language;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

@Document(collection = "musics")
public class Music {

	@Id
	private String id;
	
	private String artistId;
	
	private List<Genre> genres;
	
	private Language originalLanguage;
	
	private Map<Language, String> nameByLanguages;
	
	private Map<Language, String> lyricByLanguages;
	
	private List<String> composersNames;
	
	private Map<Language, String> submitterIdByLanguages;

	private Map<Language, List<String>> proofreadersIdsByLanguages;
	
//	private String videoLink;
	
	public Music() {
		genres = new ArrayList<>();
		nameByLanguages = new LinkedHashMap<>();
		lyricByLanguages = new LinkedHashMap<>();
		composersNames = new ArrayList<>();
		submitterIdByLanguages = new LinkedHashMap<>();
		proofreadersIdsByLanguages = new LinkedHashMap<>();
	}
}
