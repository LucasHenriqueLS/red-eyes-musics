package br.com.uuu.json.output.user;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class UserOutput {

	private String id;

	private String username;

	private String email;

	private String password;

	private String profileImageUrl;

	private List<String> favoriteGenreIds;

	private List<String> followingArtistIds;

}
