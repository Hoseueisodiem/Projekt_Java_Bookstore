package pl.bookstore.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.bookstore.domain.LoanStatus;
import pl.bookstore.dto.LoanResponse;
import pl.bookstore.exception.BusinessException;
import pl.bookstore.service.LoanService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoanControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    LoanService loanService;

    private LoanResponse sample(LoanStatus status) {
        return new LoanResponse(10L, "Clean Code", "filip", status,
                null, null, null, null, BigDecimal.ZERO);
    }

    @Test
    void reserveReturns201() throws Exception {
        when(loanService.reserve(1L, 2L)).thenReturn(sample(LoanStatus.RESERVED));

        mockMvc.perform(post("/api/loans/reserve").param("userId", "1").param("bookId", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RESERVED"));
    }

    @Test
    void reserveUnavailableReturns409() throws Exception {
        when(loanService.reserve(1L, 2L)).thenThrow(new BusinessException("Book is not available"));

        mockMvc.perform(post("/api/loans/reserve").param("userId", "1").param("bookId", "2"))
                .andExpect(status().isConflict());
    }

    @Test
    void borrowReturnsLoan() throws Exception {
        when(loanService.borrow(10L)).thenReturn(sample(LoanStatus.BORROWED));

        mockMvc.perform(post("/api/loans/10/borrow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BORROWED"));
    }

    @Test
    void returnReturnsLoan() throws Exception {
        when(loanService.returnBook(10L)).thenReturn(sample(LoanStatus.RETURNED));

        mockMvc.perform(post("/api/loans/10/return"))
                .andExpect(status().isOk());
    }

    @Test
    void cancelReturnsLoan() throws Exception {
        when(loanService.cancel(10L)).thenReturn(sample(LoanStatus.CANCELLED));

        mockMvc.perform(post("/api/loans/10/cancel"))
                .andExpect(status().isOk());
    }

    @Test
    void historyReturnsList() throws Exception {
        when(loanService.history(1L)).thenReturn(List.of(sample(LoanStatus.RESERVED)));

        mockMvc.perform(get("/api/loans/history").param("userId", "1"))
                .andExpect(status().isOk());
    }
}
