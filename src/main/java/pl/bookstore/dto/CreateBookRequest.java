package pl.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pl.bookstore.domain.BookFormat;

import java.math.BigDecimal;

public record CreateBookRequest(
        @NotNull BookFormat format,
        @NotBlank String title,
        @NotBlank String isbn,
        @NotNull Long authorId,
        @NotNull @Positive BigDecimal price,
        @NotNull @Positive Integer totalCopies,
        // PrintedBook
        Integer pages,
        String coverType,
        // Ebook
        String fileFormat,
        BigDecimal fileSizeMb,
        // Audiobook
        Integer durationMinutes,
        String narrator
) {
}
