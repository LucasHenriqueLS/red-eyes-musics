package br.com.uuu.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import br.com.uuu.converter.LanguageConverter;
import br.com.uuu.error.exception.ErrorResponse;
import br.com.uuu.json.input.language.LanguageCreateInput;
import br.com.uuu.json.input.language.LanguageUpdateInput;
import br.com.uuu.json.output.language.LanguageOutput;
import br.com.uuu.model.mongodb.entity.Language;
import br.com.uuu.model.mongodb.repository.LanguageRepositoryTest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LanguageControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private LanguageConverter languageConverter;

	private List<Language> languages;
	
	private List<LanguageCreateInput> languageCreateInputs;
	
	private List<LanguageOutput> languageOutputs;

    @BeforeAll
    public void setup(){
    	setupLanguage();
    }

    private void setupLanguage() {
    	languageCreateInputs = LanguageRepositoryTest.getLanguages().stream().map(language -> {
        	return LanguageCreateInput.builder()
        		.code(language.getCode())
        		.name(language.getName())
        	.build();
        }).toList();
    	languages = languageCreateInputs.stream().map(input -> languageConverter.toEntity(new Language(), input)).collect(Collectors.toList());
    	languageOutputs = languages.stream().map(entity -> languageConverter.toOutput(entity)).collect(Collectors.toList());
    }

    private void checkLanguageOutput(ResultActions response, LanguageOutput languageOutput, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.id", jsonPath)).value(languageOutput.getId()))
        .andExpect(jsonPath(String.format("%s.code", jsonPath)).value(languageOutput.getCode()))
        .andExpect(jsonPath(String.format("%s.name", jsonPath)).value(languageOutput.getName()));
    }

    private void checkErrorResponse(ResultActions response, ErrorResponse errorResponse, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.status", jsonPath)).value(errorResponse.getStatus()))
        .andExpect(jsonPath(String.format("%s.message", jsonPath)).value(errorResponse.getMessage()))
        .andExpect(jsonPath(String.format("%s.timestamp", jsonPath)).isNotEmpty());
    }

	@Test
	@Order(1)
	void givenValidLanguageCreateInputWhenPostRequestThenReturnsCreatedStatusAndLanguageOutput() throws Exception {
		for (int i = 0; i < languageCreateInputs.size(); i++) {
			var languageCreateInput = languageCreateInputs.get(i);
			var languageOutput = languageOutputs.get(i);

			var response = mockMvc.perform(post("/languages")
				    .contentType(MediaType.APPLICATION_JSON)
				    .content(objectMapper.writeValueAsString(languageCreateInput)))
				    .andExpect(status().isCreated());

			var jsonResponse = response.andReturn().getResponse().getContentAsString();
			var id = (String) JsonPath.parse(jsonResponse).read("$.id");
			languages.get(i).setId(id);
			languageOutputs.get(i).setId(id);

			checkLanguageOutput(response, languageOutput, "$");
		}
    }

	@Test
	@Order(2)
    void givenInvalidLanguageCreateInputWithAllFieldsEmptyWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var response = mockMvc.perform(post("/languages")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{code=não pode ser nulo, vazio ou conter somente espaços em branco, name=não pode ser nulo, vazio ou conter somente espaços em branco}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(3)
    void givenInvalidLanguageCreateInputWithAllFieldsInvalidWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var languageCreateInput =
			LanguageCreateInput.builder()
				.code(" ")
				.name(" ")
			.build();

		var response = mockMvc.perform(post("/languages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(languageCreateInput)))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{code=não pode ser nulo, vazio ou conter somente espaços em branco, name=não pode ser nulo, vazio ou conter somente espaços em branco}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(4)
    void whenGetAllRequestAfterPostRequestThenReturnsOkStatusAndListOfLanguageOutputs() throws Exception {
		getAll();
    }

	private void getAll() throws Exception {
		var response = mockMvc.perform(get("/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(languageOutputs.size()));

        for (int i = 0; i < languageOutputs.size(); i++) {
        	var languageOutput = languageOutputs.get(i);
	        checkLanguageOutput(response, languageOutput, String.format("$[%d]", i));
	    }
	}
	
	@Test
	@Order(5)
    void givenValidLanguageIdWhenGetByIdAfterPostRequestThenReturnsOkStatusAndLanguageOutput() throws Exception {
        getById();
    }

	private void getById() throws Exception {
		for (var languageOutput : languageOutputs) {
			var response = mockMvc.perform(get("/languages/get-by-id/{id}", languageOutput.getId()))
				.andExpect(status().isOk());
			checkLanguageOutput(response, languageOutput, "$");
		}
	}

	@Test
	@Order(6)
    void givenInvalidLanguageIdWhenGetByIdThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
    	var id = "invalid_id";
		var response = mockMvc.perform(get("/languages/get-by-id/{id}", id))
    		.andExpect(status().isNotFound());

    	var errorResponse = ErrorResponse.notFound(String.format("Idioma com o ID %s não foi encontrado", id));
    	checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(7)
    void givenValidLanguageUpdateInputWhenPutRequestThenReturnsOkStatusAndLanguageOutput() throws Exception {
		var languageUpdateInput =
			LanguageUpdateInput.builder()
				.code("en_GB")
				.name("Inglês Britânico")
			.build();

        var response = mockMvc.perform(put("/languages/{id}", languages.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(languageUpdateInput)))
            .andExpect(status().isOk());
        
        languageConverter.toEntity(languages.get(0), languageUpdateInput);
        var updatedLanguageOutput = languageConverter.toOutput(languages.get(0));
        languageOutputs.set(0, updatedLanguageOutput);

        checkLanguageOutput(response, updatedLanguageOutput, "$");
    }

	@Test
	@Order(8)
    void givenValidLanguageUpdateInputWithAllFieldsEmptyWhenPutRequestThenReturnsOkStatusAndLanguageOutput() throws Exception {
        var languageOutput = languageOutputs.get(0);

        var response = mockMvc.perform(put("/languages/{id}", languages.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isOk());
        checkLanguageOutput(response, languageOutput, "$");
    }

	@Test
	@Order(9)
    void givenInvalidLanguageUpdateInputWithAllFieldsInvalidWhenPutRequestThenReturnsOkStatusAndLanguageOutput() throws Exception {
		var languageUpdateInput =
			LanguageUpdateInput.builder()
				.code(" ")
				.name(" ")
			.build();

        var response = mockMvc.perform(put("/languages/{id}", languages.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(languageUpdateInput)))
            .andExpect(status().isBadRequest());
        
        var errorResponse = ErrorResponse.badRequest("{code=não pode ser vazio ou conter somente espaços em branco se não for nulo, name=não pode ser vazio ou conter somente espaços em branco se não for nulo}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(10)
    void whenGetAllRequestAfterPutRequestThenReturnsOkStatusAndListOfLanguageOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(11)
    void givenValidGenreIdWhenGetById_afterPutRequestThenReturnsOkStatusAndLanguageOutput() throws Exception {
        getById();
    }

	@Test
	@Order(12)
    void givenValidLanguageIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/languages/{id}", languages.get(0).getId()))
            .andExpect(status().isOk());

		languages.remove(0);
		languageOutputs.remove(0);
    }

	@Test
	@Order(13)
    void givenInvalidLanguageIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/languages/{id}", "invalid_id"))
            .andExpect(status().isOk());
    }

	@Test
	@Order(14)
    void whenGetAllRequestAfterDeleteRequestThenReturnsOkStatusAndListOfLanguageOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(15)
    void givenValidLanguageIdWhenGetByIdAfterDeleteRequestThenReturnsOkStatusAndLanguageOutput() throws Exception {
        getById();
    }

	@AfterAll
    public void cleanUp() {
        mongoTemplate.getDb().drop();
    }

}
