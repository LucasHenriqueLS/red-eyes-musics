package br.com.uuu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import br.com.uuu.converter.AlbumConverter;
import br.com.uuu.json.input.album.AlbumCreateInput;
import br.com.uuu.json.input.album.AlbumUpdateInput;
import br.com.uuu.json.output.album.AlbumOutput;
import br.com.uuu.model.mongodb.entity.Album;
import br.com.uuu.model.mongodb.repository.AlbumRepository;
import br.com.uuu.model.mongodb.repository.AlbumRepositoryTest;


public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;
    
    @Mock
    private AlbumConverter albumConverter;

    @InjectMocks
    private AlbumService albumService;

    private List<Album> albums;
    
    private List<AlbumCreateInput> albumCreateInputs;
    
    private List<AlbumOutput> albumOutputs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        albums = AlbumRepositoryTest.getAlbums();
        var counter = new AtomicInteger(1);
        albums.forEach(album -> album.setId(String.valueOf(counter.getAndIncrement())));
        albumCreateInputs = albums.stream().map(album ->
		    	AlbumCreateInput.builder()
					.title(album.getTitle())
					.releaseDate(album.getReleaseDate())
					.artistIds(album.getArtistIds())
					.coverUrl(album.getCoverUrl())
					.genreIds(album.getGenreIds())
					.recordCompanyName(album.getRecordCompanyName())
				.build()
			).toList();
        albumOutputs = albums.stream().map(album ->
			    	AlbumOutput.builder()
			    	.id(album.getId())
			    	.title(album.getTitle())
					.releaseDate(album.getReleaseDate())
					.artistIds(album.getArtistIds())
					.coverUrl(album.getCoverUrl())
					.genreIds(album.getGenreIds())
					.recordCompanyName(album.getRecordCompanyName())
				.build()
			).toList();
    }

    private void checkAlbum(AlbumOutput output, AlbumOutput album) {
		assertThat(output.getId()).isEqualTo(album.getId());
		assertThat(output.getTitle()).isEqualTo(album.getTitle());
		assertThat(output.getReleaseDate()).isEqualTo(album.getReleaseDate());
		assertThat(output.getArtistIds()).isEqualTo(album.getArtistIds());
		assertThat(output.getCoverUrl()).isEqualTo(album.getCoverUrl());
		assertThat(output.getGenreIds()).isEqualTo(album.getGenreIds());
		assertThat(output.getRecordCompanyName()).isEqualTo(album.getRecordCompanyName());
	}

    @Test
    void whenSaveFromInputToOutputThenAlbumIsSaved() {
    	var album = albums.get(0);
    	var albumCreateInput = albumCreateInputs.get(0);
    	var albumOutput = albumOutputs.get(0);
        
    	when(albumConverter.toEntity(any(Album.class), eq(albumCreateInput))).thenReturn(album);
        when(albumRepository.save(album)).thenReturn(album);
        when(albumConverter.toOutput(album)).thenReturn(albumOutput);

        var output = albumService.saveFromInputToOutput(albumCreateInput);

        assertThat(output).isNotNull();
        checkAlbum(output, albumOutput);
        
        verify(albumConverter).toEntity(any(Album.class), eq(albumCreateInput));
        verify(albumRepository).save(album);
        verify(albumConverter).toOutput(album);
    }

    @Test
    void whenGetByIdToOutputThenReturnAlbumOutput() {
    	var album = albums.get(0);
    	var albumOutput = albumOutputs.get(0);

        when(albumRepository.findById("1")).thenReturn(Optional.of(album));
        when(albumConverter.toOutput(album)).thenReturn(albumOutput);

        var output = albumService.getByIdToOutput("1");

        assertThat(output).isNotNull();
        checkAlbum(output, albumOutput);

        verify(albumRepository).findById("1");
        verify(albumConverter).toOutput(album);
    }

    @Test
    void whenInvalidGetByIdToOutputThenThrowException() {
        when(albumRepository.findById("4")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            albumService.getByIdToOutput("4");
        });

        verify(albumRepository).findById("4");
    }

    @Test
    void whenGetAllToOutputThenReturnAllAlbumOutputs() {
        when(albumRepository.findAll()).thenReturn(albums);
        when(albumConverter.toOutput(albums)).thenReturn(albumOutputs);

        var allAlbum = albumService.getAllToOutput();

        assertThat(allAlbum).hasSize(albums.size());
        assertThat(allAlbum).containsAll(albumOutputs);

        verify(albumRepository).findAll();
        verify(albumConverter).toOutput(albums);
    }

    @Test
    void whenUpdateFromInputToOutputThenAlbumIsUpdated() {
    	var album = albums.get(0);
    	var updatedAlbum = albums.get(2);
    	var albumOutput = albumOutputs.get(2);
    	var albumUpdateInput =
    			AlbumUpdateInput.builder()
    			.title(Optional.of("Thriller (Special Edition)"))
    			.releaseDate(Optional.of(LocalDate.parse("1982-11-30")))
    			.artistIds(Optional.of(List.of("a87179e9c79647a557f17b95")))
    			.coverUrl(Optional.of("https://example.com/images/thriller_special_edition.jpg"))
    			.genreIds(Optional.of(List.of("b969e7a595f1d87174579c", "c87179e9c79647a557f17b95", "g87179e9c79647a557f17b95")))
    			.recordCompanyName(Optional.of("Epic Records"))
    			.build();
    	
    	when(albumConverter.toEntity(album, albumUpdateInput)).thenReturn(updatedAlbum);
        when(albumRepository.findById("1")).thenReturn(Optional.of(album));
        when(albumRepository.save(updatedAlbum)).thenReturn(updatedAlbum);
        when(albumConverter.toOutput(updatedAlbum)).thenReturn(albumOutput);

        var output = albumService.updateFromInputToOutput("1", albumUpdateInput);

        assertThat(output).isNotNull();
        checkAlbum(output, albumOutput);

        verify(albumConverter).toEntity(album, albumUpdateInput);
        verify(albumRepository).findById("1");
        verify(albumRepository).save(updatedAlbum);
        verify(albumConverter).toOutput(updatedAlbum);
    }

    @Test
    void whenDeleteAlbumThenAlbumIsDeleted() {
        albumService.delete("1");
        verify(albumRepository).deleteById("1");
    }

}
