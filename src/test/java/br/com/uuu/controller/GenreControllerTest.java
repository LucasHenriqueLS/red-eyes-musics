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

import br.com.uuu.converter.GenreConverter;
import br.com.uuu.error.exception.ErrorResponse;
import br.com.uuu.json.input.genre.GenreCreateInput;
import br.com.uuu.json.input.genre.GenreUpdateInput;
import br.com.uuu.json.output.genre.GenreOutput;
import br.com.uuu.model.mongodb.entity.Genre;
import br.com.uuu.model.mongodb.repository.GenreRepositoryTest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenreControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private GenreConverter genreConverter;

	private List<GenreCreateInput> genreCreateInputs;

	private List<Genre> genres;

    private List<GenreOutput> genreOutputs;

    @BeforeAll
    public void setup(){
    	setupGenres();
    }

    private void setupGenres() {
    	genreCreateInputs = GenreRepositoryTest.getGenres().stream().map(genre -> {
        	return GenreCreateInput.builder()
        		.name(genre.getName())
        		.description(genre.getDescription())
        	.build();
        }).toList();
    	genres = genreCreateInputs.stream().map(input -> genreConverter.toEntity(new Genre(), input)).collect(Collectors.toList());
    	genreOutputs = genres.stream().map(entity -> genreConverter.toOutput(entity)).collect(Collectors.toList());
    }

    private void checkGenreOutput(ResultActions response, GenreOutput genreOutput, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.id", jsonPath)).value(genreOutput.getId()))
    	.andExpect(jsonPath(String.format("%s.name", jsonPath)).value(genreOutput.getName()))
        .andExpect(jsonPath(String.format("%s.description", jsonPath)).value(genreOutput.getDescription()));
    }

    private void checkErrorResponse(ResultActions response, ErrorResponse errorResponse, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.status", jsonPath)).value(errorResponse.getStatus()))
        .andExpect(jsonPath(String.format("%s.message", jsonPath)).value(errorResponse.getMessage()))
        .andExpect(jsonPath(String.format("%s.timestamp", jsonPath)).isNotEmpty());
    }

	@Test
	@Order(1)
	void givenValidGenreCreateInputWhenPostRequestThenReturnsCreatedStatusAndGenreOutput() throws Exception {
		for (int i = 0; i < genreCreateInputs.size(); i++) {
			var genreCreateInput = genreCreateInputs.get(i);
			var genreOutput = genreOutputs.get(i);
			
			var response = mockMvc.perform(post("/genres")
				    .contentType(MediaType.APPLICATION_JSON)
				    .content(objectMapper.writeValueAsString(genreCreateInput)))
				    .andExpect(status().isCreated());

			var jsonResponse = response.andReturn().getResponse().getContentAsString();
			var id = (String) JsonPath.parse(jsonResponse).read("$.id");
			genres.get(i).setId(id);
			genreOutputs.get(i).setId(id);

			checkGenreOutput(response, genreOutput, "$");
		}
    }

	@Test
	@Order(2)
    void givenInvalidGenreCreateInputWithAllFieldsEmptyWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var response = mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{name=não pode ser nulo, vazio ou conter somente espaços em branco}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(3)
    void givenInvalidGenreCreateInputWithAllFieldsInvalidWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var genreUpdateInput =
			GenreCreateInput.builder()
				.name(" ")
			.build();

		var response = mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(genreUpdateInput)))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{name=não pode ser nulo, vazio ou conter somente espaços em branco}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(4)
    void whenGetAllRequestAfterPostRequestThenReturnsOkStatusAndListOfGenreOutputs() throws Exception {
		getAll();
    }

	private void getAll() throws Exception {
		var response = mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(genreOutputs.size()));

        for (int i = 0; i < genreOutputs.size(); i++) {
        	var genreOutput = genreOutputs.get(i);
	        checkGenreOutput(response, genreOutput, String.format("$[%d]", i));
	    }
	}

	@Test
	@Order(5)
    void givenValidGenreIdWhenGetByIdAfterPostRequestThenReturnsOkStatusAndGenreOutput() throws Exception {
        getById();
    }

	private void getById() throws Exception {
		for (var genreOutput : genreOutputs) {
			var response = mockMvc.perform(get("/genres/get-by-id/{id}", genreOutput.getId()))
				.andExpect(status().isOk());
			checkGenreOutput(response, genreOutput, "$");
		}
	}

	@Test
	@Order(6)
    void givenInvalidGenreIdWhenGetByIdThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
    	var id = "invalid_id";
		var response = mockMvc.perform(get("/genres/get-by-id/{id}", id))
    		.andExpect(status().isNotFound());

    	var errorResponse = ErrorResponse.notFound(String.format("Gênero com o ID %s não foi encontrado", id));
    	checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(7)
    void givenValidGenreUpdateInputWhenPutRequestThenReturnsOkStatusAndGenreOutput() throws Exception {
		var genreUpdateInput =
			GenreUpdateInput.builder()
				.name("Pop")
				.description("O gênero Pop evoluiu ao longo das décadas, incorporando novos estilos musicais e tecnologias, mantendo sua essência acessível e cativante para o público em massa.")
			.build();

        var response = mockMvc.perform(put("/genres/{id}", genres.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(genreUpdateInput)))
            .andExpect(status().isOk());

        genreConverter.toEntity(genres.get(0), genreUpdateInput);
        var updatedGenreOutput = genreConverter.toOutput(genres.get(0));
        genreOutputs.set(0, updatedGenreOutput);

        checkGenreOutput(response, updatedGenreOutput, "$");
    }

	@Test
	@Order(8)
    void givenValidLanguageUpdateInputWithAllFieldsEmptyWhenPutRequestThenReturnsOkStatusAndLanguageOutput() throws Exception {
        var genreOutput = genreOutputs.get(0);

        var response = mockMvc.perform(put("/genres/{id}", genres.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isOk());
        checkGenreOutput(response, genreOutput, "$");
    }
	
	@Test
	@Order(9)
    void givenInvalidGenreUpdateInputWithAllFieldsInvalidWhenPutRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var genreUpdateInput =
			GenreUpdateInput.builder()
				.name(" ")
			.build();

        var response = mockMvc.perform(put("/genres/{id}", genres.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(genreUpdateInput)))
            .andExpect(status().isBadRequest());

        var errorResponse = ErrorResponse.badRequest("{name=não pode ser vazio ou conter somente espaços em branco se não for nulo}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(9)
    void whenGetRequestAfterPutRequestThenReturnsOkStatusAndListOfGenreOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(10)
    void givenValidGenreIdWhenGetByIdAfterPutRequestThenReturnsOkStatusAndGenreOutput() throws Exception {
        getById();
    }

	@Test
	@Order(11)
    void givenValidGenreIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/genres/{id}", genres.get(0).getId()))
            .andExpect(status().isOk());

		genres.remove(0);
        genreOutputs.remove(0);
    }

	@Test
	@Order(11)
    void givenInvalidGenreIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/genres/{id}", "invalid_id"))
            .andExpect(status().isOk());
    }

	@Test
	@Order(12)
    void whenGetRequestAfterDeleteRequestThenReturnsOkStatusAndListOfGenreOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(13)
    void givenValidGenreIdWhenGetByIdAfterDeleteRequestThenReturnsOkStatusAndGenreOutput() throws Exception {
        getById();
    }

	@AfterAll
    public void cleanUp() {
        mongoTemplate.getDb().drop();
    }

	public static List<Genre> setupGenres(MockMvc mockMvc, ObjectMapper objectMapper, GenreConverter genreConverter) throws Exception {
		var genreControllerTest = new GenreControllerTest();
		return genreControllerTest.createGenres(mockMvc, objectMapper, genreConverter);
	}

	private List<Genre> createGenres(MockMvc mockMvc, ObjectMapper objectMapper, GenreConverter genreConverter) throws Exception {
		this.genreConverter = genreConverter;
		this.mockMvc = mockMvc;
		this.objectMapper = objectMapper;
		setupGenres();
		givenValidGenreCreateInputWhenPostRequestThenReturnsCreatedStatusAndGenreOutput();
		return genres;
	}

}
