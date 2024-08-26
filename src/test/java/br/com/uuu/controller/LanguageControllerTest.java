package br.com.uuu.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
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

import br.com.uuu.json.input.language.LanguageCreateInput;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LanguageControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private List<LanguageCreateInput> languageCreateInputs;

    @BeforeEach
    public void setup(){
    	languageCreateInputs = new ArrayList<>();
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
    }
    
    private void checkExpectedResult(ResultActions response, LanguageCreateInput languageCreateInput, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.id", jsonPath)).isNotEmpty())
        .andExpect(jsonPath(String.format("%s.code", jsonPath)).value(languageCreateInput.getCode()))
        .andExpect(jsonPath(String.format("%s.name", jsonPath)).value(languageCreateInput.getName()));
    }

	@Test
	@Order(1)
	void givenValidLanguageInput_whenPostRequest_shouldReturnsCreatedStatusAndLanguageDetails() throws Exception {
		for (var languageCreateInput : languageCreateInputs) {
			var response = mockMvc.perform(post("/languages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(languageCreateInput)))
                .andExpect(status().isCreated());
			checkExpectedResult(response, languageCreateInput, "$");
		}
    }

	@Test
	@Order(2)
    void whenPostRequest_shouldReturnsOkStatusAndListOfLanguageDetails() throws Exception {
		var response = mockMvc.perform(get("/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(languageCreateInputs.size()));
        
        for (int i = 0; i < languageCreateInputs.size(); i++) {
	        var languageCreateInput = languageCreateInputs.get(i);
	        checkExpectedResult(response, languageCreateInput, String.format("$[%d]", i));
	    }
    }

//	@Test
//    void whenGetLanguageById_thenStatus200() throws Exception {
//        // Primeiro, cria um novo idioma para buscar depois
//        String newLanguageJson = "{ \"code\": \"de\", \"name\": \"German\" }";
//
//        mockMvc.perform(post("/api/languages")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(newLanguageJson))
//                .andExpect(status().isCreated());
//
//        // Agora, busca pelo idioma criado
//        mockMvc.perform(get("/api/languages/{id}", "de"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value("de"))
//                .andExpect(jsonPath("$.name").value("German"));
//    }

	@AfterAll
    public void cleanUp() {
        mongoTemplate.getDb().drop();
//		mongoTemplate.getCollection("languages").drop();
    }

}
