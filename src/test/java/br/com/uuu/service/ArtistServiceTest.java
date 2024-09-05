package br.com.uuu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        artists = List.of(
        		buildArtist("1", List.of("Mamiko Noto", "Noto Mamiko"), "????", List.of("J-Pop", "J-Rock"), "????"),
                buildArtist("2", List.of("Skillet"), "????", List.of("Rock"), "????"),
                buildArtist("3", List.of("Michael Jackson", "Michael Joseph Jackson"), "????", List.of("Pop"), "????")
        	);
        artistCreateInputs = artists.stream().map(artist -> buildArtistCreateInput(artist)).collect(Collectors.toList());
        artistOutputs = artists.stream().map(artist -> buildArtistOutput(artist)).collect(Collectors.toList());
    }

    private Artist buildArtist(String id, List<String> names, String bio, List<String> genreIds, String imageUrl) {
		var artist = new Artist();
		artist.setId(id);
		artist.setNames(names);
		artist.setBio(bio);
		artist.setGenreIds(genreIds);
		artist.setImageUrl(imageUrl);
        return artist;
	}

    private ArtistCreateInput buildArtistCreateInput(Artist artist) {
        return ArtistCreateInput.builder()
 				.names(artist.getNames())
 				.bio(artist.getBio())
 				.genreIds(artist.getGenreIds())
 				.imageUrl(artist.getImageUrl())
 				.build();
	}

    private ArtistOutput buildArtistOutput(Artist artist) {
        return ArtistOutput.builder()
    			.id(artist.getId())
    			.names(artist.getNames())
 				.bio(artist.getBio())
 				.genreIds(artist.getGenreIds())
 				.imageUrl(artist.getImageUrl())
 				.build();
	}

    private void checkArtist(ArtistOutput output, ArtistOutput artist) {
		assertThat(output.getId()).isEqualTo(artist.getId());
		assertThat(output.getNames()).isEqualTo(artist.getNames());
		assertThat(output.getBio()).isEqualTo(artist.getBio());
		assertThat(output.getGenreIds()).isEqualTo(artist.getGenreIds());
		assertThat(output.getImageUrl()).isEqualTo(artist.getImageUrl());
	}

    @Test
    void whenSaveFromInputToOutput_thenArtistIsSaved() {
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
    void whenGetByIdToOutput_thenReturnArtistOutput() {
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
    void whenInvalidGetByIdToOutput_thenThrowException() {
        when(artistRepository.findById("4")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            artistService.getByIdToOutput("4");
        });

        verify(artistRepository).findById("4");
    }

    @Test
    void whenGetAllToOutput_thenReturnAllArtistOutputs() {
        when(artistRepository.findAll()).thenReturn(artists);
        when(artistConverter.toOutput(artists)).thenReturn(artistOutputs);

        var allArtist = artistService.getAllToOutput();

        assertThat(allArtist).hasSize(artists.size());
        assertThat(allArtist).containsAll(artistOutputs);

        verify(artistRepository).findAll();
        verify(artistConverter).toOutput(artists);
    }

    @Test
    void whenUpdateFromInputToOutput_thenArtistIsUpdated() {
    	var artist = artists.get(0);
    	var updatedArtist = artists.get(2);
    	var artistOutput = artistOutputs.get(2);
    	var artistUpdateInput =
    			ArtistUpdateInput.builder()
    			.names(Optional.of(List.of("?")))
    			.bio(Optional.of("?"))
    			.genreIds(Optional.of(List.of("?")))
    			.imageUrl(Optional.of("?"))
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
    void whenDeleteArtist_thenArtistIsDeleted() {
        artistService.delete("1");
        verify(artistRepository).deleteById("1");
    }

}
