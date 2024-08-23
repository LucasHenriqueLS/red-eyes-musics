package br.com.uuu.model.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.model.mongodb.entity.Language;

public interface LanguageRepository extends MongoRepository<Language, String> { }
