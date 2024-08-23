package br.com.uuu.model.mongodb.util;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DetailsByLanguageId {

	private String title;
	
	private String lyric;
	
	private String submitterId;
	
	private List<String> proofreaderIds;

}
