package br.com.uuu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.converter.UserConverter;
import br.com.uuu.json.dto.IdDTO;
import br.com.uuu.json.input.user.UserCreateInput;
import br.com.uuu.json.input.user.UserUpdateInput;
import br.com.uuu.json.output.user.UserOutput;
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

	public List<UserOutput> getAllToOutput() {
		return userConverter.toOutput(getAll());
	}

	public User getById(String id) {
		return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Usuário com o ID %s não foi encontrado", id)));
	}

	public UserOutput getByIdToOutput(String id) {
		return userConverter.toOutput(getById(id));
	}

	public Boolean existsById(String id) {
		return userRepository.existsById(id);
	}

	public List<String> getAllIdsNotFound(List<String> ids) {
		var allIdsFound = userRepository.findAllByIdIn(ids).stream().map(IdDTO::id).toList();
		return ids.stream().filter(id -> !allIdsFound.contains(id)).toList();
	}

	public User save(User user) {
		return userRepository.save(user);
	}

	public UserOutput saveFromInputToOutput(UserCreateInput input) {
		var user = new User();
		return userConverter.toOutput(save(userConverter.toEntity(user, input)));
	}

	public UserOutput updateFromInputToOutput(String id, UserUpdateInput input) {
		var user = getById(id);
		return userConverter.toOutput(save(userConverter.toEntity(user, input)));
	}

	public void delete(String id) {
		userRepository.deleteById(id);
	}

}
