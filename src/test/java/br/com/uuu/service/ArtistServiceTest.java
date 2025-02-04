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

import br.com.uuu.converter.ArtistConverter;
import br.com.uuu.json.input.artist.ArtistCreateInput;
import br.com.uuu.json.input.artist.ArtistUpdateInput;
import br.com.uuu.json.output.artist.ArtistOutput;
import br.com.uuu.model.mongodb.entity.Artist;
import br.com.uuu.model.mongodb.repository.ArtistRepository;
import br.com.uuu.model.mongodb.repository.ArtistRepositoryTest;

class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;
    
    @Mock
    private ArtistConverter artistConverter;

    @InjectMocks
    private ArtistService artistService;

    private List<Artist> artists;
    
    private List<ArtistCreateInput> artistCreateInputs;
    
    private List<ArtistOutput> artistOutputs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        artists = ArtistRepositoryTest.getArtists();
        var i = new AtomicInteger(1);
        artists.forEach(artist -> artist.setId(String.valueOf(i.getAndIncrement())));
        artistCreateInputs = artists.stream().map(entity ->
        		ArtistCreateInput.builder()
        			.names(entity.getNames())
        			.bio(entity.getBio())
        			.genreIds(entity.getGenreIds())
        			.imageUrl(entity.getImageUrl())
        		.build()
        	).toList();
        artistOutputs = artists.stream().map(entity ->
        		ArtistOutput.builder()
        			.id(entity.getId())
        			.names(entity.getNames())
        			.bio(entity.getBio())
        			.genreIds(entity.getGenreIds())
        			.imageUrl(entity.getImageUrl())
        		.build()
        	).toList();
    }

    private void checkArtist(ArtistOutput output, ArtistOutput artist) {
		assertThat(output.getId()).isEqualTo(artist.getId());
		assertThat(output.getNames()).isEqualTo(artist.getNames());
		assertThat(output.getBio()).isEqualTo(artist.getBio());
		assertThat(output.getGenreIds()).isEqualTo(artist.getGenreIds());
		assertThat(output.getImageUrl()).isEqualTo(artist.getImageUrl());
	}

    @Test
    void whenSaveFromInputToOutputThenArtistIsSaved() {
    	var artist = artists.get(0);
    	var artistCreateInput = artistCreateInputs.get(0);
    	var artistOutput = artistOutputs.get(0);
        
    	when(artistConverter.toEntity(any(Artist.class), eq(artistCreateInput))).thenReturn(artist);
        when(artistRepository.save(artist)).thenReturn(artist);
        when(artistConverter.toOutput(artist)).thenReturn(artistOutput);

        var output = artistService.saveFromInputToOutput(artistCreateInput);

        assertThat(output).isNotNull();
        checkArtist(output, artistOutput);
        
        verify(artistConverter).toEntity(any(Artist.class), eq(artistCreateInput));
        verify(artistRepository).save(artist);
        verify(artistConverter).toOutput(artist);
    }

    @Test
    void whenGetByIdToOutputThenReturnArtistOutput() {
    	var artist = artists.get(0);
    	var artistOutput = artistOutputs.get(0);

        when(artistRepository.findById("1")).thenReturn(Optional.of(artist));
        when(artistConverter.toOutput(artist)).thenReturn(artistOutput);

        var output = artistService.getByIdToOutput("1");

        assertThat(output).isNotNull();
        checkArtist(output, artistOutput);

        verify(artistRepository).findById("1");
        verify(artistConverter).toOutput(artist);
    }

    @Test
    void whenInvalidGetByIdToOutputThenThrowException() {
        when(artistRepository.findById("4")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            artistService.getByIdToOutput("4");
        });

        verify(artistRepository).findById("4");
    }

    @Test
    void whenGetAllToOutputThenReturnAllArtistOutputs() {
        when(artistRepository.findAll()).thenReturn(artists);
        when(artistConverter.toOutput(artists)).thenReturn(artistOutputs);

        var allArtist = artistService.getAllToOutput();

        assertThat(allArtist).hasSize(artists.size());
        assertThat(allArtist).containsAll(artistOutputs);

        verify(artistRepository).findAll();
        verify(artistConverter).toOutput(artists);
    }

    @Test
    void whenUpdateFromInputToOutputThenArtistIsUpdated() {
    	var artist = artists.get(0);
    	var updatedArtist = artists.get(2);
    	var artistOutput = artistOutputs.get(2);
    	var artistUpdateInput =
    		ArtistUpdateInput.builder()
    			.names(List.of("Michael Jackson", "Michael Joseph Jackson", "MJ", "King of Pop", "The Gloved One"))
    			.bio("Michael Jackson foi um artista multifacetado, conhecido por redefinir o cenário da música pop com seus movimentos de dança icônicos, videoclipes revolucionários e uma voz única.")
    			.genreIds(List.of("g87179e9c79647a557f17b95", "h969e7a595f1d87174579c"))
    			.imageUrl("https://example.com/images/michael_joseph_jackson.jpg")
    		.build();

    	when(artistConverter.toEntity(artist, artistUpdateInput)).thenReturn(updatedArtist);
        when(artistRepository.findById("1")).thenReturn(Optional.of(artist));
        when(artistRepository.save(updatedArtist)).thenReturn(updatedArtist);
        when(artistConverter.toOutput(updatedArtist)).thenReturn(artistOutput);

        var output = artistService.updateFromInputToOutput("1", artistUpdateInput);

        assertThat(output).isNotNull();
        checkArtist(output, artistOutput);

        verify(artistConverter).toEntity(artist, artistUpdateInput);
        verify(artistRepository).findById("1");
        verify(artistRepository).save(updatedArtist);
        verify(artistConverter).toOutput(updatedArtist);
    }

    @Test
    void whenDeleteArtistThenArtistIsDeleted() {
        artistService.delete("1");
        verify(artistRepository).deleteById("1");
    }

}
