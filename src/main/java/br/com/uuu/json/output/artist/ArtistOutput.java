package br.com.uuu.json.output.artist;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ArtistOutput {

	private String id;

	private List<String> names;

	private String bio;

	private List<String> genreIds;

	private String imageUrl;

}
