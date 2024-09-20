package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.json.input.user.UserCreateInput;
import br.com.uuu.json.input.user.UserUpdateInput;
import br.com.uuu.json.output.user.UserOutput;
import br.com.uuu.model.mongodb.entity.User;
import br.com.uuu.service.ArtistService;
import br.com.uuu.service.GenreService;

@Component
public class UserConverter {

	@Autowired
	private ArtistService artistService;

	@Autowired
	private GenreService genreService;
	
	public User toEntity(User user, UserCreateInput input) {
		user.setFavoriteGenreIds(getGenreIdsIfAreValid(input.getFavoriteGenreIds()));
		user.setFollowingArtistIds(getArtistIdsIfAreValid(input.getFollowingArtistIds()));

		user.setUsername(input.getUsername());
		user.setEmail(input.getEmail());
		user.setPassword(input.getPassword());
		user.setProfileImageUrl(input.getProfileImageUrl());

		return user;
	}

	public User toEntity(User user, UserUpdateInput input) {
		Optional.ofNullable(input.getFavoriteGenreIds()).ifPresent(genreIds -> getGenreIdsIfAreValid(genreIds));
		Optional.ofNullable(input.getFollowingArtistIds()).ifPresent(artistIds -> getArtistIdsIfAreValid(artistIds));

		Optional.ofNullable(input.getUsername()).ifPresent(user::setUsername);
		Optional.ofNullable(input.getEmail()).ifPresent(user::setEmail);
		Optional.ofNullable(input.getPassword()).ifPresent(user::setPassword);
		Optional.ofNullable(input.getProfileImageUrl()).ifPresent(user::setProfileImageUrl);

		return user;
	}

	private List<String> getGenreIdsIfAreValid(List<String> genreIds) {
		var genreIdsNotFound = genreService.getAllIdsNotFound(genreIds);
		if (genreIdsNotFound.isEmpty()) {
			return genreIds;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Gêneros com os IDs %s não foram encontrados", genreIdsNotFound));
		}
	}

	private List<String> getArtistIdsIfAreValid(List<String> artistIds) {
		var artistIdsNotFound = artistService.getAllIdsNotFound(artistIds);
		if (artistIdsNotFound.isEmpty()) {
			return artistIds;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Artistas com os IDs %s não foram encontrados", artistIdsNotFound));
		}
	}

	public List<UserOutput> toOutput(List<User> users) {
		var outputs = new ArrayList<UserOutput>();
		for (var user : users) {
			outputs.add(toOutput(user));
		}
		return outputs;
	}

	public UserOutput toOutput(User user) {
		return UserOutput.builder()
				.id(user.getId())
				.username(user.getUsername())
				.email(user.getEmail())
				.password(user.getPassword())
				.profileImageUrl(user.getProfileImageUrl())
				.favoriteGenreIds(user.getFavoriteGenreIds())
				.followingArtistIds(user.getFollowingArtistIds())
			   .build();
	}

}
