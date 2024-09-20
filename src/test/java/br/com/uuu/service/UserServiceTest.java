package br.com.uuu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.converter.UserConverter;
import br.com.uuu.json.input.user.UserCreateInput;
import br.com.uuu.json.input.user.UserUpdateInput;
import br.com.uuu.json.output.user.UserOutput;
import br.com.uuu.model.mongodb.entity.User;
import br.com.uuu.model.mongodb.repository.UserRepository;
import br.com.uuu.model.mongodb.repository.UserRepositoryTest;
import br.com.uuu.util.TestUtils;


public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private UserService userService;

    private List<User> users;
    
    private List<UserCreateInput> userCreateInputs;
    
    private List<UserOutput> userOutputs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        users = UserRepositoryTest.getUsers();
        var i = new AtomicInteger(1);
        users.forEach(user -> user.setId(String.valueOf(i.getAndIncrement())));
        userCreateInputs = users.stream().map(user ->
		    	UserCreateInput.builder()
			    	.username(user.getUsername())
					.email(user.getEmail())
					.password(user.getPassword())
					.profileImageUrl(user.getProfileImageUrl())
					.favoriteGenreIds(user.getFavoriteGenreIds())
					.followingArtistIds(user.getFollowingArtistIds())
				.build()
			).toList();
        userOutputs = users.stream().map(user ->
			    UserOutput.builder()
			    	.id(user.getId())
			    	.username(user.getUsername())
					.email(user.getEmail())
					.password(user.getPassword())
					.profileImageUrl(user.getProfileImageUrl())
					.favoriteGenreIds(user.getFavoriteGenreIds())
					.followingArtistIds(user.getFollowingArtistIds())
				.build()
			).toList();
    }

    @Test
    void whenSaveFromInputToOutputThenUserIsSaved() {
    	var user = users.get(0);
    	var userCreateInput = userCreateInputs.get(0);
    	var userOutput = userOutputs.get(0);
        
    	when(userConverter.toEntity(any(User.class), eq(userCreateInput))).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userConverter.toOutput(user)).thenReturn(userOutput);

        var output = userService.saveFromInputToOutput(userCreateInput);

        assertThat(output).isNotNull();
        TestUtils.checkExpectedResults(output, userOutput);
        
        verify(userConverter).toEntity(any(User.class), eq(userCreateInput));
        verify(userRepository).save(user);
        verify(userConverter).toOutput(user);
    }

    @Test
    void whenGetByIdToOutputThenReturnUserOutput() {
    	var user = users.get(0);
    	var userOutput = userOutputs.get(0);

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userConverter.toOutput(user)).thenReturn(userOutput);

        var output = userService.getByIdToOutput("1");

        assertThat(output).isNotNull();
        TestUtils.checkExpectedResults(output, userOutput);

        verify(userRepository).findById("1");
        verify(userConverter).toOutput(user);
    }

    @Test
    void whenInvalidGetByIdToOutputThenThrowException() {
        when(userRepository.findById("4")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            userService.getByIdToOutput("4");
        });

        verify(userRepository).findById("4");
    }

    @Test
    void whenGetAllToOutputThenReturnAllUserOutputs() {
        when(userRepository.findAll()).thenReturn(users);
        when(userConverter.toOutput(users)).thenReturn(userOutputs);

        var allUser = userService.getAllToOutput();

        assertThat(allUser).hasSize(users.size());
        assertThat(allUser).containsAll(userOutputs);

        verify(userRepository).findAll();
        verify(userConverter).toOutput(users);
    }

    @Test
    void whenUpdateFromInputToOutputThenUserIsUpdated() {
    	var user = users.get(0);
    	var updatedUser = users.get(2);
    	var userOutput = userOutputs.get(2);
    	var userUpdateInput =
    			UserUpdateInput.builder()
	    		    .username("musicLover89")
	    		    .email("musiclover89@example.com")
	    		    .password("newSecurePassword123")
	    		    .profileImageUrl("https://example.com/images/musiclover89_new.jpg")
	    		    .favoriteGenreIds(List.of("d87179e9c79647a557f17b95", "77b969e7a595f1d87174579c"))
	    		    .followingArtistIds(List.of("64957a557f1d87179e9c77b9", "77b969e7a595f1d87174579c"))
    		    .build();

    	when(userConverter.toEntity(user, userUpdateInput)).thenReturn(updatedUser);
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userConverter.toOutput(updatedUser)).thenReturn(userOutput);

        var output = userService.updateFromInputToOutput("1", userUpdateInput);

        assertThat(output).isNotNull();
        TestUtils.checkExpectedResults(output, userOutput);

        verify(userConverter).toEntity(user, userUpdateInput);
        verify(userRepository).findById("1");
        verify(userRepository).save(updatedUser);
        verify(userConverter).toOutput(updatedUser);
    }

    @Test
    void whenDeleteUserThenUserIsDeleted() {
        userService.delete("1");
        verify(userRepository).deleteById("1");
    }

}
