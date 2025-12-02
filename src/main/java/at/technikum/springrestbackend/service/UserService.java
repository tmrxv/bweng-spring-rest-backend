package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.UpdateUserRequest;
import at.technikum.springrestbackend.dto.UserResponse;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponse> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toResponse);
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

        // Email ändern + Eindeutigkeit prüfen
        if (req.getEmail() != null && !req.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.getEmail())) {
                throw new DataIntegrityViolationException("Email already in use");
            }
            user.setEmail(req.getEmail());
        }

        // Username ändern + Eindeutigkeit prüfen
        if (req.getUsername() != null && !req.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(req.getUsername())) {
                throw new DataIntegrityViolationException("Username already in use");
            }
            user.setUsername(req.getUsername());
        }

        if (req.getPassword() != null) {
            user.setPassword(req.getPassword());
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

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
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
                user.getCreatedAt()
        );
    }
}
