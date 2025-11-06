package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.RegisterRequest;
import at.technikum.springrestbackend.dto.UserResponse;
import at.technikum.springrestbackend.model.User;
import at.technikum.springrestbackend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already in use");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setUsername(req.getUsername());
        // NOTE: real hashing/JWT will come in a later milestone per spec
        user.setPassword(req.getPassword());
        user.setCountry(req.getCountry());
        user.setProfileImageUrl(req.getProfileImageUrl());
        user.setRole("USER");

        User saved = userRepository.save(user);

        UserResponse resp = new UserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getUsername(),
                saved.getCountry(),
                saved.getProfileImageUrl(),
                saved.getRole(),
                saved.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
