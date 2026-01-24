package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.UpdateUserRequest;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import at.technikum.springrestbackend.repository.TimeCapsulePostRepository;
import at.technikum.springrestbackend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimeCapsulePostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        // Delete posts first to avoid FK violations, then users
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User persistUser(String email, String username, String role) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("ValidPass1!"));
        user.setCountry("AT");
        user.setRole(role);
        return userRepository.save(user);
    }

    private String authHeader(User user) {
        return "Bearer " + jwtService.generateToken(user.getEmail());
    }

    @Test
    void adminCanListUsers() throws Exception {
        User admin = persistUser("admin@example.com", "admin1", "ADMIN");
        persistUser("member@example.com", "member1", "USER");

        mvc.perform(get("/api/users")
                        .header("Authorization", authHeader(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void userCannotListUsers() throws Exception {
        User user = persistUser("user@example.com", "user01", "USER");

        mvc.perform(get("/api/users")
                        .header("Authorization", authHeader(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void userCanReadOwnProfileButNotOthers() throws Exception {
        User alice = persistUser("yes@example.com", "yesYes", "USER");
        User bob = persistUser("no@example.com", "noNono", "USER");

        mvc.perform(get("/api/users/" + alice.getId())
                        .header("Authorization", authHeader(alice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("yes@example.com"));

        mvc.perform(get("/api/users/" + alice.getId())
                        .header("Authorization", authHeader(bob)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanUpdateUserRoleAndEmail() throws Exception {
        User admin = persistUser("okay@example.com", "okay1", "ADMIN");
        User target = persistUser("okaybro@example.com", "okaybro1", "USER");

        UpdateUserRequest req = new UpdateUserRequest();
        req.setEmail("updated@example.com");
        req.setRole("ADMIN");

        mvc.perform(put("/api/users/" + target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authHeader(admin))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}
