package br.com.uuu.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.uuu.converter.util.GetIdsIfAreValid;
import br.com.uuu.json.input.user.UserCreateInput;
import br.com.uuu.json.input.user.UserUpdateInput;
import br.com.uuu.json.output.user.UserOutput;
import br.com.uuu.model.mongodb.entity.User;

@Component
public class UserConverter {
	
	@Autowired
	private GetIdsIfAreValid getIdsIfAreValid;
	
	public User toEntity(User user, UserCreateInput input) {
		user.setFavoriteGenreIds(getIdsIfAreValid.getGenreIds(input.getFavoriteGenreIds()));
		user.setFollowingArtistIds(getIdsIfAreValid.getArtistIds(input.getFollowingArtistIds()));

		user.setUsername(input.getUsername());
		user.setEmail(input.getEmail());
		user.setPassword(input.getPassword());
		user.setProfileImageUrl(input.getProfileImageUrl());

		return user;
	}

	public User toEntity(User user, UserUpdateInput input) {
		Optional.ofNullable(input.getFavoriteGenreIds()).ifPresent(genreIds -> user.setFavoriteGenreIds(getIdsIfAreValid.getGenreIds(genreIds)));
		Optional.ofNullable(input.getFollowingArtistIds()).ifPresent(artistIds -> user.setFollowingArtistIds(getIdsIfAreValid.getArtistIds(artistIds)));

		Optional.ofNullable(input.getUsername()).ifPresent(user::setUsername);
		Optional.ofNullable(input.getEmail()).ifPresent(user::setEmail);
		Optional.ofNullable(input.getPassword()).ifPresent(user::setPassword);
		Optional.ofNullable(input.getProfileImageUrl()).ifPresent(user::setProfileImageUrl);

		return user;
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
