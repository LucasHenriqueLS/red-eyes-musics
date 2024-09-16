package br.com.uuu.json.output.song;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class SongDetailsOutput {

	private String title;
	
	private String lyric;
	
	private String submitterId;
	
	private List<String> proofreaderIds;

}
