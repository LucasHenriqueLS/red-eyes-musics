package br.com.uuu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uuu.json.input.language.LanguageCreateInput;
import br.com.uuu.json.input.language.LanguageUpdateInput;
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
		return ResponseEntity.ok(languageService.getAllToOutput());
	}

	@GetMapping("get-by-id/{id}")
	@Operation(description = "Recupera um idioma pelo ID")
	public ResponseEntity<?> getById(@PathVariable String id) {
		return ResponseEntity.ok(languageService.getByIdToOutput(id));
	}

	@PostMapping
	@Operation(description = "Cria um novo idioma")
	public ResponseEntity<?> save(@Valid @RequestBody LanguageCreateInput input) {
		return ResponseEntity.status(HttpStatus.CREATED).body(languageService.saveFromInputToOutput(input));
	}

	@PutMapping("{id}")
	@Operation(description = "Atualiza um idioma pelo ID")
	public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody LanguageUpdateInput input) {
		return ResponseEntity.ok(languageService.updateFromInputToOutput(id, input));
	}
	
	@DeleteMapping("{id}")
	@Operation(description = "Deleta um idioma pelo ID")
	public ResponseEntity<?> delete(@PathVariable String id) {
		languageService.delete(id);
		return ResponseEntity.ok().build();
	}

}
