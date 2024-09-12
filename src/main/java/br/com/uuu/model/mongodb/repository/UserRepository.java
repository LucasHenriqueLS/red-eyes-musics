package br.com.uuu.model.mongodb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.json.dto.user.UserIdDTO;
import br.com.uuu.model.mongodb.entity.User;

public interface UserRepository extends MongoRepository<User, String> {

	List<UserIdDTO> findAllByIdIn(List<String> ids);

}
