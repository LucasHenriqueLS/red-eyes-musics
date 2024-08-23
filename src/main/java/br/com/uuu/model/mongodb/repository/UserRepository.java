package br.com.uuu.model.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.model.mongodb.entity.User;

public interface UserRepository extends MongoRepository<User, String> { }
