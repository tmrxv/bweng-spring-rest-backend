package at.technikum.springrestbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileStorageServiceTest {

    private FileStorageService service;

    @BeforeEach
    public void setup() {
        MinioFileStorageService minio = Mockito.mock(MinioFileStorageService.class);
        service = new FileStorageService(minio, false);
    }

    @Test
    public void rejectUnsupportedContentType() {
        MockMultipartFile bad = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
        assertThrows(IllegalArgumentException.class, () -> service.storeFile(bad));
    }
}