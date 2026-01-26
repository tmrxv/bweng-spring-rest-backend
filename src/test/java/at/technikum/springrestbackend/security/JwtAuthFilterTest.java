package at.technikum.springrestbackend.security;

import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import at.technikum.springrestbackend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    private JwtService jwtService;
    private UserRepository userRepository;
    private JwtAuthFilter filter;

    @BeforeEach
    void setup() {
        jwtService = mock(JwtService.class);
        userRepository = mock(UserRepository.class);
        filter = new JwtAuthFilter(jwtService, userRepository);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setsAuthenticationWhenTokenValid() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/posts");
        request.addHeader("Authorization", "Bearer token-abc");
        MockHttpServletResponse response = new MockHttpServletResponse();

        User user = new User();
        user.setEmail("user@example.com");
        user.setRole("USER");
        when(jwtService.extractSubject("token-abc")).thenReturn("user@example.com");
        when(jwtService.isTokenValid("token-abc", "user@example.com")).thenReturn(true);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        FilterChain chain = mock(FilterChain.class);
        filter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(chain, times(1)).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void skipsSwaggerAndAuthPaths() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/swagger-ui/index.html");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
        assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doesNotAuthenticateLockedUser() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/posts");
        request.addHeader("Authorization", "Bearer token-abc");
        MockHttpServletResponse response = new MockHttpServletResponse();

        User user = new User();
        user.setEmail("user@example.com");
        user.setRole("USER");
        user.setLocked(true);
        when(jwtService.extractSubject("token-abc")).thenReturn("user@example.com");
        when(jwtService.isTokenValid("token-abc", "user@example.com")).thenReturn(true);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        FilterChain chain = mock(FilterChain.class);
        filter.doFilterInternal(request, response, chain);

        // should skip setting authentication
        assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
        verify(chain, times(1)).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}
