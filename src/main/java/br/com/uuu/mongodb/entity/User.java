//package br.com.uuu.mongodb.entity;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import br.com.uuu.mongodb.util.Country;
//import br.com.uuu.mongodb.util.Language;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//@Getter
//@Setter
//@ToString
//
//@Document(collection = "users")
//public class User {
//
//	@Id
//	private String id;
//	
//	private String name;
//	
//	private String email;
//	
//	private String password;
//	
//	private LocalDate birthDate;
//	
//	private Country country;
//	
//	private Language language;
//	
//	private String profilePicture;
//	
//	private Map<String, List<String>> musicsIdsByPlaylistNames;
//	
//	private List<String> favoriteMusicsIds;
//	
//	private List<String> favoriteAlbumsIds;
//	
//	private List<String> favoriteArtistsIds;
//	
//	public User() {
//		musicsIdsByPlaylistNames = new LinkedHashMap<>();
//		favoriteMusicsIds = new ArrayList<>();
//		favoriteAlbumsIds = new ArrayList<>();
//		favoriteArtistsIds = new ArrayList<>();
//	}
//}
