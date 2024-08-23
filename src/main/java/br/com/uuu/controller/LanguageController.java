package br.com.uuu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.json.input.language.LanguageCreateInput;
import br.com.uuu.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("languages")
public class LanguageController {

	@Autowired
	private LanguageService languageService;
	
	@GetMapping
	@Operation(description = "Recupera todos os idiomas")
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(languageService.getAll());
	}

	@PostMapping
	@Operation(description = "Cria um novo idioma")
	public ResponseEntity<?> save(@Valid @RequestBody LanguageCreateInput input) {
		return ResponseEntity.ok(languageService.save(input));
	}

}
