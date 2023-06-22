package br.com.uuu.redeyesmusics.dto.input.artist;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ArtistInput {
	
	@NotBlank
	private String name;
	
	private List<String> otherNames;
}
