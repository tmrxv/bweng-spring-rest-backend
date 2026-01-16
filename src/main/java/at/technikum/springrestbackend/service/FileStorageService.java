package at.technikum.springrestbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {

    public static final String UPLOAD_DIR = "uploads";

    // Allowed MIME types
    private static final String[] ALLOWED = new String[]{"image/png", "image/jpeg", "application/pdf"};

    private final Optional<MinioFileStorageService> minioService;
    private final boolean minioEnabled;

    public FileStorageService(@Autowired(required = false) MinioFileStorageService minioService, 
        @org.springframework.beans.factory.annotation.Value("${minio.enabled:false}") boolean minioEnabled) {
        this.minioService = Optional.ofNullable(minioService);
        this.minioEnabled = minioEnabled;
    }

    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        boolean ok = false;
        for (String a : ALLOWED) {
            if (a.equalsIgnoreCase(contentType)) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
        }

        if (minioEnabled && minioService.isPresent()) {
            try {
                return minioService.get().storeFile(file);
            } catch (Exception e) {
                throw new IOException("MinIO upload failed: " + e.getMessage(), e);
            }
        }

        Path uploadDir = Path.of(UPLOAD_DIR);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + (ext.isBlank() ? "" : "." + ext);
        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Return a relative path that will be served as /uploads/{filename}
        return "/" + UPLOAD_DIR + "/" + filename;
    }

    private String getExtension(String name) {
        if (name == null) return "";
        int idx = name.lastIndexOf('.');
        if (idx == -1) return "";
        return name.substring(idx + 1);
    }
}