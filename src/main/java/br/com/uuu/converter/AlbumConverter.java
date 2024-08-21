//package br.com.uuu.converter;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import br.com.uuu.dto.error.exception.NotFoundException;
//import br.com.uuu.dto.input.album.AlbumCreateInput;
//import br.com.uuu.dto.input.album.AlbumUpdateInput;
//import br.com.uuu.mongodb.entity.Album;
//import br.com.uuu.service.ArtistService;
//
//@Component
//public class AlbumConverter {
//
//	@Autowired
//	private ArtistService artistService;
//
//	public Album toEntity(AlbumCreateInput input) {
//		var album = new Album();
//
//		if (artistService.existsById(input.getArtistId())) {
//			album.setArtistId(input.getArtistId());
//		} else {
//			throw new NotFoundException(input.getArtistId(), "Artista");
//		}
//
//		album.setName(input.getName());
//		album.setReleaseDate(input.getReleaseDate());
//		album.setRecordCompanyName(input.getRecordCompanyName());
//		album.setMusicsIdsByDiskNames(input.getMusicsIdsByDiskNames());
//		album.setSubmitterId(input.getSubmitterId());
//
//		return album;
//	}
//	
//	public Album toUpdatedEntity(Album album, AlbumUpdateInput input) {
//
//		if (input.getArtistId() != null) {
//			artistService.removeAlbumId(album.getArtistId(), album.getId());
//			artistService.addAlbumId(input.getArtistId(), album.getId());
//
//			album.setArtistId(input.getArtistId());
//		}
//		if (input.getName() != null) {
//			album.setName(input.getName());
//		}
//		if (input.getReleaseDate() != null) {
//			album.setReleaseDate(input.getReleaseDate());
//		}
//		if (input.getRecordCompanyName() != null) {
//			album.setRecordCompanyName(input.getRecordCompanyName());
//		}
//		if (input.getUpdatedMusicsIdsByDiskNames() != null && !input.getUpdatedMusicsIdsByDiskNames().isEmpty()) {
//			input.getUpdatedMusicsIdsByDiskNames().forEach((diskName, musicIds) -> {
//				album.getMusicsIdsByDiskNames().put(diskName, musicIds);				
//			});
//		}
//		if (input.getProofreaderId() != null) {
//			album.getProofreadersIds().add(input.getProofreaderId());
//		}
//
//		return album;
//	}
//}
