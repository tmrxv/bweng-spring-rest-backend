package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.TimeCapsulePostResponse;
import at.technikum.springrestbackend.dto.UpdateUserRequest;
import at.technikum.springrestbackend.dto.UserResponse;
import at.technikum.springrestbackend.service.TimeCapsulePostService;
import at.technikum.springrestbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponse> adminUpdateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest req
    ) {
        return ResponseEntity.ok(userService.updateUser(id, req));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> adminDeleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<TimeCapsulePostResponse>> listPosts(Pageable pageable) {
        return ResponseEntity.ok(
                postService.findAll(java.util.Optional.empty(), java.util.Optional.empty(), pageable)
        );
    }
}
