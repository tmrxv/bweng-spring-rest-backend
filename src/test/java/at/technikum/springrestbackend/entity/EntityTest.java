package at.technikum.springrestbackend.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void userEntityGettersAndSetters() {
        User user = new User();
        OffsetDateTime created = OffsetDateTime.now();
        OffsetDateTime updated = OffsetDateTime.now();

        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("encoded");
        user.setCountry("AT");
        user.setProfileImageUrl("http://img.url");
        user.setRole("USER");
        user.setCreatedAt(created);
        user.setUpdatedAt(updated);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getCountry()).isEqualTo("AT");
        assertThat(user.getProfileImageUrl()).isEqualTo("http://img.url");
        assertThat(user.getRole()).isEqualTo("USER");
        assertThat(user.getCreatedAt()).isEqualTo(created);
        assertThat(user.getUpdatedAt()).isEqualTo(updated);
    }

    @Test
    void userAuthoritiesReturnRole() {
        User user = new User();
        user.setRole("ADMIN");

        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void userPreUpdateCallbackSetsUpdatedAt() throws InterruptedException {
        User user = new User();
        OffsetDateTime before = OffsetDateTime.now();
        Thread.sleep(5); // ensure time passes
        user.onUpdate();
        OffsetDateTime after = user.getUpdatedAt();

        assertThat(after).isAfterOrEqualTo(before);
    }

    @Test
    void timeCapsulePostGettersAndSetters() {
        User user = new User();
        user.setId(5L);

        TimeCapsulePost post = new TimeCapsulePost();
        LocalDateTime sendAt = LocalDateTime.now().plusDays(30);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        post.setId(10L);
        post.setUser(user);
        post.setTitle("Capsule Title");
        post.setMessage("Capsule Message");
        post.setSendAt(sendAt);
        post.setCreatedAt(created);
        post.setUpdatedAt(updated);
        post.setFileUrl("http://file.url");
        post.setFileType("image/jpeg");

        assertThat(post.getId()).isEqualTo(10L);
        assertThat(post.getUser()).isEqualTo(user);
        assertThat(post.getTitle()).isEqualTo("Capsule Title");
        assertThat(post.getMessage()).isEqualTo("Capsule Message");
        assertThat(post.getSendAt()).isEqualTo(sendAt);
        assertThat(post.getCreatedAt()).isEqualTo(created);
        assertThat(post.getUpdatedAt()).isEqualTo(updated);
        assertThat(post.getFileUrl()).isEqualTo("http://file.url");
        assertThat(post.getFileType()).isEqualTo("image/jpeg");
    }
}
