package br.com.uuu.mongodb.entity;

import java.time.LocalDate;
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
	
	private String artistId;
	
	private String name;
	
	private LocalDate releaseDate;
	
	private String recordCompanyName;
	
	private Map<String, List<String>> musicsIdsByDiskNames;
	
	private String submitterId;
	
	private List<String> proofreadersIds;
	
	public Album() {
		musicsIdsByDiskNames = new LinkedHashMap<>();
		proofreadersIds = new ArrayList<>();
	}
}
