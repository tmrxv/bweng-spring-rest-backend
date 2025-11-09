package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.TimeCapsulePostRequest;
import at.technikum.springrestbackend.dto.TimeCapsulePostResponse;
import at.technikum.springrestbackend.service.TimeCapsulePostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class TimeCapsulePostController {

    private final TimeCapsulePostService service;

    public TimeCapsulePostController(TimeCapsulePostService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TimeCapsulePostResponse> createPost(
            @Valid @org.springframework.lang.NonNull @RequestBody TimeCapsulePostRequest request) {
        return ResponseEntity.ok(service.save(request));
    }

    @GetMapping
    public ResponseEntity<List<TimeCapsulePostResponse>> getAllPosts() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeCapsulePostResponse> getPostById(@PathVariable long id) {
        return service.findById(id)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable(required = true) long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
