package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.entity.TimeCapsulePost;
import at.technikum.springrestbackend.repository.TimeCapsulePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}