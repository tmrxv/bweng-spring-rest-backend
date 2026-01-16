package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.TimeCapsulePostResponse;
import at.technikum.springrestbackend.dto.UserResponse;
import at.technikum.springrestbackend.service.TimeCapsulePostService;
import at.technikum.springrestbackend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final TimeCapsulePostService postService;

    public AdminController(UserService userService, TimeCapsulePostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> listUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.listUsers(pageable));
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<TimeCapsulePostResponse>> listPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.findAll(java.util.Optional.empty(), java.util.Optional.empty(), pageable));
    }
}