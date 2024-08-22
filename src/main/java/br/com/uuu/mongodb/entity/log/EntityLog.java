//package br.com.uuu.mongodb.entity.log;
//
//import java.time.LocalDateTime;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//@Getter
//@Setter
//@ToString
//
//@Document(collection = "entity-logs")
//public class EntityLog<T> {
//
//	@Id
//	private String id;
//	
//	private T entity;
//
//	private LocalDateTime createdAt;
//	
//	private String createdById;
//	
//	private LocalDateTime updatedAt;
//	
//	private String updatedById;
//	
//	private LocalDateTime deletedAt;
//	
//	private String deletedById;
//
//}
