package br.com.uuu.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
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
import br.com.uuu.converter.LanguageConverter;
import br.com.uuu.converter.SongConverter;
import br.com.uuu.converter.UserConverter;
import br.com.uuu.error.exception.ErrorResponse;
import br.com.uuu.json.input.song.SongCreateInput;
import br.com.uuu.json.input.song.SongDetailsCreateInput;
import br.com.uuu.json.output.song.SongDetailsOutput;
import br.com.uuu.json.output.song.SongOutput;
import br.com.uuu.model.mongodb.entity.Album;
import br.com.uuu.model.mongodb.entity.Artist;
import br.com.uuu.model.mongodb.entity.Genre;
import br.com.uuu.model.mongodb.entity.Language;
import br.com.uuu.model.mongodb.entity.Song;
import br.com.uuu.model.mongodb.entity.User;
import br.com.uuu.model.mongodb.repository.SongRepositoryTest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SongControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SongConverter songConverter;

	@Autowired
	private LanguageConverter languageConverter;

	@Autowired
	private GenreConverter genreConverter;

	@Autowired
	private ArtistConverter artistConverter;
	
	@Autowired
	private AlbumConverter albumConverter;
	
	@Autowired
	private UserConverter userConverter;

	private List<Song> songs;
	
	private List<SongCreateInput> songCreateInputs;
	
	private List<SongOutput> songOutputs;

	private List<Language> languages;

	private List<Genre> genres;

	private List<Artist> artists;

	private List<Album> albums;

	private List<User> users;

    @BeforeAll
    public void setup() throws Exception {
    	languages = LanguageControllerTest.setupLanguages(mockMvc, objectMapper, languageConverter);
    	genres = GenreControllerTest.setupGenres(mockMvc, objectMapper, genreConverter);
    	artists = ArtistControllerTest.setupArtist(mockMvc, objectMapper, artistConverter, genres);
    	albums = AlbumControllerTest.setupAlbums(mockMvc, objectMapper, albumConverter, genres, artists);
    	users = UserControllerTest.setupUsers(mockMvc, objectMapper, userConverter, genres, artists);
    	setupSongs(genres, artists, albums, users);
    }

    private void setupSongs(List<Genre> genres, List<Artist> artists, List<Album> albums, List<User> users) {
    	var i = new AtomicInteger(0);
    	var j = new AtomicInteger(0);
    	var random = new Random();
        songCreateInputs = SongRepositoryTest.getSongs().stream().map(song ->
        	SongCreateInput.builder()
    			.artistIds(List.of(artists.get(i.get()).getId()))
				.composerNames(song.getComposerNames())
				.albumId(albums.get(i.get()).getId())
				.genreIds(List.of(genres.get(random.nextInt(2)).getId(), genres.get(random.nextInt(2)).getId()))
				.originalLanguageId(languages.get(i.getAndIncrement()).getId())
				.detailsByLanguageId(
					song.getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						entry -> languages.get(j.getAndIncrement() % song.getDetailsByLanguageId().size()).getId(),
						entry ->
							SongDetailsCreateInput.builder()
								.title(entry.getValue().getTitle())
								.lyric(entry.getValue().getLyric())
								.submitterId(users.get(random.nextInt(2)).getId())
							.build()
					))
				)
				.durationInSeconds(song.getDurationInSeconds())
				.releaseDate(song.getReleaseDate())
				.videoUrl(song.getVideoUrl())
			.build()
        ).toList();
    	songs = songCreateInputs.stream().map(input -> songConverter.toEntity(new Song(), input)).collect(Collectors.toList());
    	songOutputs = songs.stream().map(entity -> songConverter.toOutput(entity)).collect(Collectors.toList());
    }

    private <T> void checkList(ResultActions response, List<T> list, String jsonPath, TriConsumer<ResultActions, T, String> verifier) throws Exception {
        response
            .andExpect(jsonPath(String.format("%s", jsonPath)).isArray())
            .andExpect(jsonPath(String.format("%s.length()", jsonPath)).value(list.size()));
        
        for (int i = 0; i < list.size(); i++) {
            verifier.accept(response, list.get(i), String.format("%s[%d]", jsonPath, i));
        }
    }

    private <T> void check(ResultActions response, T value, String jsonPath, TriConsumer<ResultActions, T, String> verifier) throws Exception {
    	verifier.accept(response, value, jsonPath);
    }
    
    private <T> void check(ResultActions response, T value, String jsonPath, Consumer4<ResultActions, T, String, TriConsumer<ResultActions, T, String>> verifier, TriConsumer<ResultActions, T, String> subVerifier) throws Exception {
    	verifier.accept(response, value, jsonPath, subVerifier);
    }
    
    private <T> void checkPrimitive(ResultActions response, T value, String jsonPath) throws Exception {
    	response.andExpect(jsonPath(jsonPath).value(value));
    }
    
    private <K, V> void checkMap(ResultActions response, Map<K, V> map, String jsonPath) throws Exception {
        response
        .andExpect(jsonPath(jsonPath).isMap())
        .andExpect(jsonPath(String.format("%s.length()", jsonPath)).value(map.size()));
        for (var entry : map.entrySet()) {
        	checkSongDetailsOutput(response, (SongDetailsOutput) entry.getValue(), String.format("%s.%s", jsonPath, entry.getKey()));
        }
    }

    private void checkSongOutput(ResultActions response, SongOutput songOutput, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.id", jsonPath)).value(songOutput.getId()))
    	.andExpect(jsonPath(String.format("%s.albumId", jsonPath)).value(songOutput.getAlbumId()))
    	.andExpect(jsonPath(String.format("%s.originalLanguageId", jsonPath)).value(songOutput.getOriginalLanguageId()))
    	.andExpect(jsonPath(String.format("%s.durationInSeconds", jsonPath)).value(songOutput.getDurationInSeconds()))
    	.andExpect(jsonPath(String.format("%s.videoUrl", jsonPath)).value(songOutput.getVideoUrl()))
    	.andExpect(jsonPath(String.format("%s.releaseDate", jsonPath)).value(songOutput.getReleaseDate().toString()));
    	check(response, songOutput.getComposerNames(), String.format("%s.composerNames", jsonPath), (t, u, v) -> {
            try {
                checkList(t, u, v, (response1, item, path) -> {
                    try {
						checkPrimitive(response1, item, path);
					} catch (Exception e) {
						e.printStackTrace();
					}
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
//    	checkList(response, songOutput.getComposerNames(), String.format("%s.composerNames", jsonPath));
//    	checkList(response, songOutput.getArtistIds(), String.format("%s.artistIds", jsonPath));
//    	checkList(response, songOutput.getGenreIds(), String.format("%s.genreIds", jsonPath));
//    	checkMap(response, songOutput.getDetailsByLanguageId(), String.format("%s.detailsByLanguageId", jsonPath));
    }

    private void checkSongDetailsOutput(ResultActions response, SongDetailsOutput songDetailsOutput, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.title", jsonPath)).value(songDetailsOutput.getTitle()))
    	.andExpect(jsonPath(String.format("%s.lyric", jsonPath)).value(songDetailsOutput.getLyric()))
    	.andExpect(jsonPath(String.format("%s.submitterId", jsonPath)).value(songDetailsOutput.getSubmitterId()));

//    	if (jsonPathExists(response,  String.format("%s.proofreaderIds", jsonPath))) {
//    		checkList(response, songDetailsOutput.getProofreaderIds(), String.format("%s.proofreaderIds", jsonPath));    		
//    	}
    }
    
    private Boolean jsonPathExists(ResultActions response, String jsonPath) throws Exception {
        try {
            response.andExpect(jsonPath(jsonPath).exists());
            return Boolean.TRUE;
        } catch (AssertionError e) {
            return Boolean.FALSE;
        }
    }

    private void checkErrorResponse(ResultActions response, ErrorResponse errorResponse, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.status", jsonPath)).value(errorResponse.getStatus()))
        .andExpect(jsonPath(String.format("%s.message", jsonPath)).value(errorResponse.getMessage()))
        .andExpect(jsonPath(String.format("%s.timestamp", jsonPath)).isNotEmpty());
    }

	@Test
	@Order(1)
	void givenValidSongCreateInputWhenPostRequestThenReturnsCreatedStatusAndSongOutput() throws Exception {
		for (int i = 0; i < songCreateInputs.size(); i++) {
			var songCreateInput = songCreateInputs.get(i);
			var songOutput = songOutputs.get(i);

			var response = mockMvc.perform(post("/songs")
				    .contentType(MediaType.APPLICATION_JSON)
				    .content(objectMapper.writeValueAsString(songCreateInput)))
				    .andExpect(status().isCreated());

			var jsonResponse = response.andReturn().getResponse().getContentAsString();
			var id = (String) JsonPath.parse(jsonResponse).read("$.id");
			songs.get(i).setId(id);
			songOutputs.get(i).setId(id);

			checkSongOutput(response, songOutput, "$");
		}
    }

//	@Test
//	@Order(2)
//    void givenInvalidSongCreateInputWithAllFieldsEmptyWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
//		var response = mockMvc.perform(post("/songs")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{}"))
//                .andExpect(status().isBadRequest());
//
//		var errorResponse = ErrorResponse.badRequest("{artistIds=não pode ser nulo ou vazio, genreIds=não pode ser nulo ou vazio, releaseDate=não pode ser nulo, title=não pode ser nulo, vazio ou conter somente espaços em branco}");
//		checkErrorResponse(response, errorResponse, "$");
//    }

//	@Test
//	@Order(3)
//    void givenInvalidSongCreateInputWithAllFieldsInvalidWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
//		var songCreateInput =
//			SongCreateInput.builder()
//				.title(" ")
//	    		.releaseDate(LocalDate.parse("2030-06-15"))
//	    		.artistIds(List.of())
//	    		.genreIds(List.of())
//	    	.build();
//
//		var response = mockMvc.perform(post("/songs")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(songCreateInput)))
//                .andExpect(status().isBadRequest());
//
//		var errorResponse = ErrorResponse.badRequest("{artistIds=não pode ser nulo ou vazio, genreIds=não pode ser nulo ou vazio, releaseDate=deve ser a data atual ou uma data passada, title=não pode ser nulo, vazio ou conter somente espaços em branco}");
//		checkErrorResponse(response, errorResponse, "$");
//    }
//
//	@Test
//	@Order(3)
//    void givenInvalidSongCreateInputWithInvalidArtistIdsWhenPostRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
//		var songCreateInput =
//			SongCreateInput.builder()
//				.title(songs.get(0).getTitle())
//				.releaseDate(songs.get(0).getReleaseDate())
//				.artistIds(List.of("invalid_id_1", "invalid_id_2"))
//				.coverUrl(songs.get(0).getCoverUrl())
//				.genreIds(List.of("invalid_id_1", "invalid_id_2"))
//				.recordCompanyName(songs.get(0).getRecordCompanyName())
//			.build();
//
//		var response = mockMvc.perform(post("/songs")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(songCreateInput)))
//                .andExpect(status().isNotFound());
//
//		var errorResponse = ErrorResponse.notFound("Artistas com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
//		checkErrorResponse(response, errorResponse, "$");
//    }
//	
//	@Test
//	@Order(4)
//    void givenInvalidSongCreateInputWithInvalidGenreIdsWhenPostRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
//		var songCreateInput =
//			SongCreateInput.builder()
//				.title(songs.get(0).getTitle())
//				.releaseDate(songs.get(0).getReleaseDate())
//				.artistIds(songs.get(0).getArtistIds())
//				.coverUrl(songs.get(0).getCoverUrl())
//				.genreIds(List.of("invalid_id_1", "invalid_id_2"))
//				.recordCompanyName(songs.get(0).getRecordCompanyName())
//			.build();
//
//		var response = mockMvc.perform(post("/songs")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(songCreateInput)))
//                .andExpect(status().isNotFound());
//
//		var errorResponse = ErrorResponse.notFound("Gêneros com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
//		checkErrorResponse(response, errorResponse, "$");
//    }
//
//	@Test
//	@Order(5)
//    void whenGetAllRequestAfterPostRequestThenReturnsOkStatusAndListOfSongOutputs() throws Exception {
//		getAll();
//    }
//
//	private void getAll() throws Exception {
//		var response = mockMvc.perform(get("/songs"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(songOutputs.size()));
//
//        for (int i = 0; i < songOutputs.size(); i++) {
//        	var songOutput = songOutputs.get(i);
//	        checkSongOutput(response, songOutput, String.format("$[%d]", i));
//	    }
//	}
//
//	@Test
//	@Order(6)
//    void givenValidSongIdWhenGetByIdAfterPostRequesThenReturnsOkStatusAndSongOutput() throws Exception {
//        getById();
//    }
//
//	private void getById() throws Exception {
//		for (var songOutput : songOutputs) {
//			var response = mockMvc.perform(get("/songs/get-by-id/{id}", songOutput.getId()))
//				.andExpect(status().isOk());
//			checkSongOutput(response, songOutput, "$");
//		}
//	}
//
//	@Test
//	@Order(7)
//    void givenInvalidSongIdWhenGetByIdThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
//    	var id = "invalid_id";
//		var response = mockMvc.perform(get("/songs/get-by-id/{id}", id))
//    		.andExpect(status().isNotFound());
//
//    	var errorResponse = ErrorResponse.notFound(String.format("Álbum com o ID %s não foi encontrado", id));
//    	checkErrorResponse(response, errorResponse, "$");
//    }
//
//	@Test
//	@Order(8)
//    void givenValidSongUpdateInputWhenPutRequestThenReturnsOkStatusAndSongOutput() throws Exception {
//		var songUpdateInput =
//			SongUpdateInput.builder()
//    			.title("Thriller (Special Edition)")
//    			.releaseDate(LocalDate.parse("1982-11-30"))
//    			.artistIds(List.of(artists.get(0).getId()))
//    			.coverUrl("https://example.com/images/thriller_special_edition.jpg")
//    			.genreIds(List.of(genres.get(0).getId(), genres.get(1).getId(), genres.get(2).getId()))
//    			.recordCompanyName("Epic Records")
//    		.build();
//
//        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(songUpdateInput)))
//            .andExpect(status().isOk());
//
//        songConverter.toEntity(songs.get(0), songUpdateInput);
//        var updatedSongOutput = songConverter.toOutput(songs.get(0));
//        songOutputs.set(0, updatedSongOutput);
//
//        checkSongOutput(response, updatedSongOutput, "$");
//    }
//
//	@Test
//	@Order(9)
//    void givenValidSongUpdateInputWithAllFieldsEmptyWhenPutRequestThenReturnsOkStatusAndSongOutput() throws Exception {
//        var songOutput = songOutputs.get(0);
//
//        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .content("{}"))
//            .andExpect(status().isOk());
//        checkSongOutput(response, songOutput, "$");
//    }
//
//	@Test
//	@Order(10)
//    void givenInvalidSongUpdateInputWithAllFieldsInvalidWhenPutRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
//		var songUpdateInput =
//			SongUpdateInput.builder()
//    			.title(" ")
//    			.releaseDate(LocalDate.parse("2030-06-15"))
//    			.coverUrl(" ")
//    		.build();
//
//        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(songUpdateInput)))
//            .andExpect(status().isBadRequest());
//
//        var errorResponse = ErrorResponse.badRequest("{releaseDate=deve ser a data atual ou uma data passada, title=não pode ser vazio ou conter somente espaços em branco se não for nulo}");
//		checkErrorResponse(response, errorResponse, "$");
//    }
//
//	@Test
//	@Order(11)
//    void givenInvalidSongUpdateInputWithInvalidArtistIdsWhenPutRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
//		var songUpdateInput =
//			SongUpdateInput.builder()
//				.artistIds(List.of("invalid_id_1"))
//				.genreIds(List.of("invalid_id_1", "invalid_id_2"))
//			.build();
//
//        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(songUpdateInput)))
//            .andExpect(status().isNotFound());
//
//        var errorResponse = ErrorResponse.notFound("Artistas com os IDs [invalid_id_1] não foram encontrados");
//		checkErrorResponse(response, errorResponse, "$");
//    }
//
//	@Test
//	@Order(12)
//    void givenInvalidSongUpdateInputWithInvalidGenreIdsWhenPutRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
//		var songUpdateInput =
//			SongUpdateInput.builder()
//				.genreIds(List.of("invalid_id_1", "invalid_id_2"))
//			.build();
//
//        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(songUpdateInput)))
//            .andExpect(status().isNotFound());
//
//        var errorResponse = ErrorResponse.notFound("Gêneros com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
//		checkErrorResponse(response, errorResponse, "$");
//    }
//
//	@Test
//	@Order(13)
//    void whenGetAllRequestAfterPutRequestThenReturnsOkStatusAndListOfSongOutputs() throws Exception {
//		getAll();
//    }
//
//	@Test
//	@Order(14)
//    void givenValidGenreIdWhenGetByIdAfterPutRequestThenReturnsOkStatusAndSongOutput() throws Exception {
//        getById();
//    }
//
//	@Test
//	@Order(15)
//    void givenValidSongIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
//		mockMvc.perform(delete("/songs/{id}", songs.get(0).getId()))
//            .andExpect(status().isOk());
//
//		songs.remove(0);
//		songOutputs.remove(0);
//    }
//
//	@Test
//	@Order(16)
//    void givenInvalidSongIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
//		mockMvc.perform(delete("/songs/{id}", "invalid_id"))
//            .andExpect(status().isOk());
//    }
//
//	@Test
//	@Order(17)
//    void whenGetAllRequestAfterDeleteRequestThenReturnsOkStatusAndListOfSongOutputs() throws Exception {
//		getAll();
//    }
//
//	@Test
//	@Order(18)
//    void givenValidSongIdWhenGetByIdAfterDeleteRequestThenReturnsOkStatusAndSongOutput() throws Exception {
//        getById();
//    }

	@AfterAll
    public void cleanUp() {
        mongoTemplate.getDb().drop();
    }

}

@FunctionalInterface
interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}

@FunctionalInterface
interface Consumer4<T, U, V, W> {
    void accept(T t, U u, V v, W w);
}
