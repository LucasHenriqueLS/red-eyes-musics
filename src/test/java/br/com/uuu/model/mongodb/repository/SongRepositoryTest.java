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
		    		buildSong
		    			(
		    				List.of("64957a557f1d87179e9c77b9"),
		    				List.of("Michael Jackson"),
		    				"17b959647ad87179e9c7557f",
		    				List.of("d87179e9c79647a557f17b95", "77b969e7a595f1d87174579c"),
		    				"f17b9179e9c79645da557877",
		    				Map.of(
		    					"66c8b94ff5249d656c735e3a",
		    					buildSongDetails("Billie Jean", "Billie Jean is not my lover...", "64957a557f1d87179e9c77f9", List.of("87179e9c77f964957a557f1d", "57f1d87179e9c77f964957a5")),
		    					"5249d656c735e3a66c8b94ff",
		    					buildSongDetails("Billie Jean", "Billie Jean não é meu amor...", "1d87179e4957a557f9c77f96", List.of("f1df964957a587179e9c7757", "9e9c77f957a557f1d8717964"))
		    				),
		    				294,
		    				LocalDate.parse("1982-01-02"),
		    				"https://www.youtube.com/watch?v=Zi_XLOBDo_Y"
		    			),
		    			buildSong
		    			(
		    				List.of("64957a557f1d87179e9c77a1"),
		    				List.of("Ariana Grande", "Charles Anderson"),
		    				"27b959647ad87179e9c7456f",
		    				List.of("d87179e9c79647a557f17b96", "87b969e7a595f1d87174579d"),
		    				"f17b9179e9c79645da557877",
		    				Map.of(
		    					"66c8b94ff5249d656c735e3a",
		    					buildSongDetails("7 rings", "I want it, I got it...", "64957a557f1d87179e9c77f9", List.of("557f1d87179e9c77f964957a", "9c77f964957a557f1d87179e")),
		    					"5249d656c735e3a66c8b94ff",
		    					buildSongDetails("7 anéis", "Eu quero, eu consegui...", "1d87179e9c77f964957a557f", List.of("f964957a557f1d87179e9c77", "d87179e9c77f964957a557f1"))
		    				),
		    				178,
		    				LocalDate.parse("2019-01-18"),
		    				"https://www.youtube.com/watch?v=QYh6mYIJG2Y"
		    			),
		    			buildSong
		    			(
		    				List.of("64957a557f1d87179e9c77b2"),
		    				List.of("Hans Zimmer"),
		    				"37b959647ad87179e9c7557g",
		    				List.of("d87179e9c79647a557f17b94", "77b969e7a595f1d87174579e"),
		    				"f17b9179e9c79645da557877",
		    				Map.of(
		    					"66c8b94ff5249d656c735e3a",
		    					buildSongDetails("Time", "Instrumental", "64957a557f1d87179e9c77f9", List.of("79e9c77f964957a557f1d871", "f1d87179e9c77f964957a557")),
		    					"5249d656c735e3a66c8b94ff",
		    					buildSongDetails("Tempo", "Instrumental", "d87179e9c77f9649f157a557", List.of("64957f1d87179e9c7757a5f9", "9c77f79e964957a557f1d871"))
		    				),
		    				248,
		    				LocalDate.parse("2010-07-13"),
		    				"https://www.youtube.com/watch?v=RxabLA7UQ9k"
		    			)
		    		);
    }

    @BeforeEach
    void setUp() {
        songRepository.deleteAll();
        songs = getSongs();
    }

	private static Song buildSong(List<String> artistIds, List<String> composerNames, String albumId, List<String> genreIds, String originalLanguageId, Map<String, SongDetails> detailsByLanguageId, Integer durationInSeconds, LocalDate releaseDate, String videoLink) {
		var song = new Song();
		song.setArtistIds(artistIds);
		song.setComposerNames(composerNames);
		song.setAlbumId(albumId);
		song.setGenreIds(genreIds);
		song.setOriginalLanguageId(originalLanguageId);
		song.setDetailsByLanguageId(detailsByLanguageId);
		song.setDurationInSeconds(durationInSeconds);
		song.setReleaseDate(releaseDate);
		song.setVideoLink(videoLink);
        return song;
	}

	private static SongDetails buildSongDetails(String title, String lyric, String submitterId, List<String> proofreaderIds) {
		var songDetails = new SongDetails();
		songDetails.setTitle(title);
		songDetails.setLyric(lyric);
		songDetails.setSubmitterId(submitterId);
		songDetails.setProofreaderIds(proofreaderIds);
		return songDetails;
	}

	private void checkSong(Song entity, Song song) {
		assertThat(entity.getId()).isEqualTo(song.getId());
		assertThat(entity.getArtistIds()).isEqualTo(song.getArtistIds());
		assertThat(entity.getComposerNames()).isEqualTo(song.getComposerNames());
		assertThat(entity.getAlbumId()).isEqualTo(song.getAlbumId());
		assertThat(entity.getGenreIds()).isEqualTo(song.getGenreIds());
		assertThat(entity.getOriginalLanguageId()).isEqualTo(song.getOriginalLanguageId());
		assertThat(entity.getDetailsByLanguageId()).isEqualTo(song.getDetailsByLanguageId());
		assertThat(entity.getDurationInSeconds()).isEqualTo(song.getDurationInSeconds());
		assertThat(entity.getReleaseDate()).isEqualTo(song.getReleaseDate());
		assertThat(entity.getVideoLink()).isEqualTo(song.getVideoLink());
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
    			checkSong(entity, song);
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
	    	checkSong(allSong.get(i), songs.get(i));
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
