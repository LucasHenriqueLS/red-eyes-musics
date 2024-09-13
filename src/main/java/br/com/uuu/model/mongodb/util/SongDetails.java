package br.com.uuu.model.mongodb.util;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongDetails {

	private String title;
	
	private String lyric;
	
	private String submitterId;
	
	private List<String> proofreaderIds;

}
