package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.converter.UserConverter;
import br.com.uuu.json.dto.user.UserIdDTO;
import br.com.uuu.json.input.user.UserCreateInput;
import br.com.uuu.model.mongodb.entity.User;
import br.com.uuu.model.mongodb.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserConverter userConverter;

	public List<User> getAll() {
		return userRepository.findAll();
	}

	public User save(UserCreateInput input) {
		return userRepository.save(userConverter.toEntity(input));
	}

	public Boolean existsById(String id) {
		return userRepository.existsById(id);
	}

	public List<String> getAllIdsNotFound(List<String> ids) {
		var allIdsFound = userRepository.findAllByIdIn(ids).stream().map(UserIdDTO::id).toList();
        return ids.stream().filter(id -> !allIdsFound.contains(id)).toList();
	}

}
