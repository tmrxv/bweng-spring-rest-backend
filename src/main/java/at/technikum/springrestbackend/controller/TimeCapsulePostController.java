package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.TimeCapsulePostRequest;
import at.technikum.springrestbackend.dto.TimeCapsulePostResponse;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.service.TimeCapsulePostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class TimeCapsulePostController {

    private final TimeCapsulePostService service;
    private final at.technikum.springrestbackend.service.FileStorageService fileStorageService;

    public TimeCapsulePostController(TimeCapsulePostService service, at.technikum.springrestbackend.service.FileStorageService fileStorageService) {
        this.service = service;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<TimeCapsulePostResponse> createPost(
            @Valid @org.springframework.lang.NonNull @RequestBody TimeCapsulePostRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<Page<TimeCapsulePostResponse>> getAllPosts(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String title,
            Pageable pageable) {
        Page<TimeCapsulePostResponse> page = service.findAll(Optional.ofNullable(userId), Optional.ofNullable(title), pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeCapsulePostResponse> getPostById(@PathVariable long id) {
        return service.findById(id)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeCapsulePostResponse> updatePost(
            @PathVariable long id,
            @Valid @RequestBody TimeCapsulePostRequest req,
            @AuthenticationPrincipal User currentUser) {

        var existing = service.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!currentUser.getRole().equals("ADMIN") && !existing.get().getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var updated = service.update(id, req);
        return ResponseEntity.ok(updated);
    }

    @PostMapping(path = "/{id}/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@PathVariable long id,
                                        @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                        @AuthenticationPrincipal User currentUser) {
        var existing = service.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!currentUser.getRole().equals("ADMIN") && !existing.get().getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String fileUrl = this.fileStorageService.storeFile(file);
            var updated = service.attachFile(id, fileUrl, file.getContentType());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException | java.io.IOException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable(required = true) long id,
                                           @AuthenticationPrincipal User currentUser) {
        var existing = service.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!currentUser.getRole().equals("ADMIN") && !existing.get().getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
