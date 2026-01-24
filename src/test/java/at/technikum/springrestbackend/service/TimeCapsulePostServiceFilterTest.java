package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.TimeCapsulePostRequest;
import at.technikum.springrestbackend.entity.TimeCapsulePost;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.TimeCapsulePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TimeCapsulePostServiceFilterTest {

    private TimeCapsulePostRepository repo;
    private TimeCapsulePostService service;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(TimeCapsulePostRepository.class);
        service = new TimeCapsulePostService(repo);
    }

    @Test
    void findAllWithUserIdFilter() {
        TimeCapsulePost post = new TimeCapsulePost();
        post.setId(1L);
        User user = new User();
        user.setId(10L);
        post.setUser(user);

        Page<TimeCapsulePost> page = new PageImpl<>(List.of(post));
        Pageable pageable = PageRequest.of(0, 10);

        when(repo.findByUserId(10L, pageable)).thenReturn(page);

        var result = service.findAll(Optional.of(10L), Optional.empty(), pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(10L);
    }

    @Test
    void findAllWithTitleFilter() {
        TimeCapsulePost post = new TimeCapsulePost();
        post.setId(2L);
        post.setTitle("Test Title");

        Page<TimeCapsulePost> page = new PageImpl<>(List.of(post));
        Pageable pageable = PageRequest.of(0, 10);

        when(repo.findByTitleContainingIgnoreCase("Test", pageable)).thenReturn(page);

        var result = service.findAll(Optional.empty(), Optional.of("Test"), pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Title");
    }

    @Test
    void findAllWithUserIdAndTitleFilter() {
        TimeCapsulePost post = new TimeCapsulePost();
        post.setId(3L);
        post.setTitle("Filtered");
        User user = new User();
        user.setId(20L);
        post.setUser(user);

        Page<TimeCapsulePost> page = new PageImpl<>(List.of(post));
        Pageable pageable = PageRequest.of(0, 10);

        when(repo.findByUserIdAndTitleContainingIgnoreCase(20L, "Filtered", pageable)).thenReturn(page);

        var result = service.findAll(Optional.of(20L), Optional.of("Filtered"), pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(20L);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Filtered");
    }

    @Test
    void findAllWithoutFilters() {
        TimeCapsulePost post = new TimeCapsulePost();
        post.setId(4L);

        Page<TimeCapsulePost> page = new PageImpl<>(List.of(post));
        Pageable pageable = PageRequest.of(0, 10);

        when(repo.findAll(pageable)).thenReturn(page);

        var result = service.findAll(Optional.empty(), Optional.empty(), pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void savePostWithUser() {
        User user = new User();
        user.setId(30L);

        TimeCapsulePostRequest req = new TimeCapsulePostRequest();
        req.setTitle("New Post");
        req.setMessage("Message");
        req.setSendAt(LocalDateTime.now().plusDays(5));

        TimeCapsulePost saved = new TimeCapsulePost();
        saved.setId(100L);
        saved.setUser(user);
        saved.setTitle("New Post");

        when(repo.save(any(TimeCapsulePost.class))).thenReturn(saved);

        var result = service.save(req, user);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getUserId()).isEqualTo(30L);
        assertThat(result.getTitle()).isEqualTo("New Post");
    }
}
