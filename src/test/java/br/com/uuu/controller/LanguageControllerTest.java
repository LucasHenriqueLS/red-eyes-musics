package br.com.uuu.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import br.com.uuu.error.exception.ErrorResponse;
import br.com.uuu.json.input.language.LanguageCreateInput;
import br.com.uuu.json.input.language.LanguageUpdateInput;

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
	
	private List<String> languageIds;
	
	private List<LanguageCreateInput> languageCreateInputs;
	
	private List<LanguageUpdateInput> languageUpdateInputs;

    @BeforeAll
    public void setup(){
    	languageIds = new ArrayList<>();
    	languageCreateInputs = new ArrayList<>();
    	languageUpdateInputs = new ArrayList<>();
    	languageCreateInputs.add(
    			LanguageCreateInput.builder()
 				.code("en_US")
 				.name("Inglês Americano")
 				.build());
    	languageCreateInputs.add(
    			LanguageCreateInput.builder()
 				.code("pt_BR")
 				.name("Português Brasileiro")
 				.build());
    	languageCreateInputs.add(
    			LanguageCreateInput.builder()
 				.code("ja_JP")
 				.name("Japonês")
 				.build());
    	languageUpdateInputs.add(
    			LanguageUpdateInput.builder()
        		.code(Optional.of("en_GB"))
        		.name(Optional.of("Inglês Britânico"))
        		.build());
    }
    
    private void checkLanguageOutputExpectedResult(ResultActions response, LanguageCreateInput languageCreateInput, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.id", jsonPath)).isNotEmpty())
        .andExpect(jsonPath(String.format("%s.code", jsonPath)).value(languageCreateInput.getCode()))
        .andExpect(jsonPath(String.format("%s.name", jsonPath)).value(languageCreateInput.getName()));
    }
    
    private void checkLanguageOutputExpectedResult(ResultActions response, LanguageUpdateInput languageCreateInput, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.id", jsonPath)).isNotEmpty())
        .andExpect(jsonPath(String.format("%s.code", jsonPath)).value(languageCreateInput.getCode().get()))
        .andExpect(jsonPath(String.format("%s.name", jsonPath)).value(languageCreateInput.getName().get()));
    }

    private void checkErrorResponseExpectedResult(ResultActions response, ErrorResponse errorResponse, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.status", jsonPath)).value(errorResponse.getStatus()))
        .andExpect(jsonPath(String.format("%s.message", jsonPath)).value(errorResponse.getMessage()))
        .andExpect(jsonPath(String.format("%s.timestamp", jsonPath)).isNotEmpty());
    }

	@Test
	@Order(1)
	void givenValidLanguageCreateInput_whenPostRequest_thenReturnsCreatedStatusAndLanguageOutput() throws Exception {
		for (var languageCreateInput : languageCreateInputs) {
			var response = mockMvc.perform(post("/languages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(languageCreateInput)))
                .andExpect(status().isCreated());
			checkLanguageOutputExpectedResult(response, languageCreateInput, "$");
			
			var jsonResponse = response.andReturn().getResponse().getContentAsString();
			var id = (String) JsonPath.parse(jsonResponse).read("$.id");
			languageIds.add(id);
		}
    }
	
	@Test
	@Order(2)
    void givenEmptyInvalidLanguageCreateInput_whenPostRequest_thenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var response = mockMvc.perform(post("/languages")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
		checkErrorResponseExpectedResult(response, ErrorResponse.badRequest(objectMapper.readValue("{\"code\": \"não pode ser nulo ou vazio\",\"name\": \"não pode ser nulo ou vazio\"}", Object.class)), "$");
    }

	@Test
	@Order(3)
    void whenGetRequest_thenReturnsOkStatusAndListOfLanguageOutputs() throws Exception {
		var response = mockMvc.perform(get("/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(languageCreateInputs.size()));
        
        for (int i = 0; i < languageCreateInputs.size(); i++) {
	        var languageCreateInput = languageCreateInputs.get(i);
	        checkLanguageOutputExpectedResult(response, languageCreateInput, String.format("$[%d]", i));
	    }
    }

	@Test
	@Order(4)
    void givenValidLanguageId_whenGetById_thenReturnsOkStatusAndLanguageOutput() throws Exception {
        for (int i = 0; i < languageCreateInputs.size(); i++) {
        	var languageCreateInput = languageCreateInputs.get(i);
        	var id = languageIds.get(i);
        	var response = mockMvc.perform(get("/languages/get-by-id/{id}", id))
        		.andExpect(status().isOk());
        	checkLanguageOutputExpectedResult(response, languageCreateInput, "$");
        }
    }
	
	@Test
	@Order(5)
    void givenInvalidLanguageId_whenGetById_thenReturnsNotFoundStatusAndErrorResponse() throws Exception {
    	var id = "invalid_id";
    	var response = mockMvc.perform(get("/languages/get-by-id/{id}", id))
    		.andExpect(status().isNotFound());
    	checkErrorResponseExpectedResult(response, ErrorResponse.notFound(String.format("Idioma com o ID %s não foi encontrado", id)), "$");
    }

	@Test
	@Order(6)
    void givenValidLanguageUpdateInput_whenPutRequest_thenReturnsOkStatusAndLanguageOutput() throws Exception {
        var id = languageIds.get(0);
        var languageUpdateInput = languageUpdateInputs.get(0);
        var response = mockMvc.perform(put("/languages/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(languageUpdateInput)))
            .andExpect(status().isOk());
        checkLanguageOutputExpectedResult(response, languageUpdateInput, "$");
    }
	
	@Test
	@Order(7)
    void givenEmptyValidLanguageUpdateInput_whenPutRequest_thenReturnsOkStatusAndLanguageOutput() throws Exception {
        var id = languageIds.get(0);
        var languageUpdateInput = languageUpdateInputs.get(0);
        var response = mockMvc.perform(put("/languages/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isOk());
        checkLanguageOutputExpectedResult(response, languageUpdateInput, "$");
    }

	@Test
	@Order(8)
    void givenValidLanguageId_whenDeleteRequest_thenReturnsOkStatus() throws Exception {
        var id = languageIds.get(2);
		mockMvc.perform(delete("/languages/{id}", id))
            .andExpect(status().isOk());
    }

	@Test
	@Order(9)
    void givenInvalidLanguageId_whenDeleteRequest_thenReturnsOkStatus() throws Exception {
        var id = "invalid_id";
		mockMvc.perform(delete("/languages/{id}", id))
            .andExpect(status().isOk());
    }

//    @Test
//    void whenDeleteLanguage_thenStatus204() throws Exception {
//        // Primeiro, cria um novo idioma para deletar depois
//        String newLanguageJson = "{ \"code\": \"pt\", \"name\": \"Portuguese\" }";
//
//        mockMvc.perform(post("/api/languages")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(newLanguageJson))
//                .andExpect(status().isCreated());
//
//        // Agora, deleta o idioma criado
//        mockMvc.perform(delete("/api/languages/{id}", "pt"))
//                .andExpect(status().isNoContent());
//
//        // Verifica se o idioma foi realmente deletado
//        mockMvc.perform(get("/api/languages/{id}", "pt"))
//                .andExpect(status().isNotFound());
//    }

	@AfterAll
    public void cleanUp() {
//        mongoTemplate.getDb().drop();
		mongoTemplate.getCollection("languages").drop();
    }

}
