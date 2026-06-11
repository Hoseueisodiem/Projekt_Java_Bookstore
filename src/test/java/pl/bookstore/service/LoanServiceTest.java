package pl.bookstore.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bookstore.domain.Author;
import pl.bookstore.domain.Loan;
import pl.bookstore.domain.LoanStatus;
import pl.bookstore.domain.PrintedBook;
import pl.bookstore.domain.Role;
import pl.bookstore.domain.User;
import pl.bookstore.dto.LoanResponse;
import pl.bookstore.exception.BusinessException;
import pl.bookstore.exception.NotFoundException;
import pl.bookstore.repository.BookRepository;
import pl.bookstore.repository.LoanRepository;
import pl.bookstore.repository.UserRepository;
import pl.bookstore.service.penalty.StandardPenaltyStrategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    LoanRepository loanRepository;
    @Mock
    BookRepository bookRepository;
    @Mock
    UserRepository userRepository;

    private LoanService service() {
        return new LoanService(loanRepository, bookRepository, userRepository, new StandardPenaltyStrategy());
    }

    private User user() {
        User u = new User();
        u.setId(1L);
        u.setUsername("filip");
        u.setEmail("f@x.pl");
        u.setRole(Role.USER);
        u.setPassword("x");
        return u;
    }

    private PrintedBook book(int available) {
        PrintedBook b = new PrintedBook();
        b.setId(2L);
        b.setTitle("Clean Code");
        b.setIsbn("123");
        b.setAvailableCopies(available);
        b.setTotalCopies(5);
        b.setPrice(new BigDecimal("50.00"));
        Author a = new Author();
        a.setFirstName("Robert");
        a.setLastName("Martin");
        b.setAuthor(a);
        return b;
    }

    private Loan loanWith(LoanStatus status, PrintedBook book) {
        Loan loan = new Loan();
        loan.setId(10L);
        loan.setUser(user());
        loan.setBook(book);
        loan.setStatus(status);
        return loan;
    }

    @Test
    void reserveDecrementsAvailableCopies() {
        PrintedBook book = book(2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoanResponse response = service().reserve(1L, 2L);

        assertEquals(LoanStatus.RESERVED, response.status());
        assertEquals(1, book.getAvailableCopies());
    }

    @Test
    void reserveRejectsUnavailableBook() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book(0)));

        assertThrows(BusinessException.class, () -> service().reserve(1L, 2L));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void reserveThrowsWhenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service().reserve(1L, 2L));
    }

    @Test
    void reserveThrowsWhenBookMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service().reserve(1L, 2L));
    }

    @Test
    void borrowSetsDueDateFromBookLoanPeriod() {
        Loan loan = loanWith(LoanStatus.RESERVED, book(1));
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service().borrow(10L);

        assertEquals(LoanStatus.BORROWED, loan.getStatus());
        // PrintedBook -> 30 dni (polimorfizm)
        assertEquals(30, Duration.between(loan.getBorrowedAt(), loan.getDueAt()).toDays());
    }

    @Test
    void borrowRejectsNonReservedLoan() {
        Loan loan = loanWith(LoanStatus.BORROWED, book(1));
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        assertThrows(BusinessException.class, () -> service().borrow(10L));
    }

    @Test
    void returnWithoutDelayHasNoPenalty() {
        PrintedBook book = book(0);
        Loan loan = loanWith(LoanStatus.BORROWED, book);
        loan.setDueAt(Instant.now().plus(Duration.ofDays(5)));
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoanResponse response = service().returnBook(10L);

        assertEquals(LoanStatus.RETURNED, response.status());
        assertEquals(0, loan.getPenalty().compareTo(BigDecimal.ZERO));
        assertEquals(1, book.getAvailableCopies());
    }

    @Test
    void returnOverdueChargesPenalty() {
        PrintedBook book = book(0);
        Loan loan = loanWith(LoanStatus.BORROWED, book);
        loan.setDueAt(Instant.now().minus(Duration.ofDays(10)));
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service().returnBook(10L);

        // 10 dni spoznienia * 1 zl (StandardPenaltyStrategy)
        assertEquals(0, loan.getPenalty().compareTo(new BigDecimal("10")));
    }

    @Test
    void returnRejectsNonBorrowedLoan() {
        Loan loan = loanWith(LoanStatus.RESERVED, book(1));
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        assertThrows(BusinessException.class, () -> service().returnBook(10L));
    }

    @Test
    void cancelReleasesCopy() {
        PrintedBook book = book(0);
        Loan loan = loanWith(LoanStatus.RESERVED, book);
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service().cancel(10L);

        assertEquals(LoanStatus.CANCELLED, loan.getStatus());
        assertEquals(1, book.getAvailableCopies());
    }

    @Test
    void cancelRejectsNonReservedLoan() {
        Loan loan = loanWith(LoanStatus.BORROWED, book(1));
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        assertThrows(BusinessException.class, () -> service().cancel(10L));
    }

    @Test
    void historyReturnsUserLoans() {
        when(loanRepository.findByUserId(1L)).thenReturn(List.of(loanWith(LoanStatus.RESERVED, book(1))));

        assertEquals(1, service().history(1L).size());
    }
}
