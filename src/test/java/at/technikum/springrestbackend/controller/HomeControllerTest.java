package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.TimeCapsulePostResponse;
import at.technikum.springrestbackend.service.TimeCapsulePostService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class HomeControllerTest {

    @Test
    void returnsLatestPosts() {
        TimeCapsulePostService service = Mockito.mock(TimeCapsulePostService.class);
        TimeCapsulePostResponse resp = new TimeCapsulePostResponse();
        resp.setTitle("Hello");
        when(service.findLatest(5)).thenReturn(List.of(resp));

        HomeController controller = new HomeController(service);
        ResponseEntity<List<TimeCapsulePostResponse>> result = controller.latest();

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().get(0).getTitle()).isEqualTo("Hello");
    }
}
