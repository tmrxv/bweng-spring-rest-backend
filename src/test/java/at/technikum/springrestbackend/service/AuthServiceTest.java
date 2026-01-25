package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.LoginRequest;
import at.technikum.springrestbackend.dto.LoginResponse;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtService = Mockito.mock(JwtService.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void loginReturnsTokenWhenCredentialsMatch() {
        User user = new User();
        user.setId(1L);
        user.setEmail("login@example.com");
        user.setUsername("loginuser");
        user.setPassword("encoded");
        user.setRole("USER");

        LoginRequest req = new LoginRequest();
        req.setEmail("login@example.com");
        req.setPassword("raw");

        when(userRepository.findByEmail("login@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);
        when(jwtService.generateToken("login@example.com")).thenReturn("token-123");

        LoginResponse resp = authService.login(req);
        assertEquals("token-123", resp.getToken());
        assertEquals("login@example.com", resp.getEmail());
        assertEquals("USER", resp.getRole());
    }

    @Test
    void loginThrowsWhenPasswordDoesNotMatch() {
        User user = new User();
        user.setEmail("login@example.com");
        user.setPassword("encoded");

        LoginRequest req = new LoginRequest();
        req.setEmail("login@example.com");
        req.setPassword("bad");

        when(userRepository.findByEmail("login@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    }

    @Test
    void loginThrowsWhenUserLocked() {
        User user = new User();
        user.setEmail("locked@example.com");
        user.setPassword("encoded");
        user.setLocked(true);

        LoginRequest req = new LoginRequest();
        req.setEmail("locked@example.com");
        req.setPassword("raw");

        when(userRepository.findByEmail("locked@example.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> authService.login(req));
    }
}
