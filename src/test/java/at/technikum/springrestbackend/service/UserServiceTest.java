package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.RegisterRequest;
import at.technikum.springrestbackend.dto.UpdateUserRequest;
import at.technikum.springrestbackend.dto.UserResponse;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserRepository repo;
    private FileStorageService fileStorageService;
    private at.technikum.springrestbackend.repository.TimeCapsulePostRepository postRepository;
    private UserService service;

    @BeforeEach
    public void setup() {
        repo = Mockito.mock(UserRepository.class);
        fileStorageService = Mockito.mock(FileStorageService.class);
        postRepository = Mockito.mock(at.technikum.springrestbackend.repository.TimeCapsulePostRepository.class);
        
        when(repo.existsByEmail("taken@example.com")).thenReturn(true);
        when(repo.existsByUsername("takenusername")).thenReturn(true);

        when(repo.save(any(User.class))).thenAnswer(i -> {
            User u = (User) i.getArgument(0);
            u.setId(1L);
            return u;
        });

        service = new UserService(repo, new BCryptPasswordEncoder(), fileStorageService, postRepository);
    }

    @Test
    public void registerRejectsWeakPassword() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@example.com");
        req.setUsername("newuser");
        req.setPassword("weakpass");
        req.setCountry("AT");

        assertThrows(IllegalArgumentException.class, () -> service.register(req));
    }

    @Test
    public void registerSetsPlaceholderProfileImageWhenEmpty() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new2@example.com");
        req.setUsername("newuser2");
        req.setPassword("ValidPass1!");
        req.setCountry("AT");
        req.setProfileImageUrl("");

        UserResponse resp = service.register(req);
        assertEquals("https://www.gravatar.com/avatar/?d=mp", resp.getProfileImageUrl());
    }

    @Test
    public void updateUserChangesEmailAndCountry() {
        User user = new User();
        user.setId(5L);
        user.setEmail("old@example.com");
        user.setUsername("oldname");
        user.setPassword("encoded");
        user.setCountry("AT");

        when(repo.findById(5L)).thenReturn(java.util.Optional.of(user));
        when(repo.existsByEmail("new@example.com")).thenReturn(false);
        when(repo.existsByUsername("oldname")).thenReturn(false);
        when(repo.save(user)).thenReturn(user);

        UpdateUserRequest req = new UpdateUserRequest();
        req.setEmail("new@example.com");
        req.setCountry("DE");

        UserResponse resp = service.updateUser(5L, req);
        assertEquals("new@example.com", resp.getEmail());
        assertEquals("DE", resp.getCountry());
    }

    @Test
    public void deleteUserThrowsWhenMissing() {
        when(repo.existsById(999L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> service.deleteUser(999L));
    }

    @Test
    public void uploadProfileImageThrowsWhenUserNotFound() throws Exception {
        when(repo.findById(999L)).thenReturn(java.util.Optional.empty());
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "content".getBytes());
        
        assertThrows(EntityNotFoundException.class, () -> service.uploadProfileImage(999L, file));
    }

    @Test
    public void uploadProfileImageThrowsForInvalidFileType() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        
        when(repo.findById(1L)).thenReturn(java.util.Optional.of(user));
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        
        // Mock FileStorageService to throw exception for invalid file type
        when(fileStorageService.storeFile(file)).thenThrow(new IllegalArgumentException("File type not allowed: text/plain"));
        
        assertThrows(IllegalArgumentException.class, () -> service.uploadProfileImage(1L, file));
    }
}