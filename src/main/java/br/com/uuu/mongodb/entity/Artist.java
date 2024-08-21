package br.com.uuu.mongodb.entity;

import java.time.LocalDateTime;
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
	
	private List<String> names;
	
	private String bio;
	
	private List<String> genreIds;
	
	private String imageUrl;
	
	private LocalDateTime createdAt;
	
	public Artist() {
		names = new ArrayList<>();
		genreIds = new ArrayList<>();
	}

}
