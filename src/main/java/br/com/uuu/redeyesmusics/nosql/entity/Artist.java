package br.com.uuu.redeyesmusics.nosql.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

@Document(collection = "artists")
public class Artist {

	@Id
	private String id;
	
	private String name;
	
	private List<String> otherNames;
	
	private List<String> musicsIds;
	
	private List<String> albumsIds;
	
	public Artist() {
		otherNames = new ArrayList<>();
		musicsIds = new ArrayList<>();
		albumsIds = new ArrayList<>();
	}
}
