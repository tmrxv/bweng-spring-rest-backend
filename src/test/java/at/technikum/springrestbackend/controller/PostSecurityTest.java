package at.technikum.springrestbackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostSecurityTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void anonymousCannotCreatePost() throws Exception {
        String json = "{\"title\":\"Hello\",\"message\":\"World\",\"sendAt\":\"2025-01-01T00:00:00\"}";

        mvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is4xxClientError());
    }
}