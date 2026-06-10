package pl.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bookstore.domain.Loan;
import pl.bookstore.domain.LoanStatus;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserId(Long userId);

    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);

    List<Loan> findByStatus(LoanStatus status);

    long countByBookIdAndStatus(Long bookId, LoanStatus status);
}
