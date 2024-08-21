package br.com.uuu.mongodb.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

@Document(collection = "albums")
public class Album {

	@Id
	private String id;
	
	private String title;
	
	private LocalDate releaseDate;
	
	private List<String> artistIds;
	
	private String coverUrl;
	
	private List<String> genreIds;
	
	private String recordCompanyName;
	
//	private Map<String, List<String>> musicsIdsByDiskNames;
	
	private String submitterId;
	
	private List<String> proofreadersIds;
	
	private LocalDateTime createdAt;
	
	public Album() {
		artistIds = new ArrayList<>();
		genreIds = new ArrayList<>();
//		musicsIdsByDiskNames = new LinkedHashMap<>();
		proofreadersIds = new ArrayList<>();
	}

}
