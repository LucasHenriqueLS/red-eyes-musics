package br.com.uuu.model.mongodb.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.uuu.model.mongodb.entity.Song;
import br.com.uuu.model.mongodb.util.SongDetails;

@DataMongoTest
@ActiveProfiles("unit-test")
public class SongRepositoryTest {

	@Autowired
    private SongRepository songRepository;

    private static List<Song> songs;
    
    public static List<Song> getSongs() {
    	return List.of(
    				Song.builder()
    					.artistIds(List.of("64957a557f1d87179e9c77b9"))
    					.composerNames(List.of("Michael Jackson"))
    					.albumId("17b959647ad87179e9c7557f")
    					.genreIds(List.of("d87179e9c79647a557f17b95", "77b969e7a595f1d87174579c"))
    					.originalLanguageId("f17b9179e9c79645da557877")
    					.detailsByLanguageId(
    						Map.of(
								"66c8b94ff5249d656c735e3a",
								SongDetails.builder()
									.title("Billie Jean")
									.lyric("Billie Jean não é minha amante...")
									.submitterId("64957a557f1d87179e9c77f9")
									.proofreaderIds(List.of("87179e9c77f964957a557f1d", "57f1d87179e9c77f964957a5"))
								.build(),
								"5249d656c735e3a66c8b94ff",
								SongDetails.builder()
	   								.title("Billie Jean")
	   								.lyric("Billie Jean não é meu amor...")
	   								.submitterId("1d87179e4957a557f9c77f96")
	   								.proofreaderIds(List.of("f1df964957a587179e9c7757", "9e9c77f957a557f1d8717964"))
	   							.build()
    						)
    					).durationInSeconds(294)
    					.releaseDate(LocalDate.parse("1982-01-02"))
    					.videoLink("https://www.youtube.com/watch?v=Zi_XLOBDo_Y")
    				.build(),
    				Song.builder()
						.artistIds(List.of("64957a557f1d87179e9c77a1"))
						.composerNames(List.of("Ariana Grande", "Charles Anderson"))
						.albumId("27b959647ad87179e9c7456f")
						.genreIds(List.of("d87179e9c79647a557f17b96", "87b969e7a595f1d87174579d"))
						.originalLanguageId("f17b9179e9c79645da557877")
						.detailsByLanguageId(
							Map.of(
								"66c8b94ff5249d656c735e3a",
								SongDetails.builder()
									.title("7 rings")
									.lyric("I want it, I got it...")
									.submitterId("66c8b94ff5249d656c735e3a")
									.proofreaderIds(List.of("557f1d87179e9c77f964957a", "9c77f964957a557f1d87179e"))
								.build(),
								"5249d656c735e3a66c8b94ff",
								SongDetails.builder()
	   								.title("7 anéis")
	   								.lyric("Eu quero, eu consegui...")
	   								.submitterId("1d87179e9c77f964957a557f")
	   								.proofreaderIds(List.of("f964957a557f1d87179e9c77", "d87179e9c77f964957a557f1"))
	   							.build()
							)
						).durationInSeconds(178)
						.releaseDate(LocalDate.parse("2019-01-18"))
						.videoLink("https://www.youtube.com/watch?v=QYh6mYIJG2Y")
					.build(),
					Song.builder()
						.artistIds(List.of("64957a557f1d87179e9c77b2"))
						.composerNames(List.of("Hans Zimmer"))
						.albumId("37b959647ad87179e9c7557g")
						.genreIds(List.of("d87179e9c79647a557f17b94", "77b969e7a595f1d87174579e"))
						.originalLanguageId("f17b9179e9c79645da557877")
						.detailsByLanguageId(
							Map.of(
								"66c8b94ff5249d656c735e3a",
								SongDetails.builder()
									.title("Time")
									.lyric("Instrumental")
									.submitterId("64957a557f1d87179e9c77f9")
									.proofreaderIds(List.of("79e9c77f964957a557f1d871", "f1d87179e9c77f964957a557"))
								.build(),
								"5249d656c735e3a66c8b94ff",
								SongDetails.builder()
	   								.title("Tempo")
	   								.lyric("Instrumental")
	   								.submitterId("d87179e9c77f9649f157a557")
	   								.proofreaderIds(List.of("64957f1d87179e9c7757a5f9", "9c77f79e964957a557f1d871"))
	   							.build()
							)
						).durationInSeconds(248)
						.releaseDate(LocalDate.parse("2010-07-13"))
						.videoLink("https://www.youtube.com/watch?v=RxabLA7UQ9k")
					.build()
		    	);
    }

    @BeforeEach
    void setUp() {
        songRepository.deleteAll();
        songs = getSongs();
    }

	private <T> void checkOutput(T entity, T song) {
		assertThat(entity).usingRecursiveComparison().isEqualTo(song);
	}

	private Optional<Song> findById(String id) {
		return songRepository.findById(id);
	}

	private List<Song> findAll() {
		return songRepository.findAll();
	}

	private void save(Song song) {
		songRepository.save(song);
	}

	private void deleteById(String id) {
		songRepository.deleteById(id);
	}

	@Test
    void whenSave_thenSongIsSaved() {
		for (var song : songs) {
			save(song);
			assertThat(song.getId()).isNotBlank();
		}
    }

    @Test
    void whenFindById_thenReturnSong() {
    	for (var song : songs) {
    		save(song);
    		var optional = findById(song.getId());
    		assertThat(optional).isPresent();
    		optional.ifPresent(entity -> {
    			checkOutput(entity, song);
    		});
    	}
    }

    @Test
    void whenFindByInvalidId_thenReturnEmpty() {
        var optional = findById("invalid_id");
        assertThat(optional).isNotPresent();
    }

    @Test
	void whenFindAll_thenReturnAllSong() {
    	for (var song : songs) {
    		save(song);
    	}
	    var allSong = findAll();
	    assertThat(allSong).hasSize(songs.size());
	    for (int i = 0; i < songs.size(); i++) {
	    	checkOutput(allSong.get(i), songs.get(i));
	    }
	}

    @Test
    void whenDeleteById_thenSongIsDeleted() {
    	for (var song : songs) {
    		save(song);
    		deleteById(song.getId());
    		var optional = findById(song.getId());
    		assertThat(optional).isNotPresent();
    	}
    }

}
