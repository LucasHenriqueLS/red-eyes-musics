package br.com.uuu.redeyesmusics.nosql.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.uuu.redeyesmusics.nosql.util.Genre;
import br.com.uuu.redeyesmusics.nosql.util.Language;
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
	
	private Map<Language, String> names;
	
	private Map<Language, String> lyrics;
	
	private List<String> composersIds;
	
	private Map<Language, String> submittersIds;

	private Map<Language, List<String>> proofreadersIds;
	
//	private String videoLink;
	
	public Music() {
		genres = new ArrayList<>();
		names = new LinkedHashMap<>();
		lyrics = new LinkedHashMap<>();
		composersIds = new ArrayList<>();
		submittersIds = new LinkedHashMap<>();
		proofreadersIds = new LinkedHashMap<>();
	}
}
