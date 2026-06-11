package pl.bookstore.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bookstore.domain.Author;
import pl.bookstore.dto.AuthorRequest;
import pl.bookstore.dto.AuthorResponse;
import pl.bookstore.exception.NotFoundException;
import pl.bookstore.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    AuthorRepository authorRepository;

    @InjectMocks
    AuthorService authorService;

    @Test
    void createReturnsSavedAuthor() {
        when(authorRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuthorResponse response = authorService.create(new AuthorRequest("Andrzej", "Sapkowski"));

        assertEquals("Andrzej", response.firstName());
        assertEquals("Sapkowski", response.lastName());
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void findAllMapsEntities() {
        Author a = new Author();
        a.setFirstName("Stanislaw");
        a.setLastName("Lem");
        when(authorRepository.findAll()).thenReturn(List.of(a));

        assertEquals(1, authorService.findAll().size());
    }

    @Test
    void findByIdReturnsAuthor() {
        Author a = new Author();
        a.setFirstName("Stanislaw");
        a.setLastName("Lem");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(a));

        assertEquals("Lem", authorService.findById(1L).lastName());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authorService.findById(99L));
    }

    @Test
    void deleteRemovesExistingAuthor() {
        when(authorRepository.existsById(1L)).thenReturn(true);

        authorService.delete(1L);

        verify(authorRepository).deleteById(1L);
    }

    @Test
    void deleteThrowsWhenMissing() {
        when(authorRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> authorService.delete(99L));
        verify(authorRepository, never()).deleteById(any());
    }
}
