package pl.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bookstore.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
