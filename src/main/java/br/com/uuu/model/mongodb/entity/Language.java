package br.com.uuu.model.mongodb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

@Document(collection = "languages")
public class Language {

	@Id
	private String id;
	
	private String code;
	
	private String name;

}
