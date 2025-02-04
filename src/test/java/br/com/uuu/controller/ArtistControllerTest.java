package br.com.uuu.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Random;
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

import br.com.uuu.converter.ArtistConverter;
import br.com.uuu.converter.GenreConverter;
import br.com.uuu.error.exception.ErrorResponse;
import br.com.uuu.json.input.artist.ArtistCreateInput;
import br.com.uuu.json.input.artist.ArtistUpdateInput;
import br.com.uuu.json.output.artist.ArtistOutput;
import br.com.uuu.model.mongodb.entity.Artist;
import br.com.uuu.model.mongodb.entity.Genre;
import br.com.uuu.model.mongodb.repository.ArtistRepositoryTest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArtistControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ArtistConverter artistConverter;
	
	@Autowired
	private GenreConverter genreConverter;

	private List<Artist> artists;
	
	private List<ArtistCreateInput> artistCreateInputs;
	
	private List<ArtistOutput> artistOutputs;

	private List<Genre> genres;

    @BeforeAll
    public void setup() throws Exception {
    	setupGenres();
    	setupArtists(genres);
    }

    private void setupGenres() throws Exception {
    	genres = GenreControllerTest.setupGenres(mockMvc, objectMapper, genreConverter);    	
    }

    private void setupArtists(List<Genre> genres) {
    	var random = new Random();	
        artistCreateInputs = ArtistRepositoryTest.getArtists().stream().map(artist -> {
        	return ArtistCreateInput.builder()
        		.names(artist.getNames())
        		.bio(artist.getBio())
        		.genreIds(List.of(genres.get(random.nextInt(2)).getId(), genres.get(random.nextInt(2)).getId()))
        		.imageUrl(artist.getImageUrl())
        	.build();
        }).toList();
    	artists = artistCreateInputs.stream().map(input -> artistConverter.toEntity(new Artist(), input)).collect(Collectors.toList());
    	artistOutputs = artists.stream().map(entity -> artistConverter.toOutput(entity)).collect(Collectors.toList());
    }

    private <T> void checkList(ResultActions response, List<T> list, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s", jsonPath)).isArray())
    	.andExpect(jsonPath(String.format("%s.length()", jsonPath)).value(list.size()));
    	for (int i = 0; i < list.size(); i++) {
    	     response.andExpect(jsonPath(String.format("%s[%d]", jsonPath, i)).value(list.get(i)));
    	}
    }
    
    private void checkArtistOutput(ResultActions response, ArtistOutput artistOutput, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.id", jsonPath)).value(artistOutput.getId()))
    	.andExpect(jsonPath(String.format("%s.bio", jsonPath)).value(artistOutput.getBio()))
    	.andExpect(jsonPath(String.format("%s.imageUrl", jsonPath)).value(artistOutput.getImageUrl()));
    	checkList(response, artistOutput.getNames(), String.format("%s.names", jsonPath));
    	checkList(response, artistOutput.getGenreIds(), String.format("%s.genreIds", jsonPath));
    }

    private void checkErrorResponse(ResultActions response, ErrorResponse errorResponse, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.status", jsonPath)).value(errorResponse.getStatus()))
        .andExpect(jsonPath(String.format("%s.message", jsonPath)).value(errorResponse.getMessage()))
        .andExpect(jsonPath(String.format("%s.timestamp", jsonPath)).isNotEmpty());
    }

	@Test
	@Order(1)
	void givenValidArtistCreateInputWhenPostRequestThenReturnsCreatedStatusAndArtistOutput() throws Exception {
		for (int i = 0; i < artistCreateInputs.size(); i++) {
			var artistCreateInput = artistCreateInputs.get(i);
			var artistOutput = artistOutputs.get(i);
			
			var response = mockMvc.perform(post("/artists")
				    .contentType(MediaType.APPLICATION_JSON)
				    .content(objectMapper.writeValueAsString(artistCreateInput)))
				    .andExpect(status().isCreated());

			var jsonResponse = response.andReturn().getResponse().getContentAsString();
			var id = (String) JsonPath.parse(jsonResponse).read("$.id");
			artists.get(i).setId(id);
			artistOutputs.get(i).setId(id);

			checkArtistOutput(response, artistOutput, "$");
		}
    }

	@Test
	@Order(2)
    void givenInvalidArtistCreateInputWithAllFieldsEmptyWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var response = mockMvc.perform(post("/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{genreIds=não pode ser nulo ou vazio, names=não pode ser nulo ou vazio}");
		checkErrorResponse(response, errorResponse, "$");
    }
	
	@Test
	@Order(3)
    void givenInvalidArtistCreateInputWithAllFieldsInvalidWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var artistCreateInput =
			ArtistCreateInput.builder()
				.names(List.of())
				.genreIds(List.of())
			.build();

		var response = mockMvc.perform(post("/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(artistCreateInput)))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{genreIds=não pode ser nulo ou vazio, names=não pode ser nulo ou vazio}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(4)
    void givenInvalidArtistCreateInputWithInvalidGenreIdsWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var artistUpdateInput =
			ArtistCreateInput.builder()
				.names(artists.get(0).getNames())
				.bio(artists.get(0).getBio())
				.genreIds(List.of("invalid_id_1", "invalid_id_2"))
				.imageUrl(artists.get(0).getImageUrl())
			.build();

		var response = mockMvc.perform(post("/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(artistUpdateInput)))
                .andExpect(status().isNotFound());

		var errorResponse = ErrorResponse.notFound("Gêneros com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(5)
    void whenGetAllRequestAfterPostRequestThenReturnsOkStatusAndListOfArtistOutputs() throws Exception {
		getAll();
    }

	private void getAll() throws Exception {
		var response = mockMvc.perform(get("/artists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(artistOutputs.size()));

        for (int i = 0; i < artistOutputs.size(); i++) {
        	var artistOutput = artistOutputs.get(i);
	        checkArtistOutput(response, artistOutput, String.format("$[%d]", i));
	    }
	}

	@Test
	@Order(6)
    void givenValidArtistIdWhenGetByIdAfterPostRequesThenReturnsOkStatusAndArtistOutput() throws Exception {
        getById();
    }

	private void getById() throws Exception {
		for (var artistOutput : artistOutputs) {
			var response = mockMvc.perform(get("/artists/get-by-id/{id}", artistOutput.getId()))
				.andExpect(status().isOk());
			checkArtistOutput(response, artistOutput, "$");
		}
	}

	@Test
	@Order(7)
    void givenInvalidArtistIdWhenGetByIdThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
    	var id = "invalid_id";
		var response = mockMvc.perform(get("/artists/get-by-id/{id}", id))
    		.andExpect(status().isNotFound());

    	var errorResponse = ErrorResponse.notFound(String.format("Artista com o ID %s não foi encontrado", id));
    	checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(8)
    void givenValidArtistUpdateInputWhenPutRequestThenReturnsOkStatusAndArtistOutput() throws Exception {
		var artistUpdateInput =
			ArtistUpdateInput.builder()
				.names(List.of("Michael Jackson", "Michael Joseph Jackson", "MJ", "King of Pop", "The Gloved One"))
				.bio("Michael Jackson foi um artista multifacetado, conhecido por redefinir o cenário da música pop com seus movimentos de dança icônicos, videoclipes revolucionários e uma voz única.")
				.genreIds(List.of(genres.get(1).getId(), genres.get(2).getId()))
				.imageUrl("https://example.com/images/michael_joseph_jackson.jpg")
			.build();

        var response = mockMvc.perform(put("/artists/{id}", artists.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(artistUpdateInput)))
            .andExpect(status().isOk());

        artistConverter.toEntity(artists.get(0), artistUpdateInput);
        var updatedArtistOutput = artistConverter.toOutput(artists.get(0));
        artistOutputs.set(0, updatedArtistOutput);

        checkArtistOutput(response, updatedArtistOutput, "$");
    }

	@Test
	@Order(9)
    void givenValidArtistUpdateInputWithAllFieldsEmptyWhenPutRequestThenReturnsOkStatusAndArtistOutput() throws Exception {
        var artistOutput = artistOutputs.get(0);

        var response = mockMvc.perform(put("/artists/{id}", artists.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isOk());
        checkArtistOutput(response, artistOutput, "$");
    }

	@Test
	@Order(10)
    void givenInvalidArtistUpdateInputWithAllFieldsInvalidWhenPutRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var artistUpdateInput =
			ArtistUpdateInput.builder()
				.names(List.of())
				.genreIds(List.of())
			.build();

        var response = mockMvc.perform(put("/artists/{id}", artists.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(artistUpdateInput)))
            .andExpect(status().isBadRequest());

        var errorResponse = ErrorResponse.badRequest("{genreIds=não pode ser vazio se não for nulo, names=não pode ser vazio se não for nulo}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(11)
    void givenInvalidArtistUpdateInputWithInvalidGenreIdsWhenPutRequestThenReturnsOkStatusAndArtistOutput() throws Exception {
		var artistUpdateInput =
			ArtistUpdateInput.builder()
				.genreIds(List.of("invalid_id_1","invalid_id_2"))
			.build();

        var response = mockMvc.perform(put("/artists/{id}", artists.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(artistUpdateInput)))
            .andExpect(status().isNotFound());

        var errorResponse = ErrorResponse.notFound("Gêneros com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(12)
    void whenGetAllRequestAfterPutRequestThenReturnsOkStatusAndListOfArtistOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(13)
    void givenValidGenreIdWhenGetByIdAfterPutRequestThenReturnsOkStatusAndArtistOutput() throws Exception {
        getById();
    }

	@Test
	@Order(14)
    void givenValidArtistIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/artists/{id}", artists.get(0).getId()))
            .andExpect(status().isOk());

		artists.remove(0);
		artistOutputs.remove(0);
    }

	@Test
	@Order(15)
    void givenInvalidArtistIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/artists/{id}", "invalid_id"))
            .andExpect(status().isOk());
    }

	@Test
	@Order(16)
    void whenGetAllRequestAfterDeleteRequestThenReturnsOkStatusAndListOfArtistOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(17)
    void givenValidArtistIdWhenGetByIdAfterDeleteRequestThenReturnsOkStatusAndArtistOutput() throws Exception {
        getById();
    }

	@AfterAll
    public void cleanUp() {
        mongoTemplate.getDb().drop();
    }

	public static List<Artist> setupArtist(MockMvc mockMvc, ObjectMapper objectMapper, ArtistConverter artistConverter, List<Genre> genres) throws Exception {
		var artistgenreControllerTest = new ArtistControllerTest();
		return artistgenreControllerTest.createArtist(mockMvc, objectMapper, artistConverter, genres);
	}

	private List<Artist> createArtist(MockMvc mockMvc, ObjectMapper objectMapper, ArtistConverter artistConverter, List<Genre> genres) throws Exception {
		this.artistConverter = artistConverter;
		this.mockMvc = mockMvc;
		this.objectMapper = objectMapper;
		setupArtists(genres);
		givenValidArtistCreateInputWhenPostRequestThenReturnsCreatedStatusAndArtistOutput();
		return artists;
	}

}
