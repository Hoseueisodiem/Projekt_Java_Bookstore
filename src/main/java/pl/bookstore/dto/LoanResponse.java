package pl.bookstore.dto;

import pl.bookstore.domain.Loan;
import pl.bookstore.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record LoanResponse(
        Long id,
        String bookTitle,
        String username,
        LoanStatus status,
        Instant reservedAt,
        Instant borrowedAt,
        Instant dueAt,
        Instant returnedAt,
        BigDecimal penalty
) {

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getTitle(),
                loan.getUser().getUsername(),
                loan.getStatus(),
                loan.getReservedAt(),
                loan.getBorrowedAt(),
                loan.getDueAt(),
                loan.getReturnedAt(),
                loan.getPenalty()
        );
    }
}
