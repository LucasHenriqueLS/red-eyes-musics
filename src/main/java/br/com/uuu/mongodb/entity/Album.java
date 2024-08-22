package br.com.uuu.mongodb.entity;

import java.time.LocalDate;
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

	public Album() {
		artistIds = new ArrayList<>();
		genreIds = new ArrayList<>();
	}

}
