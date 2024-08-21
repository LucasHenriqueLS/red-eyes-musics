//package br.com.uuu.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import br.com.uuu.dto.input.album.AlbumCreateInput;
//import br.com.uuu.dto.input.album.AlbumUpdateInput;
//import br.com.uuu.service.AlbumService;
//import io.swagger.v3.oas.annotations.Operation;
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("albums")
//public class AlbumController {
//
//	@Autowired
//	private AlbumService albumService;
//
//	@GetMapping
//	@Operation(description = "Recupera todos os álbuns")
//	public ResponseEntity<?> getAll() {
//		return ResponseEntity.ok(albumService.getAll());
//	}
//
//	@GetMapping("get-by-id/{albumId}")
//	@Operation(description = "Recupera um álbum pelo ID")
//	public ResponseEntity<?> getById(@PathVariable String albumId) {
//		return ResponseEntity.ok(albumService.getById(albumId));
//	}
//
//	@GetMapping("get-by-name/{albumName}")
//	@Operation(description = "Recupera álbuns pelo nome")
//	public ResponseEntity<?> getByName(@PathVariable String albumName) {
//		return ResponseEntity.ok(albumService.getByName(albumName));
//	}
//
//	@GetMapping("get-by-artist-id/{artistId}")
//	@Operation(description = "Recupera álbuns pelo ID do artista")
//	public ResponseEntity<?> getByArtistId(@PathVariable String artistId) {
//		return ResponseEntity.ok(albumService.getByArtistId(artistId));
//	}
//
//	@GetMapping("get-by-music-id/{musicId}")
//	@Operation(description = "Recupera um álbum pelo ID da música")
//	public ResponseEntity<?> getByMusicId(@PathVariable String musicId) {
//		return ResponseEntity.ok(albumService.getByMusicId(musicId));
//	}
//
//	@PostMapping
//	@Operation(description = "Cria um novo álbum")
//	public ResponseEntity<?> save(@Valid @RequestBody AlbumCreateInput input) {
//		return ResponseEntity.ok(albumService.save(input));
//	}
//
//	@PutMapping("{albumId}")
//	@Operation(description = "Atualiza um álbum pelo ID")
//	public ResponseEntity<?> update(@Valid @PathVariable String albumId, @Valid @RequestBody AlbumUpdateInput input) {
//		return ResponseEntity.ok(albumService.update(albumId, input));
//	}
//
//	@DeleteMapping("{albumId}")
//	@Operation(description = "Deleta um álbum pelo ID")
//	public ResponseEntity<?> delete(@Valid @PathVariable String albumId) {
//		albumService.delete(albumId);
//		return ResponseEntity.ok().build();
//	}
//}
