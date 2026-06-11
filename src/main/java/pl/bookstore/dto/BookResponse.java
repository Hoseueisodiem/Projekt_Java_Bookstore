package pl.bookstore.dto;

import pl.bookstore.domain.Book;

import java.math.BigDecimal;

public record BookResponse(
        Long id,
        String format,
        String title,
        String isbn,
        String author,
        BigDecimal price,
        int availableCopies,
        int totalCopies,
        int loanPeriodDays
) {

    public static BookResponse from(Book book) {
        String authorName = book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName();
        return new BookResponse(
                book.getId(),
                book.format().name(),
                book.getTitle(),
                book.getIsbn(),
                authorName,
                book.getPrice(),
                book.getAvailableCopies(),
                book.getTotalCopies(),
                book.loanPeriodDays()
        );
    }
}
