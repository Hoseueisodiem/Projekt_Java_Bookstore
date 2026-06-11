package pl.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.bookstore.domain.Role;
import pl.bookstore.dto.RegisterRequest;
import pl.bookstore.dto.UserResponse;
import pl.bookstore.exception.BusinessException;
import pl.bookstore.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @Test
    void registerReturns201() throws Exception {
        when(userService.register(any()))
                .thenReturn(new UserResponse(1L, "filip", "f@x.pl", Role.USER));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("filip", "secret1", "f@x.pl"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("filip"));
    }

    @Test
    void registerDuplicateReturns409() throws Exception {
        when(userService.register(any())).thenThrow(new BusinessException("Username already taken"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("filip", "secret1", "f@x.pl"))))
                .andExpect(status().isConflict());
    }

    @Test
    void registerInvalidReturns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
