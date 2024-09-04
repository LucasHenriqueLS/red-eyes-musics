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

import br.com.uuu.converter.GenreConverter;
import br.com.uuu.error.exception.ErrorResponse;
import br.com.uuu.json.input.genre.GenreCreateInput;
import br.com.uuu.json.input.genre.GenreUpdateInput;
import br.com.uuu.json.output.genre.GenreOutput;
import br.com.uuu.model.mongodb.entity.Genre;

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

	private List<Genre> genres;

    private List<GenreCreateInput> genreCreateInputs;

    private List<GenreOutput> genreOutputs;

    @BeforeAll
    public void setup(){
    	genreCreateInputs = List.of(
    			GenreCreateInput.builder().name("Clássica").description("Música de concerto, chamada popularmente de música clássica ou música erudita, é a principal variedade de música produzida ou enraizada nas tradições da música secular e litúrgica ocidental.").build(),
    			GenreCreateInput.builder().name("Pop").description("A música pop é um gênero da música popular que se originou durante a década de 1950 nos Estados Unidos e Reino Unido.").build(),
    			GenreCreateInput.builder().name("J-Rock").description("Rock japonês, também conhecido pela abreviatura J-rock é a música rock proveniente do Japão.").build()
    		);
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
	void givenValidGenreCreateInput_whenPostRequest_thenReturnsCreatedStatusAndGenreOutput() throws Exception {
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
    void givenEmptyInvalidGenreCreateInput_whenPostRequest_thenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var response = mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("[name: não pode ser nulo ou vazio]");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(3)
    void whenGetAllRequest_afterPostRequest_thenReturnsOkStatusAndListOfGenreOutputs() throws Exception {
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
	@Order(4)
    void givenValidGenreId_whenGetById_afterPostRequest_thenReturnsOkStatusAndGenreOutput() throws Exception {
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
	@Order(5)
    void givenInvalidGenreId_whenGetById_thenReturnsNotFoundStatusAndErrorResponse() throws Exception {
    	var id = "invalid_id";
		var response = mockMvc.perform(get("/genres/get-by-id/{id}", id))
    		.andExpect(status().isNotFound());

    	var errorResponse = ErrorResponse.notFound(String.format("Gênero com o ID %s não foi encontrado", id));
    	checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(6)
    void givenValidGenreUpdateInput_whenPutRequest_thenReturnsOkStatusAndGenreOutput() throws Exception {
		var name = "Rock";
		var description = "A música pop é um gênero da música popular que se originou durante a década de 1950 nos Estados Unidos e Reino Unido.";
		var genreUpdateInput = GenreUpdateInput.builder().name(Optional.of(name)).description(Optional.of(description)).build();

        var response = mockMvc.perform(put("/genres/{id}", genres.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(genreUpdateInput)))
            .andExpect(status().isOk());

        var updatedGenre = genres.get(0);
        updatedGenre.setName(name);
        updatedGenre.setDescription(description);
        var updatedGenreOutput = genreOutputs.get(0);
        updatedGenreOutput.setName(name);
        updatedGenreOutput.setDescription(description);

        checkGenreOutput(response, updatedGenreOutput, "$");
    }

	@Test
	@Order(7)
    void givenEmptyValidLanguageUpdateInput_whenPutRequest_thenReturnsOkStatusAndLanguageOutput() throws Exception {
        var genreOutput = genreOutputs.get(0);

        var response = mockMvc.perform(put("/genres/{id}", genres.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isOk());
        checkGenreOutput(response, genreOutput, "$");
    }

	@Test
	@Order(8)
    void whenGetRequest_afterPutRequest_thenReturnsOkStatusAndListOfGenreOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(9)
    void givenValidGenreId_whenGetById_afterPutRequest_thenReturnsOkStatusAndGenreOutput() throws Exception {
        getById();
    }

	@Test
	@Order(10)
    void givenValidGenreId_whenDeleteRequest_thenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/genres/{id}", genres.get(0).getId()))
            .andExpect(status().isOk());

		genres.remove(0);
        genreOutputs.remove(0);
    }

	@Test
	@Order(11)
    void givenInvalidGenreId_whenDeleteRequest_thenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/genres/{id}", "invalid_id"))
            .andExpect(status().isOk());
    }

	@Test
	@Order(12)
    void whenGetRequest_afterDeleteRequest_thenReturnsOkStatusAndListOfGenreOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(13)
    void givenValidGenreId_whenGetById_afterDeleteRequest_thenReturnsOkStatusAndGenreOutput() throws Exception {
        getById();
    }

	@AfterAll
    public void cleanUp() {
        mongoTemplate.getDb().drop();
    }

}
