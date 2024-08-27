package br.com.uuu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.converter.LanguageConverter;
import br.com.uuu.json.input.language.LanguageCreateInput;
import br.com.uuu.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("languages")
public class LanguageController {

	@Autowired
	private LanguageService languageService;
	
	@Autowired
	private LanguageConverter languageConverter;

	@GetMapping
	@Operation(description = "Recupera todos os idiomas")
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(languageConverter.toOutput(languageService.getAll()));
	}

	@GetMapping("get-by-id/{id}")
	@Operation(description = "Recupera um idioma pelo ID")
	public ResponseEntity<?> getById(@PathVariable String id) {
		return ResponseEntity.ok(languageConverter.toOutput(languageService.getById(id)));
	}

	@PostMapping
	@Operation(description = "Cria um novo idioma")
	public ResponseEntity<?> save(@Valid @RequestBody LanguageCreateInput input) {
		return ResponseEntity.status(HttpStatus.CREATED).body(languageConverter.toOutput(languageService.save(languageConverter.toEntity(input))));
	}

}
