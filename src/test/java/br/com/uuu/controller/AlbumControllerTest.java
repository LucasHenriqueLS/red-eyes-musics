package br.com.uuu.controller;

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

import br.com.uuu.converter.AlbumConverter;
import br.com.uuu.converter.ArtistConverter;
import br.com.uuu.converter.GenreConverter;
import br.com.uuu.error.exception.ErrorResponse;
import br.com.uuu.json.input.album.AlbumCreateInput;
import br.com.uuu.json.output.album.AlbumOutput;
import br.com.uuu.model.mongodb.entity.Album;
import br.com.uuu.model.mongodb.entity.Artist;
import br.com.uuu.model.mongodb.entity.Genre;
import br.com.uuu.model.mongodb.repository.AlbumRepositoryTest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AlbumControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private AlbumConverter albumConverter;
	
	@Autowired
	private GenreConverter genreConverter;

	@Autowired
	private ArtistConverter artistConverter;

	private List<Album> albums;
	
	private List<AlbumCreateInput> albumCreateInputs;
	
	private List<AlbumOutput> albumOutputs;

	private List<Artist> artists;
	
	private List<Genre> genres;

    @BeforeAll
    public void setup() throws Exception {
    	genres = GenreControllerTest.setupGenres(mockMvc, objectMapper, genreConverter);
    	artists = ArtistControllerTest.setupArtist(mockMvc, objectMapper, artistConverter, genres);
    	albums = AlbumRepositoryTest.getAlbums();

        albumCreateInputs = List.of(
    			AlbumCreateInput.builder().title(albums.get(0).getTitle()).releaseDate(albums.get(0).getReleaseDate()).artistIds(List.of(artists.get(0).getId())).coverUrl(albums.get(0).getCoverUrl()).genreIds(List.of(genres.get(0).getId(), genres.get(1).getId())).recordCompanyName(albums.get(0).getRecordCompanyName()).build(),
    			AlbumCreateInput.builder().title(albums.get(1).getTitle()).releaseDate(albums.get(1).getReleaseDate()).artistIds(List.of(artists.get(1).getId())).coverUrl(albums.get(1).getCoverUrl()).genreIds(List.of(genres.get(1).getId(), genres.get(2).getId())).recordCompanyName(albums.get(1).getRecordCompanyName()).build(),
    			AlbumCreateInput.builder().title(albums.get(2).getTitle()).releaseDate(albums.get(2).getReleaseDate()).artistIds(List.of(artists.get(2).getId())).coverUrl(albums.get(2).getCoverUrl()).genreIds(List.of(genres.get(0).getId(), genres.get(2).getId())).recordCompanyName(albums.get(2).getRecordCompanyName()).build()
    		);
    	albums = albumCreateInputs.stream().map(input -> albumConverter.toEntity(new Album(), input)).collect(Collectors.toList());
    	albumOutputs = albums.stream().map(entity -> albumConverter.toOutput(entity)).collect(Collectors.toList());
    }

    private <T> void checkList(ResultActions response, List<T> list, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s", jsonPath)).isArray())
    	.andExpect(jsonPath(String.format("%s.length()", jsonPath)).value(list.size()));
    	for (int i = 0; i < list.size(); i++) {
    	     response.andExpect(jsonPath(String.format("%s[%d]", jsonPath, i)).value(list.get(i)));
    	}
    }
    
    private void checkAlbumOutput(ResultActions response, AlbumOutput albumOutput, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.id", jsonPath)).value(albumOutput.getId()))
    	.andExpect(jsonPath(String.format("%s.title", jsonPath)).value(albumOutput.getTitle()))
    	.andExpect(jsonPath(String.format("%s.releaseDate", jsonPath)).value(albumOutput.getReleaseDate().toString()))
    	.andExpect(jsonPath(String.format("%s.coverUrl", jsonPath)).value(albumOutput.getCoverUrl()))
    	.andExpect(jsonPath(String.format("%s.recordCompanyName", jsonPath)).value(albumOutput.getRecordCompanyName()));
    	checkList(response, albumOutput.getArtistIds(), String.format("%s.artistIds", jsonPath));
    	checkList(response, albumOutput.getGenreIds(), String.format("%s.genreIds", jsonPath));
    }

    private void checkErrorResponse(ResultActions response, ErrorResponse errorResponse, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.status", jsonPath)).value(errorResponse.getStatus()))
        .andExpect(jsonPath(String.format("%s.message", jsonPath)).value(errorResponse.getMessage()))
        .andExpect(jsonPath(String.format("%s.timestamp", jsonPath)).isNotEmpty());
    }

	@Test
	@Order(1)
	void givenValidAlbumCreateInputWhenPostRequestThenReturnsCreatedStatusAndAlbumOutput() throws Exception {
		for (int i = 0; i < albumCreateInputs.size(); i++) {
			var albumCreateInput = albumCreateInputs.get(i);
			var albumOutput = albumOutputs.get(i);

			var response = mockMvc.perform(post("/albums")
				    .contentType(MediaType.APPLICATION_JSON)
				    .content(objectMapper.writeValueAsString(albumCreateInput)))
				    .andExpect(status().isCreated());

			var jsonResponse = response.andReturn().getResponse().getContentAsString();
			var id = (String) JsonPath.parse(jsonResponse).read("$.id");
			albums.get(i).setId(id);
			albumOutputs.get(i).setId(id);

			checkAlbumOutput(response, albumOutput, "$");
		}
    }

	@Test
	@Order(2)
    void givenInvalidAlbumCreateInputWithInvalidReleaseDateAndOtherFieldsEmptyWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var response = mockMvc.perform(post("/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"releaseDate\": \"2030-06-20\"}"))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{artistIds=não pode ser nulo ou vazio, genreIds=não pode ser nulo ou vazio, releaseDate=deve ser a data atual ou uma data passada, title=não pode ser nulo ou vazio}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(3)
    void givenInvalidAlbumCreateInputWithInvalidArtistIdsWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var albumUpdateInput =
				AlbumCreateInput.builder()
				.title(albums.get(0).getTitle())
				.releaseDate(albums.get(0).getReleaseDate())
				.artistIds(List.of("invalid_id_1","invalid_id_2"))
				.coverUrl(albums.get(0).getCoverUrl())
				.genreIds(List.of("invalid_id_1","invalid_id_2"))
				.recordCompanyName(albums.get(0).getRecordCompanyName())
				.build();

		var response = mockMvc.perform(post("/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumUpdateInput)))
                .andExpect(status().isNotFound());

		var errorResponse = ErrorResponse.notFound("Artistas com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }
	
	@Test
	@Order(4)
    void givenInvalidAlbumCreateInputWithInvalidGenreIdsWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var albumUpdateInput =
				AlbumCreateInput.builder()
				.title(albums.get(0).getTitle())
				.releaseDate(albums.get(0).getReleaseDate())
				.artistIds(albums.get(0).getArtistIds())
				.coverUrl(albums.get(0).getCoverUrl())
				.genreIds(List.of("invalid_id_1","invalid_id_2"))
				.recordCompanyName(albums.get(0).getRecordCompanyName())
				.build();

		var response = mockMvc.perform(post("/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumUpdateInput)))
                .andExpect(status().isNotFound());

		var errorResponse = ErrorResponse.notFound("Gêneros com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }

//	@Test
//	@Order(4)
//    void whenGetAllRequestAfterPostRequestThenReturnsOkStatusAndListOfAlbumOutputs() throws Exception {
//		getAll();
//    }
//
//	private void getAll() throws Exception {
//		var response = mockMvc.perform(get("/albums"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(albumOutputs.size()));
//
//        for (int i = 0; i < albumOutputs.size(); i++) {
//        	var albumOutput = albumOutputs.get(i);
//	        checkAlbumOutput(response, albumOutput, String.format("$[%d]", i));
//	    }
//	}
//
//	@Test
//	@Order(5)
//    void givenValidAlbumIdWhenGetByIdAfterPostRequesThenReturnsOkStatusAndAlbumOutput() throws Exception {
//        getById();
//    }
//
//	private void getById() throws Exception {
//		for (var albumOutput : albumOutputs) {
//			var response = mockMvc.perform(get("/albums/get-by-id/{id}", albumOutput.getId()))
//				.andExpect(status().isOk());
//			checkAlbumOutput(response, albumOutput, "$");
//		}
//	}
//
//	@Test
//	@Order(6)
//    void givenInvalidAlbumIdWhenGetByIdThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
//    	var id = "invalid_id";
//		var response = mockMvc.perform(get("/albums/get-by-id/{id}", id))
//    		.andExpect(status().isNotFound());
//
//    	var errorResponse = ErrorResponse.notFound(String.format("Albuma com o ID %s não foi encontrado", id));
//    	checkErrorResponse(response, errorResponse, "$");
//    }
//
//	@Test
//	@Order(7)
//    void givenValidAlbumUpdateInputWhenPutRequestThenReturnsOkStatusAndAlbumOutput() throws Exception {
//		var names = List.of("Michael Jackson", "Michael Joseph Jackson", "MJ", "King of Pop", "The Gloved One");
//		var bio = "Michael Jackson foi um albuma multifacetado, conhecido por redefinir o cenário da música pop com seus movimentos de dança icônicos, videoclipes revolucionários e uma voz única.";
//		var genreIds = List.of(genres.get(1).getId(), genres.get(2).getId());
//		var imageUrl = "https://example.com/images/michael_joseph_jackson.jpg";
//		var albumUpdateInput = AlbumUpdateInput.builder().names(Optional.of(names)).bio(Optional.of(bio)).genreIds(Optional.of(genreIds)).imageUrl(Optional.of(imageUrl)).build();
//
//        var response = mockMvc.perform(put("/albums/{id}", albums.get(0).getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(albumUpdateInput)))
//            .andExpect(status().isOk());
//
//        var updatedAlbum = albums.get(0);
//        updatedAlbum.setNames(names);
//        updatedAlbum.setBio(bio);
//        updatedAlbum.setGenreIds(genreIds);
//        updatedAlbum.setImageUrl(imageUrl);
//        var updatedAlbumOutput = albumOutputs.get(0);
//        updatedAlbumOutput.setNames(names);
//        updatedAlbumOutput.setBio(bio);
//        updatedAlbumOutput.setGenreIds(genreIds);
//        updatedAlbumOutput.setImageUrl(imageUrl);
//
//        checkAlbumOutput(response, updatedAlbumOutput, "$");
//    }
//
//	@Test
//	@Order(8)
//    void givenValidAlbumUpdateInputWithAllFieldsEmptyWhenPutRequestThenReturnsOkStatusAndAlbumOutput() throws Exception {
//        var albumOutput = albumOutputs.get(0);
//
//        var response = mockMvc.perform(put("/albums/{id}", albums.get(0).getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .content("{}"))
//            .andExpect(status().isOk());
//        checkAlbumOutput(response, albumOutput, "$");
//    }
//
//	@Test
//	@Order(9)
//    void givenInvalidAlbumUpdateInputWithInvalidGenreIdsWhenPutRequestThenReturnsOkStatusAndAlbumOutput() throws Exception {
//		var albumUpdateInput = AlbumUpdateInput.builder().genreIds(Optional.of(List.of("invalid_id_1","invalid_id_2"))).build();
//
//        var response = mockMvc.perform(put("/albums/{id}", albums.get(0).getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(albumUpdateInput)))
//            .andExpect(status().isNotFound());
//
//        var errorResponse = ErrorResponse.notFound("Gêneros com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
//		checkErrorResponse(response, errorResponse, "$");
//    }
//
//	@Test
//	@Order(10)
//    void whenGetAllRequestAfterPutRequestThenReturnsOkStatusAndListOfAlbumOutputs() throws Exception {
//		getAll();
//    }
//
//	@Test
//	@Order(11)
//    void givenValidGenreIdWhenGetByIdAfterPutRequestThenReturnsOkStatusAndAlbumOutput() throws Exception {
//        getById();
//    }
//
//	@Test
//	@Order(12)
//    void givenValidAlbumIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
//		mockMvc.perform(delete("/albums/{id}", albums.get(0).getId()))
//            .andExpect(status().isOk());
//
//		albums.remove(0);
//		albumOutputs.remove(0);
//    }
//
//	@Test
//	@Order(13)
//    void givenInvalidAlbumIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
//		mockMvc.perform(delete("/albums/{id}", "invalid_id"))
//            .andExpect(status().isOk());
//    }
//
//	@Test
//	@Order(14)
//    void whenGetAllRequestAfterDeleteRequestThenReturnsOkStatusAndListOfAlbumOutputs() throws Exception {
//		getAll();
//    }
//
//	@Test
//	@Order(15)
//    void givenValidAlbumIdWhenGetByIdAfterDeleteRequestThenReturnsOkStatusAndAlbumOutput() throws Exception {
//        getById();
//    }

	@AfterAll
    public void cleanUp() {
        mongoTemplate.getDb().drop();
    }

}
