package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.TimeCapsulePostResponse;
import at.technikum.springrestbackend.service.TimeCapsulePostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final TimeCapsulePostService postService;

    public HomeController(TimeCapsulePostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<TimeCapsulePostResponse>> latest() {
        return ResponseEntity.ok(postService.findLatest(5));
    }
}