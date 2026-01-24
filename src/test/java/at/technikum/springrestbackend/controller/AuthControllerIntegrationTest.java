package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.LoginRequest;
import at.technikum.springrestbackend.dto.RegisterRequest;
import at.technikum.springrestbackend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    private String asJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void registerCreatesUserAndReturns201() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setUsername("testbro");
        req.setPassword("ValidPass1!");
        req.setCountry("AT");

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void registerWithExistingEmailReturnsConflict() throws Exception {
        // seed existing user
        var existing = new at.technikum.springrestbackend.entity.User();
        existing.setEmail("yes@example.com");
        existing.setUsername("testbrowas");
        existing.setPassword(passwordEncoder.encode("ValidPass1!"));
        existing.setCountry("AT");
        userRepository.save(existing);

        RegisterRequest req = new RegisterRequest();
        req.setEmail("yes@example.com");
        req.setUsername("testbrookay");
        req.setPassword("ValidPass1!");
        req.setCountry("AT");

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void loginReturnsTokenOnValidCredentials() throws Exception {
        var user = new at.technikum.springrestbackend.entity.User();
        user.setEmail("login@example.com");
        user.setUsername("loginuser");
        user.setPassword(passwordEncoder.encode("ValidPass1!"));
        user.setCountry("AT");
        userRepository.save(user);

        LoginRequest req = new LoginRequest();
        req.setEmail("login@example.com");
        req.setPassword("ValidPass1!");

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("login@example.com"));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        var user = new at.technikum.springrestbackend.entity.User();
        user.setEmail("nobro@example.com");
        user.setUsername("nobro");
        user.setPassword(passwordEncoder.encode("ValidPass1!"));
        user.setCountry("AT");
        userRepository.save(user);

        LoginRequest req = new LoginRequest();
        req.setEmail("nobro@example.com");
        req.setPassword("BadPass1!");

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(req)))
                .andExpect(status().isUnauthorized());
    }
}
