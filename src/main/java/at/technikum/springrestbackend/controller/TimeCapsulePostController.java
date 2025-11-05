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
    public TimeCapsulePost create(@NonNull @RequestBody TimeCapsulePost post) {
        return service.save(post);
    }

    @GetMapping
    public List<TimeCapsulePost> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeCapsulePost> getOne(@NonNull @PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@NonNull @PathVariable Long id) {
        service.delete(id);
    }
}
