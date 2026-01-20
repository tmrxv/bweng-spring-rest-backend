package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.RegisterRequest;
import at.technikum.springrestbackend.dto.UserResponse;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserRepository repo;
    private UserService service;

    @BeforeEach
    public void setup() {
        repo = Mockito.mock(UserRepository.class);
        when(repo.existsByEmail("taken@example.com")).thenReturn(true);
        when(repo.existsByUsername("takenusername")).thenReturn(true);

        when(repo.save(any(User.class))).thenAnswer(i -> {
            User u = (User) i.getArgument(0);
            u.setId(1L);
            return u;
        });

        service = new UserService(repo, new BCryptPasswordEncoder());
    }

    @Test
    public void registerRejectsWeakPassword() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@example.com");
        req.setUsername("newuser");
        req.setPassword("weakpass");
        req.setCountry("US");

        assertThrows(IllegalArgumentException.class, () -> service.register(req));
    }

    @Test
    public void registerSetsPlaceholderProfileImageWhenEmpty() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new2@example.com");
        req.setUsername("newuser2");
        req.setPassword("Str0ngPass");
        req.setCountry("US");
        req.setProfileImageUrl("");

        UserResponse resp = service.register(req);
        assertEquals("https://www.gravatar.com/avatar/?d=mp", resp.getProfileImageUrl());
    }
}