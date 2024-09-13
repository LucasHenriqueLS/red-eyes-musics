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
import br.com.uuu.json.input.song.SongUpdateInput;
import br.com.uuu.json.output.song.SongDetailsOutput;
import br.com.uuu.json.output.song.SongOutput;
import br.com.uuu.model.mongodb.entity.Song;
import br.com.uuu.model.mongodb.repository.SongRepository;
import br.com.uuu.model.mongodb.repository.SongRepositoryTest;
import br.com.uuu.model.mongodb.util.SongDetails;


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
							Map.Entry::getKey,
							entry -> {
								var value = entry.getValue();
								return SongDetailsOutput.builder()
										.title(value.getTitle())
										.lyric(value.getLyric())
										.submitterId(value.getSubmitterId())
										.proofreaderIds(value.getProofreaderIds())
									   .build();
							}
						))
					)
					.durationInSeconds(song.getDurationInSeconds())
					.releaseDate(song.getReleaseDate())
					.videoLink(song.getVideoLink())
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
					.detailsByLanguageId(null)
					.durationInSeconds(song.getDurationInSeconds())
					.releaseDate(song.getReleaseDate())
					.videoLink(song.getVideoLink())
				.build()
			).toList();
    }

    private void checkSong(SongOutput output, SongOutput song) {
		assertThat(output.getId()).isEqualTo(song.getId());
		assertThat(output.getTitle()).isEqualTo(song.getTitle());
		assertThat(output.getReleaseDate()).isEqualTo(song.getReleaseDate());
		assertThat(output.getArtistIds()).isEqualTo(song.getArtistIds());
		assertThat(output.getCoverUrl()).isEqualTo(song.getCoverUrl());
		assertThat(output.getGenreIds()).isEqualTo(song.getGenreIds());
		assertThat(output.getRecordCompanyName()).isEqualTo(song.getRecordCompanyName());
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
        checkSong(output, songOutput);
        
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
        checkSong(output, songOutput);

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
    			.title("Thriller (Special Edition)")
    			.releaseDate(LocalDate.parse("1982-11-30"))
    			.artistIds(List.of("a87179e9c79647a557f17b95"))
    			.coverUrl("https://example.com/images/thriller_special_edition.jpg")
    			.genreIds(List.of("b969e7a595f1d87174579c", "c87179e9c79647a557f17b95", "g87179e9c79647a557f17b95"))
    			.recordCompanyName("Epic Records")
    		.build();

    	when(songConverter.toEntity(song, songUpdateInput)).thenReturn(updatedSong);
        when(songRepository.findById("1")).thenReturn(Optional.of(song));
        when(songRepository.save(updatedSong)).thenReturn(updatedSong);
        when(songConverter.toOutput(updatedSong)).thenReturn(songOutput);

        var output = songService.updateFromInputToOutput("1", songUpdateInput);

        assertThat(output).isNotNull();
        checkSong(output, songOutput);

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
