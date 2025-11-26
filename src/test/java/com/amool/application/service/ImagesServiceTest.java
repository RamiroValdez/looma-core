package com.amool.application.service;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.port.out.HttpDownloadPort;
import com.amool.domain.model.InMemoryMultipartFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImagesServiceTest {

    @Mock
    FilesStoragePort filesStoragePort;

    @Mock
    HttpDownloadPort httpDownloadPort;

    ImagesService imagesService;

    @BeforeEach
    void setUp() throws Exception {
        imagesService = new ImagesService(filesStoragePort, httpDownloadPort);
    }

    @Nested
    @DisplayName("uploadBannerImage")
    class UploadBannerImage {
        @Test
        @DisplayName("debería subir el banner y devolver la ruta generada")
        void deberia_subir_banner_y_devolver_ruta_cuando_ok() throws Exception {
            when(filesStoragePort.uploadPublicFile(anyString(), any(MultipartFile.class))).thenReturn(true);

            MultipartFile file = imagenPng(new byte[]{1, 2, 3}, "banner.png");
            String workId = "work-123";

            String ruta = imagesService.uploadBannerImage(file, workId);

            verificarRutaGeneradaYSubida(ruta, "works/" + workId + "/banner/", "png");
            verificarSeSubioElMismoArchivo(file);
        }

        @Test
        @DisplayName("debería lanzar IOException cuando el storage devuelve false")
        void deberia_lanzar_excepcion_al_subir_banner_cuando_storage_falla() throws Exception {
            when(filesStoragePort.uploadPublicFile(anyString(), any(MultipartFile.class))).thenReturn(false);

            MultipartFile file = imagenPng(new byte[]{1, 2, 3}, "banner.png");
            String workId = "work-err";

            IOException ex = assertThrows(IOException.class, () -> imagesService.uploadBannerImage(file, workId));
            assertTrue(ex.getMessage().toLowerCase(Locale.ROOT).contains("error uploading banner image"));

            verify(filesStoragePort, times(1)).uploadPublicFile(anyString(), any(MultipartFile.class));
        }

        @Test
        @DisplayName("debería lanzar IllegalArgumentException cuando el archivo es nulo")
        void deberia_lanzar_iae_cuando_banner_es_nulo() throws Exception {
            String workId = "work-123";
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> imagesService.uploadBannerImage(null, workId));
            assertEquals("El archivo no puede estar vacío", ex.getMessage());
            verify(filesStoragePort, never()).uploadPublicFile(anyString(), any(MultipartFile.class));
        }

        @Test
        @DisplayName("debería lanzar IllegalArgumentException cuando el archivo está vacío")
        void deberia_lanzar_iae_cuando_banner_esta_vacio() throws Exception {
            String workId = "work-123";
            MultipartFile file = imagenPng(new byte[0], "banner.png");
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> imagesService.uploadBannerImage(file, workId));
            assertEquals("El archivo no puede estar vacío", ex.getMessage());
            verify(filesStoragePort, never()).uploadPublicFile(anyString(), any(MultipartFile.class));
        }
    }

    @Nested
    @DisplayName("uploadCoverImage")
    class UploadCoverImage {
        @Test
        @DisplayName("debería subir la portada y devolver la ruta generada")
        void deberia_subir_cover_y_devolver_ruta() throws Exception {
            MultipartFile file = imagenPng(new byte[]{4, 5, 6}, "cover.png");
            String workId = "w-1";

            String ruta = imagesService.uploadCoverImage(file, workId);

            verificarRutaGeneradaYSubida(ruta, "works/" + workId + "/cover/", "png");
            verificarSeSubioElMismoArchivo(file);
        }

        @Test
        @DisplayName("debería lanzar IllegalArgumentException cuando el archivo es nulo")
        void deberia_lanzar_iae_cuando_cover_es_nulo() throws Exception {
            String workId = "w-1";
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> imagesService.uploadCoverImage(null, workId));
            assertEquals("El archivo no puede estar vacío", ex.getMessage());
            verify(filesStoragePort, never()).uploadPublicFile(anyString(), any(MultipartFile.class));
        }

        @Test
        @DisplayName("debería lanzar IllegalArgumentException cuando el archivo está vacío")
        void deberia_lanzar_iae_cuando_cover_esta_vacio() throws Exception {
            String workId = "w-1";
            MultipartFile file = imagenPng(new byte[0], "cover.png");
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> imagesService.uploadCoverImage(file, workId));
            assertEquals("El archivo no puede estar vacío", ex.getMessage());
            verify(filesStoragePort, never()).uploadPublicFile(anyString(), any(MultipartFile.class));
        }
    }

    @Nested
    @DisplayName("downloadAndUploadCoverImage")
    class DownloadAndUploadCoverImage {
        @Test
        @DisplayName("debería descargar desde URL y subir la portada con extensión .png")
        void deberia_descargar_y_subir_cover_desde_url() throws Exception {
            String url = "https://example.com/x.png";
            String workId = "w-2";
            byte[] bytes = new byte[]{9, 8, 7};
            when(httpDownloadPort.downloadImage(url)).thenReturn(bytes);

            String ruta = imagesService.downloadAndUploadCoverImage(url, workId);

            verify(httpDownloadPort, times(1)).downloadImage(url);

            verificarRutaGeneradaYSubida(ruta, "works/" + workId + "/cover/", "png");

            verificarArchivoSubido("cover.png", "image/png", bytes);
        }
    }

    @Nested
    @DisplayName("uploadUserImage")
    class UploadUserImage {
        @Test
        @DisplayName("debería subir la imagen de usuario y devolver la ruta generada")
        void deberia_subir_imagen_usuario_y_devolver_ruta() throws Exception {
            InMemoryMultipartFile file = imagenPng(new byte[]{1, 3, 5}, "avatar.jpeg");
            String userId = "user-7";

            String ruta = imagesService.uploadUserImage(file, userId);

            verificarRutaGeneradaYSubida(ruta, "users/" + userId + "/profile/", "jpeg");
            verificarSeSubioElMismoArchivo(file);
        }

        @Test
        @DisplayName("debería lanzar IllegalArgumentException cuando el archivo es nulo")
        void deberia_lanzar_iae_cuando_imagen_usuario_es_nula() throws Exception {
            String userId = "user-7";
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> imagesService.uploadUserImage(null, userId));
            assertEquals("El archivo no puede estar vacío", ex.getMessage());
            verify(filesStoragePort, never()).uploadPublicFile(anyString(), any(MultipartFile.class));
        }

        @Test
        @DisplayName("debería lanzar IllegalArgumentException cuando el archivo está vacío")
        void deberia_lanzar_iae_cuando_imagen_usuario_esta_vacia() throws Exception {
            String userId = "user-7";
            InMemoryMultipartFile file = imagenPng(new byte[0], "avatar.jpeg");
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> imagesService.uploadUserImage(file, userId));
            assertEquals("El archivo no puede estar vacío", ex.getMessage());
            verify(filesStoragePort, never()).uploadPublicFile(anyString(), any(MultipartFile.class));
        }
    }

    @Nested
    @DisplayName("deleteImage")
    class DeleteImage {
        @Test
        @DisplayName("debería no eliminar cuando la ruta es null")
        void deberia_no_eliminar_cuando_ruta_es_null() {
            imagesService.deleteImage(null);
            verify(filesStoragePort, never()).deleteObject(anyString());
        }

        @Test
        @DisplayName("debería no eliminar cuando la ruta es 'none' sin importar mayúsculas")
        void deberia_no_eliminar_cuando_ruta_es_none_case_insensitive() {
            imagesService.deleteImage("NoNe");
            verify(filesStoragePort, never()).deleteObject(anyString());
        }

        @Test
        @DisplayName("debería eliminar cuando la ruta es válida")
        void deberia_eliminar_cuando_ruta_valida() {
            String path = "works/w-9/cover/abc.png";
            imagesService.deleteImage(path);
            verify(filesStoragePort, times(1)).deleteObject(path);
        }
    }


    private InMemoryMultipartFile imagenPng(byte[] contenido, String nombreOriginal) {
        return new InMemoryMultipartFile("file", nombreOriginal, "image/png", contenido);
    }

    private void verificarRutaGeneradaYSubida(String rutaDevuelta, String prefijoEsperado, String extensionEsperada) throws Exception {
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(filesStoragePort, atLeastOnce()).uploadPublicFile(pathCaptor.capture(), any(MultipartFile.class));
        String pathUsado = pathCaptor.getValue();

        assertEquals(pathUsado, rutaDevuelta, "La ruta devuelta debe ser la misma usada en la subida");
        assertTrue(pathUsado.startsWith(prefijoEsperado), () -> "El path debe empezar con '" + prefijoEsperado + "' pero fue: " + pathUsado);
        assertTrue(pathUsado.endsWith("." + extensionEsperada), () -> "El path debe terminar con ." + extensionEsperada + " pero fue: " + pathUsado);

        String nombre = pathUsado.substring(prefijoEsperado.length(), pathUsado.length() - (extensionEsperada.length() + 1));
        assertFalse(nombre.isBlank(), "El nombre generado no debe estar vacío");
    }

    private void verificarSeSubioElMismoArchivo(MultipartFile esperado) throws Exception {
        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(filesStoragePort, atLeastOnce()).uploadPublicFile(anyString(), fileCaptor.capture());
        MultipartFile usado = fileCaptor.getValue();
        assertArrayEquals(esperado.getBytes(), usado.getBytes(), "El archivo subido debe coincidir con el provisto");
        assertEquals(esperado.getOriginalFilename(), usado.getOriginalFilename());
        assertEquals(esperado.getContentType(), usado.getContentType());
        assertEquals(esperado.isEmpty(), usado.isEmpty());
    }

    private void verificarArchivoSubido(String nombreOriginal, String contentType, byte[] contenido) throws Exception {
        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(filesStoragePort, atLeastOnce()).uploadPublicFile(anyString(), fileCaptor.capture());
        MultipartFile usado = fileCaptor.getValue();
        assertEquals(nombreOriginal, usado.getOriginalFilename());
        assertEquals(contentType, usado.getContentType());
        assertArrayEquals(contenido, usado.getBytes());
    }
}
