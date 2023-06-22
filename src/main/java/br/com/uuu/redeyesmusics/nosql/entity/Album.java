package br.com.uuu.redeyesmusics.nosql.entity;

import java.time.LocalDate;
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
	
	private String name;
	
	private LocalDate releaseDate;
	
	private String recordCompany;
	
	private Map<String, List<String>> musicsByDisk;
}
