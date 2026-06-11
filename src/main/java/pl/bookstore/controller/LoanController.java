package pl.bookstore.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bookstore.dto.LoanResponse;
import pl.bookstore.service.LoanService;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loans", description = "Rezerwacje, wypozyczenia i zwroty")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<LoanResponse> reserve(@RequestParam Long userId, @RequestParam Long bookId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.reserve(userId, bookId));
    }

    @PostMapping("/{id}/borrow")
    public LoanResponse borrow(@PathVariable Long id) {
        return loanService.borrow(id);
    }

    @PostMapping("/{id}/return")
    public LoanResponse returnBook(@PathVariable Long id) {
        return loanService.returnBook(id);
    }

    @PostMapping("/{id}/cancel")
    public LoanResponse cancel(@PathVariable Long id) {
        return loanService.cancel(id);
    }

    @GetMapping("/history")
    public List<LoanResponse> history(@RequestParam Long userId) {
        return loanService.history(userId);
    }
}
