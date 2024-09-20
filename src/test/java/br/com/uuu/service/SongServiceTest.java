package br.com.uuu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.converter.SongConverter;
import br.com.uuu.json.input.song.SongCreateInput;
import br.com.uuu.json.input.song.SongDetailsCreateInput;
import br.com.uuu.json.input.song.SongDetailsUpdateInput;
import br.com.uuu.json.input.song.SongUpdateInput;
import br.com.uuu.json.output.song.SongDetailsOutput;
import br.com.uuu.json.output.song.SongOutput;
import br.com.uuu.model.mongodb.entity.Song;
import br.com.uuu.model.mongodb.repository.SongRepository;
import br.com.uuu.model.mongodb.repository.SongRepositoryTest;


public class SongServiceTest {

    @Mock
    private SongRepository songRepository;
    
    @Mock
    private SongConverter songConverter;

    @InjectMocks
    private SongService songService;

    private List<Song> songs;
    
    private List<SongCreateInput> songCreateInputs;
    
    private List<SongOutput> songOutputs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        songs = SongRepositoryTest.getSongs();
        var i = new AtomicInteger(1);
        songs.forEach(song -> song.setId(String.valueOf(i.getAndIncrement())));
        songCreateInputs = songs.stream().map(song ->
		    	SongCreateInput.builder()
			    	.artistIds(song.getArtistIds())
					.composerNames(song.getComposerNames())
					.albumId(song.getAlbumId())
					.genreIds(song.getGenreIds())
					.originalLanguageId(song.getOriginalLanguageId())
					.detailsByLanguageId(
						song.getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
							Map.Entry::getKey, entry ->
								SongDetailsCreateInput.builder()
									.title(entry.getValue().getTitle())
									.lyric(entry.getValue().getLyric())
									.submitterId(entry.getValue().getSubmitterId())
								.build()
						))
					)
					.durationInSeconds(song.getDurationInSeconds())
					.releaseDate(song.getReleaseDate())
					.videoUrl(song.getVideoUrl())
				.build()
			).toList();
        songOutputs = songs.stream().map(song ->
			    SongOutput.builder()
			    	.id(song.getId())
			    	.artistIds(song.getArtistIds())
					.composerNames(song.getComposerNames())
					.albumId(song.getAlbumId())
					.genreIds(song.getGenreIds())
					.originalLanguageId(song.getOriginalLanguageId())
					.detailsByLanguageId(
						song.getDetailsByLanguageId().entrySet().stream().collect(Collectors.toMap(
							Map.Entry::getKey, entry ->
								SongDetailsOutput.builder()
									.title(entry.getValue().getTitle())
									.lyric(entry.getValue().getLyric())
									.submitterId(entry.getValue().getSubmitterId())
									.proofreaderIds(entry.getValue().getProofreaderIds())
								.build()
						))
					)
					.durationInSeconds(song.getDurationInSeconds())
					.releaseDate(song.getReleaseDate())
					.videoUrl(song.getVideoUrl())
				.build()
			).toList();
    }

    private <T> void checkOutput(T output, T song) {
    	assertThat(output).usingRecursiveComparison().isEqualTo(song);
	}

    @Test
    void whenSaveFromInputToOutputThenSongIsSaved() {
    	var song = songs.get(0);
    	var songCreateInput = songCreateInputs.get(0);
    	var songOutput = songOutputs.get(0);
        
    	when(songConverter.toEntity(any(Song.class), eq(songCreateInput))).thenReturn(song);
        when(songRepository.save(song)).thenReturn(song);
        when(songConverter.toOutput(song)).thenReturn(songOutput);

        var output = songService.saveFromInputToOutput(songCreateInput);

        assertThat(output).isNotNull();
        checkOutput(output, songOutput);
        
        verify(songConverter).toEntity(any(Song.class), eq(songCreateInput));
        verify(songRepository).save(song);
        verify(songConverter).toOutput(song);
    }

    @Test
    void whenGetByIdToOutputThenReturnSongOutput() {
    	var song = songs.get(0);
    	var songOutput = songOutputs.get(0);

        when(songRepository.findById("1")).thenReturn(Optional.of(song));
        when(songConverter.toOutput(song)).thenReturn(songOutput);

        var output = songService.getByIdToOutput("1");

        assertThat(output).isNotNull();
        checkOutput(output, songOutput);

        verify(songRepository).findById("1");
        verify(songConverter).toOutput(song);
    }

    @Test
    void whenInvalidGetByIdToOutputThenThrowException() {
        when(songRepository.findById("4")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            songService.getByIdToOutput("4");
        });

        verify(songRepository).findById("4");
    }

    @Test
    void whenGetAllToOutputThenReturnAllSongOutputs() {
        when(songRepository.findAll()).thenReturn(songs);
        when(songConverter.toOutput(songs)).thenReturn(songOutputs);

        var allSong = songService.getAllToOutput();

        assertThat(allSong).hasSize(songs.size());
        assertThat(allSong).containsAll(songOutputs);

        verify(songRepository).findAll();
        verify(songConverter).toOutput(songs);
    }

    @Test
    void whenUpdateFromInputToOutputThenSongIsUpdated() {
    	var song = songs.get(0);
    	var updatedSong = songs.get(2);
    	var songOutput = songOutputs.get(2);
    	var songUpdateInput =
    		SongUpdateInput.builder()
	    		.artistIds(List.of("64957a557f1d87179e9c77b9"))
				.composerNames(List.of("Michael Joseph Jackson"))
				.albumId("87179e9c7557f17b959647ad")
				.genreIds(List.of("79647a557f17b95d87179e9c", "e7a595f1d87174579c77b969","57f1d87179e9c77f964957a5"))
				.originalLanguageId("f17b9179e9c79645da557877")
				.detailsByLanguageId(
					Map.of(
						"66c8b94ff5249d656c735e3a",
						SongDetailsUpdateInput.builder()
							.title("Billie Jean")
							.lyric("Billie Jean is not my lover... She's just a girl who claims that I am the one...")
							.submitterId("64957a557f1d87179e9c77f9")
							.proofreaderIds(List.of("87179e9c77f964957a557f1d", "57f1d87179e9c77f964957a5"))
						.build(),
						"5249d656c735e3a66c8b94ff",
						SongDetailsUpdateInput.builder()
								.title("Billie Jean")
								.lyric("Billie Jean não é minha amante... Ela é apenas uma garota que afirma que eu sou o único...")
								.submitterId("1d87179e4957a557f9c77f96")
								.proofreaderIds(List.of("f1df964957a587179e9c7757", "9e9c77f957a557f1d8717964", "64957a557f1d87179e9c77f9"))
							.build()
					)
				).durationInSeconds(297)
				.releaseDate(LocalDate.parse("1982-01-02"))
				.videoLink("https://www.youtube.com/watch?v=Si_BDXLOo_T")
    		.build();

    	when(songConverter.toEntity(song, songUpdateInput)).thenReturn(updatedSong);
        when(songRepository.findById("1")).thenReturn(Optional.of(song));
        when(songRepository.save(updatedSong)).thenReturn(updatedSong);
        when(songConverter.toOutput(updatedSong)).thenReturn(songOutput);

        var output = songService.updateFromInputToOutput("1", songUpdateInput);

        assertThat(output).isNotNull();
        checkOutput(output, songOutput);

        verify(songConverter).toEntity(song, songUpdateInput);
        verify(songRepository).findById("1");
        verify(songRepository).save(updatedSong);
        verify(songConverter).toOutput(updatedSong);
    }

    @Test
    void whenDeleteSongThenSongIsDeleted() {
        songService.delete("1");
        verify(songRepository).deleteById("1");
    }

}
