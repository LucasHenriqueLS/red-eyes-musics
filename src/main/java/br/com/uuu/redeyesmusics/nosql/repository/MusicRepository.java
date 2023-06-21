package br.com.uuu.redeyesmusics.nosql.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.uuu.redeyesmusics.nosql.entity.Music;

public interface MusicRepository extends MongoRepository<Music, String> {}
