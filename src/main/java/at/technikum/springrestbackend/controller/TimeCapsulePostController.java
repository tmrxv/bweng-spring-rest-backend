package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.model.TimeCapsulePost;
import at.technikum.springrestbackend.service.TimeCapsulePostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class TimeCapsulePostController {

    private final TimeCapsulePostService service;

    public TimeCapsulePostController(TimeCapsulePostService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TimeCapsulePost> createPost(@NonNull @RequestBody TimeCapsulePost post) {
        return ResponseEntity.ok(service.save(post));
    }

    @GetMapping
    public ResponseEntity<List<TimeCapsulePost>> getAllPosts() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeCapsulePost> getPostById(@NonNull @PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@NonNull @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}