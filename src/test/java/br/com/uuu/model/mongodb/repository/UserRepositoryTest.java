package br.com.uuu.model.mongodb.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.uuu.model.mongodb.entity.User;
import br.com.uuu.util.TestUtils;

@DataMongoTest
@ActiveProfiles("unit-test")
public class UserRepositoryTest {

	@Autowired
    private UserRepository userRepository;

    private static List<User> users;
    
    public static List<User> getUsers() {
    	return List.of(
    				User.builder()
    					.username("musicLover89")
    					.email("musiclover89@example.com")
    					.password("password123")
    					.profileImageUrl("https://example.com/images/musiclover89.jpg")
    					.favoriteGenreIds(List.of("64957a557f1d87179e9c77b9", "d87179e9c79647a557f17b95"))
    					.followingArtistIds(List.of("95f1d87179e7a54579c77b96", "77b969e7a595f1d87174579c"))
    				.build(),
    				User.builder()
	    			    .username("popFanatic21")
	    			    .email("popfanatic21@example.com")
	    			    .password("securePassword!")
	    			    .profileImageUrl("https://example.com/images/popfanatic21.jpg")
	    			    .favoriteGenreIds(List.of("77b969e7a595f1d87174579c", "d87179e9c79647a557f17b95"))
	    			    .followingArtistIds(List.of("d87179e9c79647a557f17b95", "64957a557f1d87179e9c77b9"))
    			    .build(),
    			    User.builder()
	    			    .username("composerFan44")
	    			    .email("composerfan44@example.com")
	    			    .password("comp0serLove!")
	    			    .profileImageUrl("https://example.com/images/composerfan44.jpg")
	    			    .favoriteGenreIds(List.of("95f1d87179e7a54579c77b96", "d87179e9c79647a557f17b95"))
	    			    .followingArtistIds(List.of("77b969e7a595f1d87174579c", "64957a557f1d87179e9c77b9"))
    			    .build()			
		    	);
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        users = getUsers();
    }

	private Optional<User> findById(String id) {
		return userRepository.findById(id);
	}

	private List<User> findAll() {
		return userRepository.findAll();
	}

	private void save(User user) {
		userRepository.save(user);
	}

	private void deleteById(String id) {
		userRepository.deleteById(id);
	}

	@Test
    void whenSave_thenUserIsSaved() {
		for (var user : users) {
			save(user);
			assertThat(user.getId()).isNotBlank();
		}
    }

    @Test
    void whenFindById_thenReturnUser() {
    	for (var user : users) {
    		save(user);
    		var optional = findById(user.getId());
    		assertThat(optional).isPresent();
    		optional.ifPresent(entity -> {
    			TestUtils.checkExpectedResults(entity, user);
    		});
    	}
    }

    @Test
    void whenFindByInvalidId_thenReturnEmpty() {
        var optional = findById("invalid_id");
        assertThat(optional).isNotPresent();
    }

    @Test
	void whenFindAll_thenReturnAllUser() {
    	for (var user : users) {
    		save(user);
    	}
	    var allUser = findAll();
	    assertThat(allUser).hasSize(users.size());
	    for (int i = 0; i < users.size(); i++) {
	    	TestUtils.checkExpectedResults(allUser.get(i), users.get(i));
	    }
	}

    @Test
    void whenDeleteById_thenUserIsDeleted() {
    	for (var user : users) {
    		save(user);
    		deleteById(user.getId());
    		var optional = findById(user.getId());
    		assertThat(optional).isNotPresent();
    	}
    }

}
