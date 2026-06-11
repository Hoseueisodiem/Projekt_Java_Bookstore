package pl.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.bookstore.dto.AuthorRequest;
import pl.bookstore.dto.AuthorResponse;
import pl.bookstore.service.AuthorService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthorService authorService;

    @Test
    void findAllReturnsList() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(new AuthorResponse(1L, "Stanislaw", "Lem")));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Lem"));
    }

    @Test
    void findByIdReturnsAuthor() throws Exception {
        when(authorService.findById(1L)).thenReturn(new AuthorResponse(1L, "Stanislaw", "Lem"));

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createReturns201() throws Exception {
        when(authorService.create(any())).thenReturn(new AuthorResponse(1L, "Andrzej", "Sapkowski"));

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthorRequest("Andrzej", "Sapkowski"))))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteReturns204() throws Exception {
        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());
    }
}
