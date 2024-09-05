package br.com.uuu.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
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
    	languageCreateInputs = List.of(
    			LanguageCreateInput.builder().code("en_US").name("Inglês Americano").build(),
    			LanguageCreateInput.builder().code("pt_BR").name("Português Brasileiro").build(),
    			LanguageCreateInput.builder().code("ja_JP").name("Japonês").build()
    		);
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
	void givenValidLanguageCreateInput_whenPostRequest_thenReturnsCreatedStatusAndLanguageOutput() throws Exception {
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
    void givenEmptyInvalidLanguageCreateInput_whenPostRequest_thenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var response = mockMvc.perform(post("/languages")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{code=não pode ser nulo ou vazio, name=não pode ser nulo ou vazio}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(3)
    void whenGetAllRequest_afterPostRequest_thenReturnsOkStatusAndListOfLanguageOutputs() throws Exception {
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
	@Order(4)
    void givenValidLanguageId_whenGetById_afterPostRequest_thenReturnsOkStatusAndLanguageOutput() throws Exception {
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
	@Order(5)
    void givenInvalidLanguageId_whenGetById_thenReturnsNotFoundStatusAndErrorResponse() throws Exception {
    	var id = "invalid_id";
		var response = mockMvc.perform(get("/languages/get-by-id/{id}", id))
    		.andExpect(status().isNotFound());

    	var errorResponse = ErrorResponse.notFound(String.format("Idioma com o ID %s não foi encontrado", id));
    	checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(6)
    void givenValidLanguageUpdateInput_whenPutRequest_thenReturnsOkStatusAndLanguageOutput() throws Exception {
		var code = "en_GB";
		var name = "Inglês Britânico";
		var languageUpdateInput = LanguageUpdateInput.builder().code(Optional.of(code)).name(Optional.of(name)).build();

        var response = mockMvc.perform(put("/languages/{id}", languages.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(languageUpdateInput)))
            .andExpect(status().isOk());

        var updatedLanguage = languages.get(0);
        updatedLanguage.setCode(code);
        updatedLanguage.setName(name);
        var updatedLanguageOutput = languageOutputs.get(0);
        updatedLanguageOutput.setCode(code);
        updatedLanguageOutput.setName(name);

        checkLanguageOutput(response, updatedLanguageOutput, "$");
    }

	@Test
	@Order(7)
    void givenEmptyValidLanguageUpdateInput_whenPutRequest_thenReturnsOkStatusAndLanguageOutput() throws Exception {
        var languageOutput = languageOutputs.get(0);

        var response = mockMvc.perform(put("/languages/{id}", languages.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isOk());
        checkLanguageOutput(response, languageOutput, "$");
    }

	@Test
	@Order(8)
    void whenGetAllRequest_afterPutRequest_thenReturnsOkStatusAndListOfLanguageOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(9)
    void givenValidGenreId_whenGetById_afterPutRequest_thenReturnsOkStatusAndLanguageOutput() throws Exception {
        getById();
    }

	@Test
	@Order(10)
    void givenValidLanguageId_whenDeleteRequest_thenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/languages/{id}", languages.get(0).getId()))
            .andExpect(status().isOk());

		languages.remove(0);
		languageOutputs.remove(0);
    }

	@Test
	@Order(11)
    void givenInvalidLanguageId_whenDeleteRequest_thenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/languages/{id}", "invalid_id"))
            .andExpect(status().isOk());
    }

	@Test
	@Order(12)
    void whenGetAllRequest_afterDeleteRequest_thenReturnsOkStatusAndListOfLanguageOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(13)
    void givenValidLanguageId_whenGetById_afterDeleteRequest_thenReturnsOkStatusAndLanguageOutput() throws Exception {
        getById();
    }

	@AfterAll
    public void cleanUp() {
        mongoTemplate.getDb().drop();
    }

}
