package br.com.uuu.redeyesmusics.nosql.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
	
	private Language originalLanguage;
	
	private Map<Language, String> names;
	
	private Map<Language, String> lyrics;
	
	private String composerId;
	
	private String submitterId;
	
	private String translatorId;
	
//	private String proofreaderId;
	
//	private String videoLink;
	
	public Music() {
		names = new LinkedHashMap<>();
		lyrics = new LinkedHashMap<>();
	}
}
