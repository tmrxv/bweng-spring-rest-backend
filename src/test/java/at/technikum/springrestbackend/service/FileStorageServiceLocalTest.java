package at.technikum.springrestbackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FileStorageServiceLocalTest {

    @Test
    void emptyFileThrows() {
        FileStorageService service = new FileStorageService(null, false);
        MockMultipartFile empty = new MockMultipartFile("file", "", "image/png", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> service.storeFile(empty));
    }

    @Test
    void nullFileThrows() {
        FileStorageService service = new FileStorageService(null, false);
        assertThrows(IllegalArgumentException.class, () -> service.storeFile(null));
    }

    @Test
    void invalidContentTypeThrows() {
        FileStorageService service = new FileStorageService(null, false);
        MockMultipartFile badType = new MockMultipartFile("file", "file.txt", "text/plain", "content".getBytes());
        assertThrows(IllegalArgumentException.class, () -> service.storeFile(badType));
    }

    @Test
    void invalidVideoTypeThrows() {
        FileStorageService service = new FileStorageService(null, false);
        MockMultipartFile video = new MockMultipartFile("file", "video.mp4", "video/mp4", "data".getBytes());
        assertThrows(IllegalArgumentException.class, () -> service.storeFile(video));
    }

    @Test
    void invalidHtmlTypeThrows() {
        FileStorageService service = new FileStorageService(null, false);
        MockMultipartFile html = new MockMultipartFile("file", "page.html", "text/html", "data".getBytes());
        assertThrows(IllegalArgumentException.class, () -> service.storeFile(html));
    }
}
