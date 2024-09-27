package br.com.uuu.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.LinkedHashMap;
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
import br.com.uuu.json.input.song.SongDetailsUpdateInput;
import br.com.uuu.json.input.song.SongUpdateInput;
import br.com.uuu.json.output.song.SongDetailsOutput;
import br.com.uuu.json.output.song.SongOutput;
import br.com.uuu.model.mongodb.entity.Album;
import br.com.uuu.model.mongodb.entity.Artist;
import br.com.uuu.model.mongodb.entity.Genre;
import br.com.uuu.model.mongodb.entity.Language;
import br.com.uuu.model.mongodb.entity.Song;
import br.com.uuu.model.mongodb.entity.User;
import br.com.uuu.model.mongodb.repository.SongRepositoryTest;
import br.com.uuu.util.Checker;

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

    private void checkSongOutput(ResultActions response, SongOutput songOutput, String jsonPath) throws Exception {
    	response
	    	.andExpect(jsonPath(String.format("%s.id", jsonPath)).value(songOutput.getId()))
	    	.andExpect(jsonPath(String.format("%s.albumId", jsonPath)).value(songOutput.getAlbumId()))
	    	.andExpect(jsonPath(String.format("%s.originalLanguageId", jsonPath)).value(songOutput.getOriginalLanguageId()))
	    	.andExpect(jsonPath(String.format("%s.durationInSeconds", jsonPath)).value(songOutput.getDurationInSeconds()))
	    	.andExpect(jsonPath(String.format("%s.videoUrl", jsonPath)).value(songOutput.getVideoUrl()))
	    	.andExpect(jsonPath(String.format("%s.releaseDate", jsonPath)).value(songOutput.getReleaseDate().toString()));

    	Checker.checkList(response, songOutput.getComposerNames(), String.format("%s.composerNames", jsonPath), Checker::check);
    	Checker.checkList(response, songOutput.getArtistIds(), String.format("%s.artistIds", jsonPath), Checker::check);
    	Checker.checkList(response, songOutput.getGenreIds(), String.format("%s.genreIds", jsonPath), Checker::check);
    	Checker.checkMap(response, songOutput.getDetailsByLanguageId(), String.format("%s.detailsByLanguageId", jsonPath), this::checkSongDetailsOutput);
    }

    private void checkSongDetailsOutput(ResultActions response, SongDetailsOutput songDetailsOutput, String jsonPath) {
    	try {
			response
				.andExpect(jsonPath(String.format("%s.title", jsonPath)).value(songDetailsOutput.getTitle()))
				.andExpect(jsonPath(String.format("%s.lyric", jsonPath)).value(songDetailsOutput.getLyric()))
				.andExpect(jsonPath(String.format("%s.submitterId", jsonPath)).value(songDetailsOutput.getSubmitterId()));

			if (Checker.jsonPathExists(response,  String.format("%s.proofreaderIds", jsonPath))) {
				Checker.checkList(response, songDetailsOutput.getProofreaderIds(), String.format("%s.proofreaderIds", jsonPath), Checker::check);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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

	@Test
	@Order(2)
    void givenInvalidSongCreateInputWithAllFieldsEmptyWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var response = mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{artistIds=não pode ser nulo ou vazio, detailsByLanguageId=não pode ser nulo, durationInSeconds=não pode ser nulo, genreIds=não pode ser nulo ou vazio, originalLanguageId=não pode ser nulo ou vazio, releaseDate=não pode ser nulo, videoUrl=não pode ser nulo ou vazio}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(3)
    void givenInvalidSongCreateInputWithAllFieldsInvalidWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var songCreateInput =
			SongCreateInput.builder()
				.artistIds(List.of())
				.composerNames(List.of())
				.albumId(" ")
				.genreIds(List.of())
				.originalLanguageId(" ")
				.detailsByLanguageId(
					Map.of(
						"invalid_key_1",
						SongDetailsCreateInput.builder()
							.title(" ")
							.lyric(" ")
							.submitterId(" ")
						.build()
					)
				)
				.durationInSeconds(0)
				.releaseDate(LocalDate.parse("2030-06-15"))
				.videoUrl(" ")
	    	.build();

		var response = mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songCreateInput)))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{albumId=não pode ser vazio ou conter somente espaços em branco se não for nulo, artistIds=não pode ser nulo ou vazio, composerNames=não pode ser vazio se não for nulo, durationInSeconds=deve ser maior que zero, genreIds=não pode ser nulo ou vazio, originalLanguageId=não pode ser nulo ou vazio, releaseDate=deve ser a data atual ou uma data passada, videoUrl=não pode ser nulo ou vazio}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(4)
    void givenInvalidSongCreateInputWithInvalidArtistIdsWhenPostRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var i = new AtomicInteger(1);
		var songCreateInput =
			SongCreateInput.builder()
				.artistIds(List.of("invalid_id_1", "invalid_id_2"))
				.composerNames(songs.get(0).getComposerNames())
				.albumId(songs.get(0).getAlbumId())
				.genreIds(List.of("invalid_id_1", "invalid_id_2"))
				.originalLanguageId("invalid_id_1")
				.detailsByLanguageId(
					songs.get(0).getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						entry -> String.format("invalid_id_%d", i.getAndIncrement()),
						entry ->
							SongDetailsCreateInput.builder()
								.title(entry.getValue().getTitle())
								.lyric(entry.getValue().getLyric())
								.submitterId("invalid_id_1")
							.build(),
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
					))
				)
				.durationInSeconds(songs.get(0).getDurationInSeconds())
				.releaseDate(songs.get(0).getReleaseDate())
				.videoUrl(songs.get(0).getVideoUrl())
	    	.build();

		var response = mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songCreateInput)))
                .andExpect(status().isNotFound());

		var errorResponse = ErrorResponse.notFound("Artistas com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }
	
	@Test
	@Order(5)
    void givenInvalidSongCreateInputWithInvalidGenreIdsWhenPostRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var i = new AtomicInteger(1);
		var songCreateInput =
			SongCreateInput.builder()
				.artistIds(songs.get(0).getArtistIds())
				.composerNames(songs.get(0).getComposerNames())
				.albumId(songs.get(0).getAlbumId())
				.genreIds(List.of("invalid_id_1", "invalid_id_2"))
				.originalLanguageId("invalid_id_1")
				.detailsByLanguageId(
					songs.get(0).getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						entry -> String.format("invalid_id_%d", i.getAndIncrement()),
						entry ->
							SongDetailsCreateInput.builder()
								.title(entry.getValue().getTitle())
								.lyric(entry.getValue().getLyric())
								.submitterId("invalid_id_1")
							.build(),
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
					))
				)
				.durationInSeconds(songs.get(0).getDurationInSeconds())
				.releaseDate(songs.get(0).getReleaseDate())
				.videoUrl(songs.get(0).getVideoUrl())
	    	.build();

		var response = mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songCreateInput)))
                .andExpect(status().isNotFound());

		var errorResponse = ErrorResponse.notFound("Gêneros com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }
	
	@Test
	@Order(6)
    void givenInvalidSongCreateInputWithInvalidOriginalLanguageIdWhenPostRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var i = new AtomicInteger(1);
		var songCreateInput =
			SongCreateInput.builder()
				.artistIds(songs.get(0).getArtistIds())
				.composerNames(songs.get(0).getComposerNames())
				.albumId(songs.get(0).getAlbumId())
				.genreIds(songs.get(0).getGenreIds())
				.originalLanguageId("invalid_id_1")
				.detailsByLanguageId(
					songs.get(0).getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						entry -> String.format("invalid_id_%d", i.getAndIncrement()),
						entry ->
							SongDetailsCreateInput.builder()
								.title(entry.getValue().getTitle())
								.lyric(entry.getValue().getLyric())
								.submitterId("invalid_id_1")
							.build(),
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
					))
				)
				.durationInSeconds(songs.get(0).getDurationInSeconds())
				.releaseDate(songs.get(0).getReleaseDate())
				.videoUrl(songs.get(0).getVideoUrl())
	    	.build();

		var response = mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songCreateInput)))
                .andExpect(status().isNotFound());

		var errorResponse = ErrorResponse.notFound("Idioma com o ID invalid_id_1 não foi encontrado");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(7)
    void givenInvalidSongCreateInputWithInvalidSongDetailsLanguageIdsWhenPostRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var i = new AtomicInteger(1);
		var songCreateInput =
			SongCreateInput.builder()
				.artistIds(songs.get(0).getArtistIds())
				.composerNames(songs.get(0).getComposerNames())
				.albumId(songs.get(0).getAlbumId())
				.genreIds(songs.get(0).getGenreIds())
				.originalLanguageId(songs.get(0).getOriginalLanguageId())
				.detailsByLanguageId(
					songs.get(0).getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						entry -> String.format("invalid_id_%d", i.getAndIncrement()),
						entry ->
							SongDetailsCreateInput.builder()
								.title(entry.getValue().getTitle())
								.lyric(entry.getValue().getLyric())
								.submitterId("invalid_id_1")
							.build(),
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
					))
				)
				.durationInSeconds(songs.get(0).getDurationInSeconds())
				.releaseDate(songs.get(0).getReleaseDate())
				.videoUrl(songs.get(0).getVideoUrl())
	    	.build();

		var response = mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songCreateInput)))
                .andExpect(status().isNotFound());

		var errorResponse = ErrorResponse.notFound("Idioma com o ID invalid_id_1 não foi encontrado");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(8)
    void givenInvalidSongCreateInputWithInvalidSubmitterIdWhenPostRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var songCreateInput =
			SongCreateInput.builder()
				.artistIds(songs.get(0).getArtistIds())
				.composerNames(songs.get(0).getComposerNames())
				.albumId(songs.get(0).getAlbumId())
				.genreIds(songs.get(0).getGenreIds())
				.originalLanguageId(songs.get(0).getOriginalLanguageId())
				.detailsByLanguageId(
					songs.get(0).getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						entry -> entry.getKey(),
						entry ->
							SongDetailsCreateInput.builder()
								.title(entry.getValue().getTitle())
								.lyric(entry.getValue().getLyric())
								.submitterId("invalid_id_1")
							.build(),
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
					))
				)
				.durationInSeconds(songs.get(0).getDurationInSeconds())
				.releaseDate(songs.get(0).getReleaseDate())
				.videoUrl(songs.get(0).getVideoUrl())
	    	.build();

		var response = mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songCreateInput)))
                .andExpect(status().isNotFound());

		var errorResponse = ErrorResponse.notFound("Usuário com o ID invalid_id_1 não foi encontrado");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(9)
    void whenGetAllRequestAfterPostRequestThenReturnsOkStatusAndListOfSongOutputs() throws Exception {
		getAll();
    }

	private void getAll() throws Exception {
		var response = mockMvc.perform(get("/songs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(songOutputs.size()));

        for (int i = 0; i < songOutputs.size(); i++) {
        	var songOutput = songOutputs.get(i);
	        checkSongOutput(response, songOutput, String.format("$[%d]", i));
	    }
	}

	@Test
	@Order(10)
    void givenValidSongIdWhenGetByIdAfterPostRequesThenReturnsOkStatusAndSongOutput() throws Exception {
        getById();
    }

	private void getById() throws Exception {
		for (var songOutput : songOutputs) {
			var response = mockMvc.perform(get("/songs/get-by-id/{id}", songOutput.getId()))
				.andExpect(status().isOk());
			checkSongOutput(response, songOutput, "$");
		}
	}

	@Test
	@Order(11)
    void givenInvalidSongIdWhenGetByIdThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
    	var id = "invalid_id";
		var response = mockMvc.perform(get("/songs/get-by-id/{id}", id))
    		.andExpect(status().isNotFound());

    	var errorResponse = ErrorResponse.notFound(String.format("Música com o ID %s não foi encontrado", id));
    	checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(12)
    void givenValidSongUpdateInputWhenPutRequestThenReturnsOkStatusAndSongOutput() throws Exception {
		var random = new Random();
		var songUpdateInput =
			SongUpdateInput.builder()
				.artistIds(List.of(artists.get(0).getId()))
				.composerNames(List.of("Michael Joseph Jackson"))
				.albumId(albums.get(0).getId())
				.genreIds(List.of(genres.get(0).getId(), genres.get(1).getId(), genres.get(2).getId()))
				.originalLanguageId(languages.get(0).getId())
				.detailsByLanguageId(
					Map.of(
						languages.get(0).getId(),
						SongDetailsUpdateInput.builder()
							.title("Billie Jean")
							.lyric("Billie Jean is not my lover... She's just a girl who claims that I am the one...")
							.submitterId(users.get(0).getId())
							.proofreaderIds(List.of(users.get(random.nextInt(2)).getId(), users.get(random.nextInt(2)).getId()))
						.build(),
						languages.get(1).getId(),
						SongDetailsUpdateInput.builder()
							.title("Billie Jean")
							.lyric("Billie Jean não é minha amante... Ela é apenas uma garota que afirma que eu sou o único...")
							.submitterId(users.get(1).getId())
							.proofreaderIds(List.of(users.get(random.nextInt(2)).getId(), users.get(random.nextInt(2)).getId(), users.get(random.nextInt(2)).getId()))
						.build()
					)
				).durationInSeconds(297)
				.releaseDate(LocalDate.parse("1982-01-02"))
				.videoUrl("https://www.youtube.com/watch?v=Si_BDXLOo_T")
			.build();

        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(songUpdateInput)))
            .andExpect(status().isOk());

        songConverter.toEntity(songs.get(0), songUpdateInput);
        var updatedSongOutput = songConverter.toOutput(songs.get(0));
        songOutputs.set(0, updatedSongOutput);

        checkSongOutput(response, updatedSongOutput, "$");
    }

	@Test
	@Order(13)
    void givenValidSongUpdateInputWithAllFieldsEmptyWhenPutRequestThenReturnsOkStatusAndSongOutput() throws Exception {
        var songOutput = songOutputs.get(0);

        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isOk());
        checkSongOutput(response, songOutput, "$");
    }

	@Test
	@Order(14)
    void givenInvalidSongUpdateInputWithAllFieldsInvalidWhenPutRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var songUpdateInput =
			SongUpdateInput.builder()
				.artistIds(List.of())
				.composerNames(List.of())
				.albumId(" ")
				.genreIds(List.of())
				.originalLanguageId(" ")
				.detailsByLanguageId(
					Map.of(
						"invalid_key_1",
						SongDetailsUpdateInput.builder()
							.title(" ")
							.lyric(" ")
							.submitterId(" ")
							.proofreaderIds(List.of())
						.build()
					)
				)
				.durationInSeconds(0)
				.releaseDate(LocalDate.parse("2030-06-15"))
				.videoUrl(" ")
    		.build();

        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(songUpdateInput)))
            .andExpect(status().isBadRequest());

        var errorResponse = ErrorResponse.badRequest("{albumId=não pode ser vazio ou conter somente espaços em branco se não for nulo, artistIds=não pode ser vazio se não for nulo, composerNames=não pode ser vazio se não for nulo, durationInSeconds=deve ser maior que zero, genreIds=não pode ser vazio se não for nulo, originalLanguageId=não pode ser vazio ou conter somente espaços em branco se não for nulo, releaseDate=deve ser a data atual ou uma data passada, videoUrl=não pode ser vazio ou conter somente espaços em branco se não for nulo}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(15)
    void givenInvalidSongUpdateInputWithInvalidArtistIdsWhenPutRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var i = new AtomicInteger(1);
		var songUpdateInput =
			SongUpdateInput.builder()
				.artistIds(List.of("invalid_id_1", "invalid_id_2"))
				.genreIds(List.of("invalid_id_1", "invalid_id_2"))
				.originalLanguageId("invalid_id_1")
				.detailsByLanguageId(
					songs.get(0).getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						entry -> String.format("invalid_id_%d", i.getAndIncrement()),
						entry ->
							SongDetailsUpdateInput.builder()
								.submitterId("invalid_id_1")
							.build(),
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
					))
				)
			.build();

        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(songUpdateInput)))
            .andExpect(status().isNotFound());

        var errorResponse = ErrorResponse.notFound("Artistas com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(16)
    void givenInvalidSongUpdateInputWithInvalidGenreIdsWhenPutRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var i = new AtomicInteger(1);
		var songUpdateInput =
			SongUpdateInput.builder()
				.genreIds(List.of("invalid_id_1", "invalid_id_2"))
				.originalLanguageId("invalid_id_1")
				.detailsByLanguageId(
					songs.get(0).getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						entry -> String.format("invalid_id_%d", i.getAndIncrement()),
						entry ->
							SongDetailsUpdateInput.builder()
								.submitterId("invalid_id_1")
							.build(),
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
					))
				)
			.build();

        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(songUpdateInput)))
            .andExpect(status().isNotFound());

        var errorResponse = ErrorResponse.notFound("Gêneros com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(17)
    void givenInvalidSongUpdateInputWithInvalidOriginalLanguageIdWhenPutRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var i = new AtomicInteger(1);
		var songUpdateInput =
			SongUpdateInput.builder()
				.originalLanguageId("invalid_id_1")
				.detailsByLanguageId(
					songs.get(0).getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						entry -> String.format("invalid_id_%d", i.getAndIncrement()),
						entry ->
							SongDetailsUpdateInput.builder()
								.submitterId("invalid_id_1")
							.build(),
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
					))
				)
			.build();

        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(songUpdateInput)))
            .andExpect(status().isNotFound());

        var errorResponse = ErrorResponse.notFound("Idioma com o ID invalid_id_1 não foi encontrado");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(18)
    void givenInvalidSongUpdateInputWithInvalidSongDetailsLanguageIdsWhenPutRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var i = new AtomicInteger(1);
		var songUpdateInput =
			SongUpdateInput.builder()
				.detailsByLanguageId(
					songs.get(0).getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						entry -> String.format("invalid_id_%d", i.getAndIncrement()),
						entry ->
							SongDetailsUpdateInput.builder()
								.submitterId("invalid_id_1")
							.build(),
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
					))
				)
			.build();

        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(songUpdateInput)))
            .andExpect(status().isNotFound());

        var errorResponse = ErrorResponse.notFound("Idioma com o ID invalid_id_1 não foi encontrado");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(19)
    void givenInvalidSongUpdateInputWithInvalidSubmitterIdWhenPutRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var songUpdateInput =
			SongUpdateInput.builder()
				.detailsByLanguageId(
					songs.get(0).getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
						entry -> entry.getKey(),
						entry ->
							SongDetailsUpdateInput.builder()
								.submitterId("invalid_id_1")
							.build(),
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
					))
				)
			.build();

        var response = mockMvc.perform(put("/songs/{id}", songs.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(songUpdateInput)))
            .andExpect(status().isNotFound());

        var errorResponse = ErrorResponse.notFound("Usuário com o ID invalid_id_1 não foi encontrado");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(20)
    void whenGetAllRequestAfterPutRequestThenReturnsOkStatusAndListOfSongOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(21)
    void givenValidGenreIdWhenGetByIdAfterPutRequestThenReturnsOkStatusAndSongOutput() throws Exception {
        getById();
    }

	@Test
	@Order(22)
    void givenValidSongIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/songs/{id}", songs.get(0).getId()))
            .andExpect(status().isOk());

		songs.remove(0);
		songOutputs.remove(0);
    }

	@Test
	@Order(23)
    void givenInvalidSongIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/songs/{id}", "invalid_id"))
            .andExpect(status().isOk());
    }

	@Test
	@Order(24)
    void whenGetAllRequestAfterDeleteRequestThenReturnsOkStatusAndListOfSongOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(25)
    void givenValidSongIdWhenGetByIdAfterDeleteRequestThenReturnsOkStatusAndSongOutput() throws Exception {
        getById();
    }

	@AfterAll
    public void cleanUp() {
        mongoTemplate.getDb().drop();
    }

}


