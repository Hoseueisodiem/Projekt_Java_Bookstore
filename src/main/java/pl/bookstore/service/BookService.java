package pl.bookstore.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bookstore.domain.Audiobook;
import pl.bookstore.domain.Author;
import pl.bookstore.domain.Book;
import pl.bookstore.domain.Ebook;
import pl.bookstore.domain.PrintedBook;
import pl.bookstore.dto.BookResponse;
import pl.bookstore.dto.CreateBookRequest;
import pl.bookstore.exception.BusinessException;
import pl.bookstore.exception.NotFoundException;
import pl.bookstore.repository.AuthorRepository;
import pl.bookstore.repository.BookRepository;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional
    public BookResponse create(CreateBookRequest request) {
        if (bookRepository.findByIsbn(request.isbn()).isPresent()) {
            throw new BusinessException("Book with this ISBN already exists");
        }
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new NotFoundException("Author not found: " + request.authorId()));

        Book book = buildBook(request);
        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setAuthor(author);
        book.setPrice(request.price());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.totalCopies());
        return BookResponse.from(bookRepository.save(book));
    }

    private Book buildBook(CreateBookRequest request) {
        return switch (request.format()) {
            case PRINTED -> {
                PrintedBook book = new PrintedBook();
                book.setPages(request.pages());
                book.setCoverType(request.coverType());
                yield book;
            }
            case EBOOK -> {
                Ebook book = new Ebook();
                book.setFileFormat(request.fileFormat());
                book.setFileSizeMb(request.fileSizeMb());
                yield book;
            }
            case AUDIOBOOK -> {
                Audiobook book = new Audiobook();
                book.setDurationMinutes(request.durationMinutes());
                book.setNarrator(request.narrator());
                yield book;
            }
        };
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream().map(BookResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found: " + id));
        return BookResponse.from(book);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> search(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream().map(BookResponse::from).toList();
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new NotFoundException("Book not found: " + id);
        }
        bookRepository.deleteById(id);
    }
}
