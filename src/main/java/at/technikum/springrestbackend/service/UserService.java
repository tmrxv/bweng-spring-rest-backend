package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.RegisterRequest;
import at.technikum.springrestbackend.dto.UpdateUserRequest;
import at.technikum.springrestbackend.dto.UserResponse;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final at.technikum.springrestbackend.repository.TimeCapsulePostRepository postRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       FileStorageService fileStorageService,
                       at.technikum.springrestbackend.repository.TimeCapsulePostRepository postRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
        this.postRepository = postRepository;
    }

    public Page<UserResponse> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User with id " + id + " not found")
                );
        return toResponse(user);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User with id " + id + " not found")
                );

        if (req.getEmail() != null && !req.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.getEmail())) {
                throw new DataIntegrityViolationException("Email already in use");
            }
            user.setEmail(req.getEmail());
        }

        if (req.getUsername() != null && !req.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(req.getUsername())) {
                throw new DataIntegrityViolationException("Username already in use");
            }
            user.setUsername(req.getUsername());
        }

        if (req.getPassword() != null) {
            String password = req.getPassword();
            if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
                throw new IllegalArgumentException(
                        "Password must contain at least one digit, one lowercase and one uppercase character and be at least 8 characters long"
                );
            }
            user.setPassword(passwordEncoder.encode(password));
        }

        if (req.getCountry() != null) {
            user.setCountry(req.getCountry());
        }
        if (req.getProfileImageUrl() != null) {
            user.setProfileImageUrl(req.getProfileImageUrl());
        }
        if (req.getRole() != null) {
            user.setRole(req.getRole());
        }
        if (req.getLocked() != null) {
            user.setLocked(req.getLocked());
        }

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        // Delete user's posts first to avoid FK constraint violation
        postRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getCountry(),
                user.getProfileImageUrl(),
                user.getRole(),
                user.isLocked(),
                user.getCreatedAt()
        );
    }

    public UserResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DataIntegrityViolationException("Email already in use");
        }
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new DataIntegrityViolationException("Username already in use");
        }

        // Validate password strength: at least one digit, one lowercase and one uppercase letter
        String password = req.getPassword();
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one digit, one lowercase and one uppercase character and be at least 8 characters long"
            );
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(password));
        user.setCountry(req.getCountry());

        // placeholder profile image if not provided
        if (req.getProfileImageUrl() == null || req.getProfileImageUrl().isBlank()) {
            user.setProfileImageUrl("https://www.gravatar.com/avatar/?d=mp");
        } else {
            user.setProfileImageUrl(req.getProfileImageUrl());
        }

        user.setRole("USER");
        user.setLocked(false); 

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        // Use FileStorageService which handles MinIO or local storage
        String fileUrl = fileStorageService.storeFile(file);
        
        // Update user with URL
        user.setProfileImageUrl(fileUrl);
        User saved = userRepository.save(user);
        return toResponse(saved);
    }
}
