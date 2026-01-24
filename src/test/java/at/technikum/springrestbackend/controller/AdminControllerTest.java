package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.TimeCapsulePostResponse;
import at.technikum.springrestbackend.dto.UserResponse;
import at.technikum.springrestbackend.service.TimeCapsulePostService;
import at.technikum.springrestbackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdminControllerTest {

    @Test
    void adminCanListUsers() {
        UserService userService = Mockito.mock(UserService.class);
        TimeCapsulePostService postService = Mockito.mock(TimeCapsulePostService.class);

        Page<UserResponse> users = new PageImpl<>(List.of(new UserResponse()), PageRequest.of(0, 10), 1);
        when(userService.listUsers(any())).thenReturn(users);

        AdminController controller = new AdminController(userService, postService);
        ResponseEntity<Page<UserResponse>> response = controller.listUsers(PageRequest.of(0, 10));

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    void adminCanListPosts() {
        UserService userService = Mockito.mock(UserService.class);
        TimeCapsulePostService postService = Mockito.mock(TimeCapsulePostService.class);

        Page<TimeCapsulePostResponse> posts = new PageImpl<>(List.of(new TimeCapsulePostResponse()), PageRequest.of(0, 10), 1);
        when(postService.findAll(any(), any(), any())).thenReturn(posts);

        AdminController controller = new AdminController(userService, postService);
        ResponseEntity<Page<TimeCapsulePostResponse>> response = controller.listPosts(PageRequest.of(0, 10));

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
    }
}
