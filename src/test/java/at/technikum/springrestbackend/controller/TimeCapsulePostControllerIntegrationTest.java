package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.entity.TimeCapsulePost;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.TimeCapsulePostRepository;
import at.technikum.springrestbackend.repository.UserRepository;
import at.technikum.springrestbackend.service.FileStorageService;
import at.technikum.springrestbackend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TimeCapsulePostControllerIntegrationTest {

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

    private final List<Path> createdFiles = new ArrayList<>();

    @BeforeEach
    void cleanDb() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        cleanupFiles();
    }

    @AfterEach
    void cleanupAfterTest() {
        cleanupFiles();
    }

    private void cleanupFiles() {
        for (Path file : createdFiles) {
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                // ignore cleanup errors
            }
        }
        createdFiles.clear();
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
    void publicCanReadPostsWithoutToken() throws Exception {
        User user = persistUser("public@example.com", "public", "USER");
        TimeCapsulePost post = new TimeCapsulePost();
        post.setUser(user);
        post.setTitle("Hallo Welt, was geht?");
        post.setMessage("Public message");
        post.setSendAt(LocalDateTime.now().plusDays(1));
        postRepository.save(post);

        mvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Hallo Welt, was geht?"));
    }

    @Test
    void userCanCreateAndUpdateOwnPost() throws Exception {
        User owner = persistUser("owner@example.com", "owner", "USER");

        String createJson = "{" +
                "\"title\":\"First\"," +
                "\"message\":\"Message\"," +
                "\"sendAt\":\"2030-01-01T00:00:00\"}";

        MvcResult result = mvc.perform(post("/api/posts")
                        .header("Authorization", authHeader(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("First"))
                .andReturn();

        at.technikum.springrestbackend.dto.TimeCapsulePostResponse created =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        at.technikum.springrestbackend.dto.TimeCapsulePostResponse.class);

        String updateJson = "{" +
                "\"title\":\"Updated\"," +
                "\"message\":\"Updated message\"," +
                "\"sendAt\":\"2031-01-01T00:00:00\"}";

        mvc.perform(put("/api/posts/" + created.getId())
                        .header("Authorization", authHeader(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.message").value("Updated message"));
    }

    @Test
    void differentUserCannotModifyOthersPost() throws Exception {
        User owner = persistUser("owner2@example.com", "owner2", "USER");
        User other = persistUser("other@example.com", "other", "USER");

        TimeCapsulePost post = new TimeCapsulePost();
        post.setUser(owner);
        post.setTitle("Owner Post");
        post.setMessage("No access");
        post.setSendAt(LocalDateTime.now().plusDays(2));
        post = postRepository.save(post);

        String updateJson = "{" +
                "\"title\":\"Hacked\"," +
                "\"message\":\"Nope\"," +
                "\"sendAt\":\"2031-01-01T00:00:00\"}";

        mvc.perform(put("/api/posts/" + post.getId())
                        .header("Authorization", authHeader(other))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeleteAnyPost() throws Exception {
        User owner = persistUser("owner3@example.com", "owner3", "USER");
        User admin = persistUser("admin2@example.com", "admin2", "ADMIN");

        TimeCapsulePost post = new TimeCapsulePost();
        post.setUser(owner);
        post.setTitle("Delete Me");
        post.setMessage("Delete");
        post.setSendAt(LocalDateTime.now().plusDays(3));
        post = postRepository.save(post);

        mvc.perform(delete("/api/posts/" + post.getId())
                        .header("Authorization", authHeader(admin)))
                .andExpect(status().isNoContent());

        assertThat(postRepository.findById(post.getId())).isEmpty();
    }

    @Test
    void ownerCanUploadFile() throws Exception {
        User owner = persistUser("uploader@example.com", "uploader", "USER");
        TimeCapsulePost post = new TimeCapsulePost();
        post.setUser(owner);
        post.setTitle("Upload");
        post.setMessage("Upload file");
        post.setSendAt(LocalDateTime.now().plusDays(4));
        post = postRepository.save(post);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                "bytes".getBytes()
        );

        MvcResult result = mvc.perform(multipart("/api/posts/" + post.getId() + "/upload")
                        .file(file)
                        .header("Authorization", authHeader(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileUrl").isNotEmpty())
                .andExpect(jsonPath("$.fileType").value("image/png"))
                .andReturn();

        // Extract fileUrl from response and track for cleanup
        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                at.technikum.springrestbackend.dto.TimeCapsulePostResponse.class
        );
        if (response.getFileUrl() != null && response.getFileUrl().contains("/uploads/")) {
            String filename = response.getFileUrl().substring(response.getFileUrl().lastIndexOf('/') + 1);
            Path file_to_delete = Path.of(FileStorageService.UPLOAD_DIR).resolve(filename);
            createdFiles.add(file_to_delete);
        }
    }
}
