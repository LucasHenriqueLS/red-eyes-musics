package br.com.uuu.model.mongodb.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

@Document(collection = "users")
public class User {

	@Id
	private String id;
	
	private String username;
	
	private String email;
	
	private String passwordHash;
	
	private String profileImageUrl;
	
	private List<String> favoriteGenreIds;
	
	private List<String> followingArtistIds;

}
