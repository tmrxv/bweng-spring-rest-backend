package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.entity.TimeCapsulePost;
import at.technikum.springrestbackend.repository.TimeCapsulePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class TimeCapsulePostServiceTest {

    private TimeCapsulePostRepository repo;
    private TimeCapsulePostService service;

    @BeforeEach
    public void setup() {
        repo = Mockito.mock(TimeCapsulePostRepository.class);
        service = new TimeCapsulePostService(repo);
    }

    @Test
    public void findLatestReturnsLimitedList() {
        TimeCapsulePost p1 = new TimeCapsulePost(); p1.setId(1L);
        TimeCapsulePost p2 = new TimeCapsulePost(); p2.setId(2L);
        when(repo.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of(p1, p2));

        var list = service.findLatest(5);
        assertEquals(2, list.size());
    }

    @Test
    public void updateChangesPostFields() {
        TimeCapsulePost existing = new TimeCapsulePost();
        existing.setId(5L);
        existing.setTitle("Old");
        existing.setMessage("Old message");
        existing.setSendAt(java.time.LocalDateTime.now());

        when(repo.findById(5L)).thenReturn(java.util.Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        var req = new at.technikum.springrestbackend.dto.TimeCapsulePostRequest();
        req.setTitle("New");
        req.setMessage("New message");
        req.setSendAt(existing.getSendAt().plusDays(1));

        var resp = service.update(5L, req);
        assertEquals("New", resp.getTitle());
        assertEquals("New message", resp.getMessage());
    }

    @Test
    public void deleteThrowsWhenPostMissing() {
        when(repo.existsById(999L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> service.delete(999L));
    }

    @Test
    public void attachFilePersistsMetadata() {
        TimeCapsulePost existing = new TimeCapsulePost();
        existing.setId(7L);

        when(repo.findById(7L)).thenReturn(java.util.Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        var resp = service.attachFile(7L, "/uploads/file.png", "image/png");
        assertEquals("/uploads/file.png", resp.getFileUrl());
        assertEquals("image/png", resp.getFileType());
    }
}