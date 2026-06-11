package pl.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.bookstore.domain.BookFormat;
import pl.bookstore.dto.BookResponse;
import pl.bookstore.dto.CreateBookRequest;
import pl.bookstore.exception.NotFoundException;
import pl.bookstore.service.BookService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BookService bookService;

    private BookResponse sample() {
        return new BookResponse(1L, "PRINTED", "Clean Code", "123", "Robert Martin",
                new BigDecimal("100.00"), 3, 5, 30);
    }

    @Test
    void findAllReturnsList() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void findByIdReturnsBook() throws Exception {
        when(bookService.findById(1L)).thenReturn(sample());

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.format").value("PRINTED"));
    }

    @Test
    void findByIdMissingReturns404() throws Exception {
        when(bookService.findById(99L)).thenThrow(new NotFoundException("Book not found: 99"));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchReturnsList() throws Exception {
        when(bookService.search("clean")).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/books/search").param("title", "clean"))
                .andExpect(status().isOk());
    }

    @Test
    void createReturns201() throws Exception {
        when(bookService.create(any())).thenReturn(sample());

        CreateBookRequest request = new CreateBookRequest(BookFormat.PRINTED, "Clean Code", "123", 1L,
                new BigDecimal("100.00"), 3, 400, "hardcover", null, null, null, null);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createInvalidReturns400() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReturns204() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }
}
