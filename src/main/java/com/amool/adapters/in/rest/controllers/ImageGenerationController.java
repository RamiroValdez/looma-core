package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.*;
import com.amool.adapters.in.rest.dtos.*;
import com.amool.application.usecases.GenerateImageUrlUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageGenerationController {

    private final GenerateImageUrlUseCase generateImageUrlUseCase;

    public ImageGenerationController(GenerateImageUrlUseCase generateImageUrlUseCase) {
        this.generateImageUrlUseCase = generateImageUrlUseCase;
    }

    @PostMapping("/generate")
    public ResponseEntity<ImageUrlResponseDto> generate(@RequestBody ImagePromptDto request) {
        String url = generateImageUrlUseCase.execute(
                request.artisticStyleId(),
                request.colorPaletteId(),
                request.compositionId(),
                request.description());

        return ResponseEntity.ok(new ImageUrlResponseDto(url));
    }

    @GetMapping("/color-palettes/obtain-all")
    public ResponseEntity<List<ColorPaletteDto>> getAllColorPalettes() {
        List<ColorPaletteDto> palettes = List.of(
                new ColorPaletteDto(1L, "Tonos Neblinosos y Pastel","Suave, ideal para romance o drama."),
                new ColorPaletteDto(2L, "Contraste Neón (Ciberpunk)","Oscuro con luces brillantes."),
                new ColorPaletteDto(3L, "Colores cálidos","Mundo antiguo, aventura, o fantasía."),
                new ColorPaletteDto(4L, "Colores fríos","Misterio, soledad, o ciencia ficción."),
                new ColorPaletteDto(5L, "Alto Contraste B/N","Blanco y negro dramático."),
                new ColorPaletteDto(6L, "Paleta Terrosa","Naturaleza, historia o supervivencia."),
                new ColorPaletteDto(7L, "Vibrante y Saturado","Ideal para acción o cómics."),
                new ColorPaletteDto(8L, "Claro y Luminoso","Foco en la luz y claridad.")
        );
        return ResponseEntity.ok(palettes);
    }

    @GetMapping("/compositions/obtain-all")
    public  ResponseEntity<List<CompositionDto>> getAllCompositions() {

        List<CompositionDto> compositions = List.of(
                new CompositionDto(1L, "Primer Plano y Foco", "La cámara se acerca al máximo al objeto principal."),
                new CompositionDto(2L, "Silueta en Contraluz", "El sujeto es una forma oscura contra una luz intensa."),
                new CompositionDto(3L, "Regla de Tercios", "El elemento principal está desplazado del centro, creando armonía."),
                new CompositionDto(4L, "Vista desde arriba", "El escenario se ve desde una posición muy alta (como un dron)."),
                new CompositionDto(5L, "Simetría Central", "El elemento principal está en el centro, con reflejo exacto en ambos lados."),
                new CompositionDto(6L, "Escena Panorámica", "La imagen captura un paisaje amplio y la escala de un gran entorno."),
                new CompositionDto(7L, "Patrón Repetitivo", "La imagen es un patrón que se repite; ideal para portadas con mucho texto."),
                new CompositionDto(8L, "Toma baja", "La cámara mira hacia el sujeto desde abajo, haciéndolo ver grande e imponente.")
        );
        return ResponseEntity.ok(compositions);
    }

    @GetMapping("/artistic-styles/obtain-all")
    public ResponseEntity<List<ArtisticStyleDto>> getAllArtisticStyles() {

        List<ArtisticStyleDto> artisticStyles = List.of(
                new ArtisticStyleDto(1L, "Fotorrealista", "Ideal para thrillers modernos o no ficción."),
                new ArtisticStyleDto(2L,"Óleo", "Da un look rico y texturizado."),
                new ArtisticStyleDto(3L, "Minimalista", "Para diseños limpios y abstractos."),
                new ArtisticStyleDto(4L, "Dibujo a Línea", "Sencillo y efectivo, como un diagrama."),
                new ArtisticStyleDto(5L, "Arte Conceptual", "Para fantasía épica o ciencia ficción."),
                new ArtisticStyleDto(6L, "Acuarela", "Para ficción juvenil o romance."),
                new ArtisticStyleDto(7L, "Estilo Cómic", "Colores vibrantes y contornos marcados."),
                new ArtisticStyleDto(8L, "Estilo Clásico/Sepia", "Simula fotografía antigua o grabados.")
        );
        return ResponseEntity.ok(artisticStyles);
    }
}
