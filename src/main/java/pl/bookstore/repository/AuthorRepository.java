package pl.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bookstore.domain.Author;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findByLastNameContainingIgnoreCase(String lastName);
}
