package br.com.uuu.converter;

import org.springframework.stereotype.Component;

import br.com.uuu.json.input.user.UserCreateInput;
import br.com.uuu.model.mongodb.entity.User;

@Component
public class UserConverter {
	
	public User toEntity(UserCreateInput input) {
		var user = new User();

		user.setEmail(input.getEmail());
		user.setPassword(input.getPassword());

		return user;
	}

}
