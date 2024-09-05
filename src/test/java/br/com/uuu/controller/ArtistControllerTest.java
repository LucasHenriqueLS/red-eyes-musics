package br.com.uuu.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import br.com.uuu.converter.ArtistConverter;
import br.com.uuu.converter.GenreConverter;
import br.com.uuu.error.exception.ErrorResponse;
import br.com.uuu.json.input.artist.ArtistCreateInput;
import br.com.uuu.json.input.genre.GenreCreateInput;
import br.com.uuu.json.output.artist.ArtistOutput;
import br.com.uuu.json.output.genre.GenreOutput;
import br.com.uuu.model.mongodb.entity.Artist;
import br.com.uuu.model.mongodb.entity.Genre;

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
	
	private List<GenreCreateInput> genreCreateInputs;

	private List<Genre> genres;

    private List<GenreOutput> genreOutputs;

    @BeforeAll
    public void setup() throws Exception{
    	genreCreateInputs = List.of(
    			GenreCreateInput.builder().name("Clássica").description("Música de concerto, chamada popularmente de música clássica ou música erudita, é a principal variedade de música produzida ou enraizada nas tradições da música secular e litúrgica ocidental.").build(),
    			GenreCreateInput.builder().name("Pop").description("A música pop é um gênero da música popular que se originou durante a década de 1950 nos Estados Unidos e Reino Unido.").build(),
    			GenreCreateInput.builder().name("J-Rock").description("Rock japonês, também conhecido pela abreviatura J-rock é a música rock proveniente do Japão.").build()
    		);
    	genres = genreCreateInputs.stream().map(input -> genreConverter.toEntity(new Genre(), input)).collect(Collectors.toList());
        genreOutputs = genres.stream().map(entity -> genreConverter.toOutput(entity)).collect(Collectors.toList());
        createGenres();

        artistCreateInputs = List.of(
    			ArtistCreateInput.builder().names(List.of("Michael Jackson", "Michael Joseph Jackson")).bio("Michael Jackson foi um cantor, compositor e dançarino norte-americano, amplamente considerado o Rei do Pop e um dos artistas mais influentes da história da música.").genreIds(List.of(genres.get(0).getId(), genres.get(1).getId())).imageUrl("https://example.com/images/michael_jackson.jpg").build(),
    			ArtistCreateInput.builder().names(List.of("Ariana Grande", "Ariana Grande-Butera")).bio("Ariana Grande é uma cantora e atriz norte-americana reconhecida por sua poderosa voz e influente presença na cultura pop.").genreIds(List.of(genres.get(0).getId(), genres.get(2).getId())).imageUrl("https://example.com/images/ariana_grande.jpg").build(),
    			ArtistCreateInput.builder().names(List.of("Hans Zimmer", "Hans Florian Zimmer")).bio("Hans Zimmer é um renomado compositor de trilhas sonoras para cinema, famoso por seu trabalho em filmes como 'O Rei Leão' e 'Duna'.").genreIds(List.of(genres.get(0).getId(), genres.get(2).getId())).imageUrl("https://example.com/images/hans_zimmer.jpg").build()
    		);
    	artists = artistCreateInputs.stream().map(input -> artistConverter.toEntity(new Artist(), input)).collect(Collectors.toList());
    	artistOutputs = artists.stream().map(entity -> artistConverter.toOutput(entity)).collect(Collectors.toList());
    }

    private void createGenres() throws Exception {
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
			
			response
			.andExpect(jsonPath("$.id").value(genreOutput.getId()))
	    	.andExpect(jsonPath("$.name").value(genreOutput.getName()))
	        .andExpect(jsonPath("$.description").value(genreOutput.getDescription()));
		}
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
	void givenValidArtistCreateInput_whenPostRequest_thenReturnsCreatedStatusAndArtistOutput() throws Exception {
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
    void givenInvalidArtistCreateInput_becauseIsEmpty_whenPostRequest_thenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var response = mockMvc.perform(post("/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{names=não pode ser nulo ou vazio, genreIds=não pode ser nulo ou vazio}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(3)
    void givenInvalidArtistCreateInput_forHavingInvalidGenreIds_whenPostRequest_thenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var artistUpdateInput = ArtistCreateInput.builder().names(List.of("Michael Jackson", "Michael Joseph Jackson")).bio("Michael Jackson foi um cantor, compositor e dançarino norte-americano, amplamente considerado o Rei do Pop e um dos artistas mais influentes da história da música.").genreIds(List.of("invalid_id_1","invalid_id_2")).imageUrl("https://example.com/images/michael_jackson.jpg").build();
		
		var response = mockMvc.perform(post("/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(artistUpdateInput)))
                .andExpect(status().isNotFound());

		var errorResponse = ErrorResponse.notFound("Gêneros com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(4)
    void whenGetAllRequest_afterPostRequest_thenReturnsOkStatusAndListOfArtistOutputs() throws Exception {
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
	@Order(5)
    void givenValidArtistId_whenGetById_afterPostRequest_thenReturnsOkStatusAndArtistOutput() throws Exception {
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
	@Order(6)
    void givenInvalidArtistId_whenGetById_thenReturnsNotFoundStatusAndErrorResponse() throws Exception {
    	var id = "invalid_id";
		var response = mockMvc.perform(get("/artists/get-by-id/{id}", id))
    		.andExpect(status().isNotFound());

    	var errorResponse = ErrorResponse.notFound(String.format("Artista com o ID %s não foi encontrado", id));
    	checkErrorResponse(response, errorResponse, "$");
    }

//	@Test
//	@Order(6)
//    void givenValidArtistUpdateInput_whenPutRequest_thenReturnsOkStatusAndArtistOutput() throws Exception {
//		var code = "en_GB";
//		var name = "Inglês Britânico";
//		var artistUpdateInput = ArtistUpdateInput.builder().code(Optional.of(code)).name(Optional.of(name)).build();
//
//        var response = mockMvc.perform(put("/artists/{id}", artists.get(0).getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(artistUpdateInput)))
//            .andExpect(status().isOk());
//
//        var updatedArtist = artists.get(0);
//        updatedArtist.setCode(code);
//        updatedArtist.setName(name);
//        var updatedArtistOutput = artistOutputs.get(0);
//        updatedArtistOutput.setCode(code);
//        updatedArtistOutput.setName(name);
//
//        checkArtistOutput(response, updatedArtistOutput, "$");
//    }
//
//	@Test
//	@Order(7)
//    void givenEmptyValidArtistUpdateInput_whenPutRequest_thenReturnsOkStatusAndArtistOutput() throws Exception {
//        var artistOutput = artistOutputs.get(0);
//
//        var response = mockMvc.perform(put("/artists/{id}", artists.get(0).getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .content("{}"))
//            .andExpect(status().isOk());
//        checkArtistOutput(response, artistOutput, "$");
//    }
//
//	@Test
//	@Order(8)
//    void whenGetAllRequest_afterPutRequest_thenReturnsOkStatusAndListOfArtistOutputs() throws Exception {
//		getAll();
//    }
//
//	@Test
//	@Order(9)
//    void givenValidGenreId_whenGetById_afterPutRequest_thenReturnsOkStatusAndArtistOutput() throws Exception {
//        getById();
//    }
//
//	@Test
//	@Order(10)
//    void givenValidArtistId_whenDeleteRequest_thenReturnsOkStatus() throws Exception {
//		mockMvc.perform(delete("/artists/{id}", artists.get(0).getId()))
//            .andExpect(status().isOk());
//
//		artists.remove(0);
//		artistOutputs.remove(0);
//    }
//
//	@Test
//	@Order(11)
//    void givenInvalidArtistId_whenDeleteRequest_thenReturnsOkStatus() throws Exception {
//		mockMvc.perform(delete("/artists/{id}", "invalid_id"))
//            .andExpect(status().isOk());
//    }
//
//	@Test
//	@Order(12)
//    void whenGetAllRequest_afterDeleteRequest_thenReturnsOkStatusAndListOfArtistOutputs() throws Exception {
//		getAll();
//    }
//
//	@Test
//	@Order(13)
//    void givenValidArtistId_whenGetById_afterDeleteRequest_thenReturnsOkStatusAndArtistOutput() throws Exception {
//        getById();
//    }

	@AfterAll
    public void cleanUp() {
        mongoTemplate.getDb().drop();
    }

}
