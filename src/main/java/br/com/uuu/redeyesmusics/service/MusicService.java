package br.com.uuu.redeyesmusics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.uuu.redeyesmusics.converter.MusicConverter;
import br.com.uuu.redeyesmusics.dto.input.MusicInput;
import br.com.uuu.redeyesmusics.nosql.entity.Music;
import br.com.uuu.redeyesmusics.nosql.repository.MusicRepository;

@Service
public class MusicService {

	@Autowired
	private MusicRepository musicRepository;
	
	@Autowired
	private MusicConverter musicConverter;
	
	public Music save(MusicInput input) {
		return musicRepository.save(musicConverter.toEntity(input));
	}
}
