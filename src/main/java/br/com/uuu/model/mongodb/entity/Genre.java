package br.com.uuu.model.mongodb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

@Document(collection = "genres")
public class Genre {

	@Id
	private String id;
	
	private String name;
	
	private String description;

}
