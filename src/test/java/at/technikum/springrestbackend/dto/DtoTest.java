package at.technikum.springrestbackend.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void loginRequestSettersAndGetters() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");
        req.setPassword("pass123");
        assertThat(req.getEmail()).isEqualTo("test@example.com");
        assertThat(req.getPassword()).isEqualTo("pass123");
    }

    @Test
    void loginResponseSettersAndGetters() {
        LoginResponse resp = new LoginResponse(1L, "test@example.com", "user", "USER", "token");
        assertThat(resp.getId()).isEqualTo(1L);
        assertThat(resp.getEmail()).isEqualTo("test@example.com");
        assertThat(resp.getUsername()).isEqualTo("user");
        assertThat(resp.getRole()).isEqualTo("USER");
        assertThat(resp.getToken()).isEqualTo("token");

        resp.setToken("newtoken");
        assertThat(resp.getToken()).isEqualTo("newtoken");
    }

    @Test
    void registerRequestSettersAndGetters() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("reg@example.com");
        req.setUsername("reguser");
        req.setPassword("Pass123");
        req.setCountry("US");
        req.setProfileImageUrl("http://img.com/pic.png");

        assertThat(req.getEmail()).isEqualTo("reg@example.com");
        assertThat(req.getUsername()).isEqualTo("reguser");
        assertThat(req.getPassword()).isEqualTo("Pass123");
        assertThat(req.getCountry()).isEqualTo("US");
        assertThat(req.getProfileImageUrl()).isEqualTo("http://img.com/pic.png");
    }

    @Test
    void updateUserRequestSettersAndGetters() {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setEmail("update@example.com");
        req.setUsername("updated");
        req.setPassword("NewPass1");
        req.setCountry("GB");
        req.setProfileImageUrl("http://img.com/new.png");
        req.setRole("ADMIN");

        assertThat(req.getEmail()).isEqualTo("update@example.com");
        assertThat(req.getUsername()).isEqualTo("updated");
        assertThat(req.getPassword()).isEqualTo("NewPass1");
        assertThat(req.getCountry()).isEqualTo("GB");
        assertThat(req.getProfileImageUrl()).isEqualTo("http://img.com/new.png");
        assertThat(req.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void userResponseConstructorAndGetters() {
        OffsetDateTime now = OffsetDateTime.now();
        UserResponse resp = new UserResponse(1L, "user@example.com", "username", "US", "http://pic.png", "USER", now);

        assertThat(resp.getId()).isEqualTo(1L);
        assertThat(resp.getEmail()).isEqualTo("user@example.com");
        assertThat(resp.getUsername()).isEqualTo("username");
        assertThat(resp.getCountry()).isEqualTo("US");
        assertThat(resp.getProfileImageUrl()).isEqualTo("http://pic.png");
        assertThat(resp.getRole()).isEqualTo("USER");
        assertThat(resp.getCreatedAt()).isEqualTo(now);

        resp.setRole("ADMIN");
        assertThat(resp.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void timeCapsulePostRequestSettersAndGetters() {
        LocalDateTime future = LocalDateTime.now().plusDays(10);
        TimeCapsulePostRequest req = new TimeCapsulePostRequest();
        req.setUserId(5L);
        req.setTitle("Title");
        req.setMessage("Message");
        req.setSendAt(future);
        req.setFileUrl("http://file.url");
        req.setFileType("image/png");

        assertThat(req.getUserId()).isEqualTo(5L);
        assertThat(req.getTitle()).isEqualTo("Title");
        assertThat(req.getMessage()).isEqualTo("Message");
        assertThat(req.getSendAt()).isEqualTo(future);
        assertThat(req.getFileUrl()).isEqualTo("http://file.url");
        assertThat(req.getFileType()).isEqualTo("image/png");
    }

    @Test
    void timeCapsulePostResponseSettersAndGetters() {
        TimeCapsulePostResponse resp = new TimeCapsulePostResponse();
        LocalDateTime now = LocalDateTime.now();

        resp.setId(10L);
        resp.setUserId(20L);
        resp.setTitle("Post Title");
        resp.setMessage("Post Message");
        resp.setSendAt(now);
        resp.setCreatedAt(now);
        resp.setUpdatedAt(now);
        resp.setFileUrl("http://file.url");
        resp.setFileType("application/pdf");

        assertThat(resp.getId()).isEqualTo(10L);
        assertThat(resp.getUserId()).isEqualTo(20L);
        assertThat(resp.getTitle()).isEqualTo("Post Title");
        assertThat(resp.getMessage()).isEqualTo("Post Message");
        assertThat(resp.getSendAt()).isEqualTo(now);
        assertThat(resp.getCreatedAt()).isEqualTo(now);
        assertThat(resp.getUpdatedAt()).isEqualTo(now);
        assertThat(resp.getFileUrl()).isEqualTo("http://file.url");
        assertThat(resp.getFileType()).isEqualTo("application/pdf");
    }
}
