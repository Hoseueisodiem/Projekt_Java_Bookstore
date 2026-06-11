package pl.bookstore.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bookstore.domain.Author;
import pl.bookstore.domain.Book;
import pl.bookstore.domain.BookFormat;
import pl.bookstore.domain.PrintedBook;
import pl.bookstore.dto.BookResponse;
import pl.bookstore.dto.CreateBookRequest;
import pl.bookstore.exception.BusinessException;
import pl.bookstore.exception.NotFoundException;
import pl.bookstore.repository.AuthorRepository;
import pl.bookstore.repository.BookRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    AuthorRepository authorRepository;

    @InjectMocks
    BookService bookService;

    private Author author() {
        Author a = new Author();
        a.setId(1L);
        a.setFirstName("Robert");
        a.setLastName("Martin");
        return a;
    }

    private CreateBookRequest request(BookFormat format) {
        return new CreateBookRequest(format, "Clean Code", "978-0132350884", 1L,
                new BigDecimal("120.00"), 3,
                464, "hardcover",
                "PDF", new BigDecimal("4.50"),
                600, "Some Narrator");
    }

    @Test
    void createPrintedBookSetsCopiesAndPolymorphicData() {
        when(bookRepository.findByIsbn(any())).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author()));
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        BookResponse response = bookService.create(request(BookFormat.PRINTED));

        assertEquals("PRINTED", response.format());
        assertEquals(30, response.loanPeriodDays());
        assertEquals(3, response.availableCopies());
        assertEquals("Robert Martin", response.author());
    }

    @Test
    void createEbookUsesEbookLoanPeriod() {
        when(bookRepository.findByIsbn(any())).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author()));
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        BookResponse response = bookService.create(request(BookFormat.EBOOK));

        assertEquals("EBOOK", response.format());
        assertEquals(14, response.loanPeriodDays());
    }

    @Test
    void createAudiobookUsesAudiobookLoanPeriod() {
        when(bookRepository.findByIsbn(any())).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author()));
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        BookResponse response = bookService.create(request(BookFormat.AUDIOBOOK));

        assertEquals("AUDIOBOOK", response.format());
        assertEquals(21, response.loanPeriodDays());
    }

    @Test
    void createRejectsDuplicateIsbn() {
        when(bookRepository.findByIsbn(any())).thenReturn(Optional.of(new PrintedBook()));

        assertThrows(BusinessException.class, () -> bookService.create(request(BookFormat.PRINTED)));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void createThrowsWhenAuthorMissing() {
        when(bookRepository.findByIsbn(any())).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.create(request(BookFormat.PRINTED)));
    }

    @Test
    void findByIdReturnsBook() {
        when(bookRepository.findById(5L)).thenReturn(Optional.of(samplePrinted()));

        assertEquals("Clean Code", bookService.findById(5L).title());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(bookRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.findById(5L));
    }

    @Test
    void searchMapsResults() {
        when(bookRepository.findByTitleContainingIgnoreCase("clean"))
                .thenReturn(List.of(samplePrinted()));

        assertEquals(1, bookService.search("clean").size());
    }

    @Test
    void findAllMapsResults() {
        when(bookRepository.findAll()).thenReturn(List.of(samplePrinted()));

        assertEquals(1, bookService.findAll().size());
    }

    @Test
    void deleteThrowsWhenMissing() {
        when(bookRepository.existsById(7L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookService.delete(7L));
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void deleteRemovesExistingBook() {
        when(bookRepository.existsById(7L)).thenReturn(true);

        bookService.delete(7L);

        verify(bookRepository).deleteById(7L);
    }

    private Book samplePrinted() {
        PrintedBook b = new PrintedBook();
        b.setId(5L);
        b.setTitle("Clean Code");
        b.setIsbn("978-0132350884");
        b.setAuthor(author());
        b.setPrice(new BigDecimal("120.00"));
        b.setTotalCopies(3);
        b.setAvailableCopies(3);
        return b;
    }
}
