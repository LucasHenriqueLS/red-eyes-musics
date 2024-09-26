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
import br.com.uuu.converter.UserConverter;
import br.com.uuu.error.exception.ErrorResponse;
import br.com.uuu.json.input.user.UserCreateInput;
import br.com.uuu.json.input.user.UserUpdateInput;
import br.com.uuu.json.output.user.UserOutput;
import br.com.uuu.model.mongodb.entity.Artist;
import br.com.uuu.model.mongodb.entity.Genre;
import br.com.uuu.model.mongodb.entity.User;
import br.com.uuu.model.mongodb.repository.UserRepositoryTest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserConverter userConverter;

	@Autowired
	private GenreConverter genreConverter;

	@Autowired
	private ArtistConverter artistConverter;

	private List<User> users;
	
	private List<UserCreateInput> userCreateInputs;
	
	private List<UserOutput> userOutputs;

	private List<Artist> artists;

	private List<Genre> genres;

    @BeforeAll
    public void setup() throws Exception {
    	genres = GenreControllerTest.setupGenres(mockMvc, objectMapper, genreConverter);
    	artists = ArtistControllerTest.setupArtist(mockMvc, objectMapper, artistConverter, genres);
    	setupUsers(genres, artists);
    }

    private void setupUsers(List<Genre> genres, List<Artist> artists) {
    	var random = new Random();	
        userCreateInputs = UserRepositoryTest.getUsers().stream().map(user ->
        	UserCreateInput.builder()
	        	.username(user.getUsername())
				.email(user.getEmail())
				.password(user.getPassword())
				.profileImageUrl(user.getProfileImageUrl())
				.favoriteGenreIds(List.of(genres.get(random.nextInt(2)).getId(), genres.get(random.nextInt(2)).getId()))
				.followingArtistIds(List.of(artists.get(random.nextInt(2)).getId(), artists.get(random.nextInt(2)).getId()))
			.build()
        ).toList();
    	users = userCreateInputs.stream().map(input -> userConverter.toEntity(new User(), input)).collect(Collectors.toList());
    	userOutputs = users.stream().map(entity -> userConverter.toOutput(entity)).collect(Collectors.toList());
    }

    private <T> void checkList(ResultActions response, List<T> list, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s", jsonPath)).isArray())
    	.andExpect(jsonPath(String.format("%s.length()", jsonPath)).value(list.size()));
    	for (int i = 0; i < list.size(); i++) {
    	     response.andExpect(jsonPath(String.format("%s[%d]", jsonPath, i)).value(list.get(i)));
    	}
    }

    private void checkUserOutput(ResultActions response, UserOutput userOutput, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.id", jsonPath)).value(userOutput.getId()))
    	.andExpect(jsonPath(String.format("%s.username", jsonPath)).value(userOutput.getUsername()))
    	.andExpect(jsonPath(String.format("%s.email", jsonPath)).value(userOutput.getEmail()))
    	.andExpect(jsonPath(String.format("%s.password", jsonPath)).value(userOutput.getPassword()))
    	.andExpect(jsonPath(String.format("%s.profileImageUrl", jsonPath)).value(userOutput.getProfileImageUrl()));

    	checkList(response, userOutput.getFavoriteGenreIds(), String.format("%s.favoriteGenreIds", jsonPath));
    	checkList(response, userOutput.getFollowingArtistIds(), String.format("%s.followingArtistIds", jsonPath));
    }

    private void checkErrorResponse(ResultActions response, ErrorResponse errorResponse, String jsonPath) throws Exception {
    	response
    	.andExpect(jsonPath(String.format("%s.status", jsonPath)).value(errorResponse.getStatus()))
        .andExpect(jsonPath(String.format("%s.message", jsonPath)).value(errorResponse.getMessage()))
        .andExpect(jsonPath(String.format("%s.timestamp", jsonPath)).isNotEmpty());
    }

	@Test
	@Order(1)
	void givenValidUserCreateInputWhenPostRequestThenReturnsCreatedStatusAndUserOutput() throws Exception {
		for (int i = 0; i < userCreateInputs.size(); i++) {
			var userCreateInput = userCreateInputs.get(i);
			var userOutput = userOutputs.get(i);

			var response = mockMvc.perform(post("/users")
				    .contentType(MediaType.APPLICATION_JSON)
				    .content(objectMapper.writeValueAsString(userCreateInput)))
				    .andExpect(status().isCreated());

			var jsonResponse = response.andReturn().getResponse().getContentAsString();
			var id = (String) JsonPath.parse(jsonResponse).read("$.id");
			users.get(i).setId(id);
			userOutputs.get(i).setId(id);

			checkUserOutput(response, userOutput, "$");
		}
    }

	@Test
	@Order(2)
    void givenInvalidUserCreateInputWithAllFieldsEmptyWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{email=não pode ser nulo ou vazio, password=não pode ser nulo ou vazio, username=não pode ser nulo ou vazio}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(3)
    void givenInvalidUserCreateInputWithAllFieldsInvalidWhenPostRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var userCreateInput =
			UserCreateInput.builder()
				.username(" ")
				.email(" ")
				.password(" ")
				.favoriteGenreIds(List.of())
				.followingArtistIds(List.of())
	    	.build();

		var response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateInput)))
                .andExpect(status().isBadRequest());

		var errorResponse = ErrorResponse.badRequest("{email=não pode ser nulo ou vazio, favoriteGenreIds=não pode ser vazio se não for nulo, followingArtistIds=não pode ser vazio se não for nulo, password=não pode ser nulo ou vazio, username=não pode ser nulo ou vazio}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(4)
    void givenInvalidUserCreateInputWithInvalidFavoriteGenreIdsAndFollowingArtistIdsWhenPostRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var userCreateInput =
			UserCreateInput.builder()
				.username(users.get(0).getUsername())
				.email(users.get(0).getEmail())
				.password(users.get(0).getPassword())
				.profileImageUrl(users.get(0).getProfileImageUrl())
				.favoriteGenreIds(List.of("invalid_id_1", "invalid_id_2"))
				.followingArtistIds(List.of("invalid_id_1", "invalid_id_2"))
			.build();

		var response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateInput)))
                .andExpect(status().isNotFound());

		var errorResponse = ErrorResponse.notFound("Gêneros com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }
	
	@Test
	@Order(5)
    void givenInvalidUserCreateInputWithInvalidFollowingArtistIdsWhenPostRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var userCreateInput =
			UserCreateInput.builder()
				.username(users.get(0).getUsername())
				.email(users.get(0).getEmail())
				.password(users.get(0).getPassword())
				.profileImageUrl(users.get(0).getProfileImageUrl())
				.favoriteGenreIds(users.get(0).getFavoriteGenreIds())
				.followingArtistIds(List.of("invalid_id_1", "invalid_id_2"))
			.build();

		var response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateInput)))
                .andExpect(status().isNotFound());

		var errorResponse = ErrorResponse.notFound("Artistas com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(6)
    void whenGetAllRequestAfterPostRequestThenReturnsOkStatusAndListOfUserOutputs() throws Exception {
		getAll();
    }

	private void getAll() throws Exception {
		var response = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(userOutputs.size()));

        for (int i = 0; i < userOutputs.size(); i++) {
        	var userOutput = userOutputs.get(i);
	        checkUserOutput(response, userOutput, String.format("$[%d]", i));
	    }
	}

	@Test
	@Order(7)
    void givenValidUserIdWhenGetByIdAfterPostRequesThenReturnsOkStatusAndUserOutput() throws Exception {
        getById();
    }

	private void getById() throws Exception {
		for (var userOutput : userOutputs) {
			var response = mockMvc.perform(get("/users/get-by-id/{id}", userOutput.getId()))
				.andExpect(status().isOk());
			checkUserOutput(response, userOutput, "$");
		}
	}

	@Test
	@Order(8)
    void givenInvalidUserIdWhenGetByIdThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
    	var id = "invalid_id";
		var response = mockMvc.perform(get("/users/get-by-id/{id}", id))
    		.andExpect(status().isNotFound());

    	var errorResponse = ErrorResponse.notFound(String.format("Usuário com o ID %s não foi encontrado", id));
    	checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(9)
    void givenValidUserUpdateInputWhenPutRequestThenReturnsOkStatusAndUserOutput() throws Exception {
		var userUpdateInput =
			UserUpdateInput.builder()
    		    .username("musicLover89")
    		    .email("musiclover89@example.com")
    		    .password("newSecurePassword123")
    		    .profileImageUrl("https://example.com/images/musiclover89_new.jpg")
    		    .favoriteGenreIds(List.of(genres.get(0).getId(), genres.get(1).getId(), genres.get(2).getId()))
    		    .followingArtistIds(List.of(artists.get(0).getId(), artists.get(1).getId(), artists.get(2).getId()))
		    .build();

        var response = mockMvc.perform(put("/users/{id}", users.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userUpdateInput)))
            .andExpect(status().isOk());

        userConverter.toEntity(users.get(0), userUpdateInput);
        var updatedUserOutput = userConverter.toOutput(users.get(0));
        userOutputs.set(0, updatedUserOutput);

        checkUserOutput(response, updatedUserOutput, "$");
    }

	@Test
	@Order(10)
    void givenValidUserUpdateInputWithAllFieldsEmptyWhenPutRequestThenReturnsOkStatusAndUserOutput() throws Exception {
        var userOutput = userOutputs.get(0);

        var response = mockMvc.perform(put("/users/{id}", users.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isOk());
        checkUserOutput(response, userOutput, "$");
    }

	@Test
	@Order(11)
    void givenInvalidUserUpdateInputWithAllFieldsInvalidWhenPutRequestThenReturnsBadRequestStatusAndErrorResponse() throws Exception {
		var userUpdateInput =
			UserUpdateInput.builder()
				.username(" ")
				.email(" ")
				.password(" ")
				.favoriteGenreIds(List.of())
				.followingArtistIds(List.of())
    		.build();

        var response = mockMvc.perform(put("/users/{id}", users.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userUpdateInput)))
            .andExpect(status().isBadRequest());

        var errorResponse = ErrorResponse.badRequest("{email=não pode ser vazio ou conter somente espaços em branco se não for nulo, favoriteGenreIds=não pode ser vazio se não for nulo, followingArtistIds=não pode ser vazio se não for nulo, password=não pode ser vazio ou conter somente espaços em branco se não for nulo, username=não pode ser vazio ou conter somente espaços em branco se não for nulo}");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(12)
    void givenInvalidUserUpdateInputWithInvalidArtistIdsWhenPutRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var userUpdateInput =
			UserUpdateInput.builder()
				.favoriteGenreIds(List.of("invalid_id_1", "invalid_id_2"))
				.followingArtistIds(List.of("invalid_id_1", "invalid_id_2"))
			.build();

        var response = mockMvc.perform(put("/users/{id}", users.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userUpdateInput)))
            .andExpect(status().isNotFound());

        var errorResponse = ErrorResponse.notFound("Gêneros com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(13)
    void givenInvalidUserUpdateInputWithInvalidGenreIdsWhenPutRequestThenReturnsNotFoundStatusAndErrorResponse() throws Exception {
		var userUpdateInput =
			UserUpdateInput.builder()
				.followingArtistIds(List.of("invalid_id_1", "invalid_id_2"))
			.build();

        var response = mockMvc.perform(put("/users/{id}", users.get(0).getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userUpdateInput)))
            .andExpect(status().isNotFound());

        var errorResponse = ErrorResponse.notFound("Artistas com os IDs [invalid_id_1, invalid_id_2] não foram encontrados");
		checkErrorResponse(response, errorResponse, "$");
    }

	@Test
	@Order(14)
    void whenGetAllRequestAfterPutRequestThenReturnsOkStatusAndListOfUserOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(15)
    void givenValidGenreIdWhenGetByIdAfterPutRequestThenReturnsOkStatusAndUserOutput() throws Exception {
        getById();
    }

	@Test
	@Order(16)
    void givenValidUserIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/users/{id}", users.get(0).getId()))
            .andExpect(status().isOk());

		users.remove(0);
		userOutputs.remove(0);
    }

	@Test
	@Order(17)
    void givenInvalidUserIdWhenDeleteRequestThenReturnsOkStatus() throws Exception {
		mockMvc.perform(delete("/users/{id}", "invalid_id"))
            .andExpect(status().isOk());
    }

	@Test
	@Order(18)
    void whenGetAllRequestAfterDeleteRequestThenReturnsOkStatusAndListOfUserOutputs() throws Exception {
		getAll();
    }

	@Test
	@Order(19)
    void givenValidUserIdWhenGetByIdAfterDeleteRequestThenReturnsOkStatusAndUserOutput() throws Exception {
        getById();
    }

	@AfterAll
    public void cleanUp() {
        mongoTemplate.getDb().drop();
    }

	public static List<User> setupUsers(MockMvc mockMvc, ObjectMapper objectMapper, UserConverter userConverter, List<Genre> genres, List<Artist> artists) throws Exception {
		var userControllerTest = new UserControllerTest();
		return userControllerTest.createUsers(mockMvc, objectMapper, userConverter, genres, artists);
	}

	private List<User> createUsers(MockMvc mockMvc, ObjectMapper objectMapper, UserConverter userConverter, List<Genre> genres, List<Artist> artists) throws Exception {
		this.userConverter = userConverter;
		this.mockMvc = mockMvc;
		this.objectMapper = objectMapper;
		setupUsers(genres, artists);
		givenValidUserCreateInputWhenPostRequestThenReturnsCreatedStatusAndUserOutput();
		return users;
	}

}
