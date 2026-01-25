package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.UpdateUserRequest;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceEdgeCaseTest {

    private UserRepository repo;
    private FileStorageService fileStorageService;
    private at.technikum.springrestbackend.repository.TimeCapsulePostRepository postRepository;
    private UserService service;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(UserRepository.class);
        fileStorageService = Mockito.mock(FileStorageService.class);
        postRepository = Mockito.mock(at.technikum.springrestbackend.repository.TimeCapsulePostRepository.class);
        service = new UserService(repo, new BCryptPasswordEncoder(), fileStorageService, postRepository);
    }

    @Test
    void updateUserThrowsOnDuplicateEmail() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("old@example.com");
        existing.setUsername("olduser");

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.existsByEmail("taken@example.com")).thenReturn(true);

        UpdateUserRequest req = new UpdateUserRequest();
        req.setEmail("taken@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> service.updateUser(1L, req));
    }

    @Test
    void updateUserThrowsOnDuplicateUsername() {
        User existing = new User();
        existing.setId(2L);
        existing.setEmail("user@example.com");
        existing.setUsername("oldname");

        when(repo.findById(2L)).thenReturn(Optional.of(existing));
        when(repo.existsByUsername("takenname")).thenReturn(true);

        UpdateUserRequest req = new UpdateUserRequest();
        req.setUsername("takenname");

        assertThrows(DataIntegrityViolationException.class, () -> service.updateUser(2L, req));
    }

    @Test
    void updateUserThrowsOnWeakPassword() {
        User existing = new User();
        existing.setId(3L);
        existing.setEmail("user@example.com");
        existing.setUsername("username");

        when(repo.findById(3L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenReturn(existing);

        UpdateUserRequest req = new UpdateUserRequest();
        req.setPassword("weak");

        assertThrows(IllegalArgumentException.class, () -> service.updateUser(3L, req));
    }

    @Test
    void registerThrowsOnDuplicateUsername() {
        when(repo.existsByUsername("duplicate")).thenReturn(true);

        var req = new at.technikum.springrestbackend.dto.RegisterRequest();
        req.setEmail("new@example.com");
        req.setUsername("duplicate");
        req.setPassword("ValidPass1!");
        req.setCountry("AT");

        assertThrows(DataIntegrityViolationException.class, () -> service.register(req));
    }
}
