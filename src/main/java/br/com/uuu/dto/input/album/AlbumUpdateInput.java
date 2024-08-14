package br.com.uuu.dto.input.album;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AlbumUpdateInput {

	@Schema(description = "ID do artista", example = "64957a557f1d87179e9c77b9")
	private String artistId;

	@Schema(description = "Nome do álbum", example = "Temple of Love")
	private String name;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@Schema(description = "Data de lançamento do álbum", example = "30/06/2006")
	private LocalDate releaseDate;

	@Schema(description = "Nome da gravadora", example = "Sony Music")
	private String recordCompanyName;

	@Schema(description = "Lista de IDs de músicas para cada disco mapeado que foi atualizado", example = "{\"Disco 1\" : [\"64957c7eb0169b0599f68632\", \"64957c7eb0169b0599f68698\"]}")
	private Map<String, List<String>> updatedMusicsIdsByDiskNames;

	@NotBlank(message = "Informe o ID do usuário que está enviando uma revisão para o álbum")
	@Schema(description = "ID do usuário que está enviando uma revisão para o álbum", example = "64957a557f1d87179e9c77f9")
	private String proofreaderId;
}
