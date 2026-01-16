package at.technikum.springrestbackend.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@ConditionalOnProperty(name = "minio.enabled", havingValue = "true")
public class MinioFileStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioFileStorageService(
            @Value("${minio.endpoint:http://localhost:9000}") String endpoint,
            @Value("${minio.access-key:}") String accessKey,
            @Value("${minio.secret-key:}") String secretKey,
            @Value("${minio.bucket-name:files}") String bucketName,
            @Value("${minio.secure:false}") boolean secure
    ) {
        this.bucketName = bucketName;

        // Build MinIO client
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        // Create bucket if it doesn't exist
        try {
            boolean exists = minioClient.bucketExists(
                    io.minio.BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        io.minio.MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MinIO bucket: " + e.getMessage(), e);
        }
    }

    public String storeFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String objectName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        
        // Upload file to MinIO
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        // Generate presigned URL for accessing the file
        String presignedUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(io.minio.http.Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );

        return presignedUrl;
    }
}