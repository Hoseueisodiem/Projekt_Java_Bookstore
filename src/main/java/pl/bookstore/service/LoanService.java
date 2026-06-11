package pl.bookstore.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bookstore.domain.Book;
import pl.bookstore.domain.Loan;
import pl.bookstore.domain.LoanStatus;
import pl.bookstore.domain.User;
import pl.bookstore.dto.LoanResponse;
import pl.bookstore.exception.BusinessException;
import pl.bookstore.exception.NotFoundException;
import pl.bookstore.repository.BookRepository;
import pl.bookstore.repository.LoanRepository;
import pl.bookstore.repository.UserRepository;
import pl.bookstore.service.penalty.PenaltyStrategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PenaltyStrategy penaltyStrategy;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository,
                       PenaltyStrategy penaltyStrategy) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.penaltyStrategy = penaltyStrategy;
    }

    @Transactional
    public LoanResponse reserve(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found: " + bookId));

        if (!book.isAvailable()) {
            throw new BusinessException("Book is not available");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setStatus(LoanStatus.RESERVED);
        loan.setReservedAt(Instant.now());
        return LoanResponse.from(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse borrow(Long loanId) {
        Loan loan = getLoan(loanId);
        if (loan.getStatus() != LoanStatus.RESERVED) {
            throw new BusinessException("Only a reserved loan can be borrowed");
        }
        Instant now = Instant.now();
        loan.setStatus(LoanStatus.BORROWED);
        loan.setBorrowedAt(now);
        // polimorfizm: okres zalezy od typu ksiazki
        loan.setDueAt(now.plus(Duration.ofDays(loan.getBook().loanPeriodDays())));
        return LoanResponse.from(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse returnBook(Long loanId) {
        Loan loan = getLoan(loanId);
        if (loan.getStatus() != LoanStatus.BORROWED) {
            throw new BusinessException("Only a borrowed loan can be returned");
        }
        Instant now = Instant.now();
        loan.setReturnedAt(now);
        loan.setStatus(LoanStatus.RETURNED);
        loan.setPenalty(calculatePenalty(loan, now));

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        return LoanResponse.from(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse cancel(Long loanId) {
        Loan loan = getLoan(loanId);
        if (loan.getStatus() != LoanStatus.RESERVED) {
            throw new BusinessException("Only a reserved loan can be cancelled");
        }
        loan.setStatus(LoanStatus.CANCELLED);
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        return LoanResponse.from(loanRepository.save(loan));
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> history(Long userId) {
        return loanRepository.findByUserId(userId).stream().map(LoanResponse::from).toList();
    }

    private BigDecimal calculatePenalty(Loan loan, Instant returnTime) {
        if (loan.getDueAt() == null || !returnTime.isAfter(loan.getDueAt())) {
            return BigDecimal.ZERO;
        }
        long overdueDays = Duration.between(loan.getDueAt(), returnTime).toDays();
        return penaltyStrategy.calculate(overdueDays);
    }

    private Loan getLoan(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found: " + loanId));
    }
}
