package pl.bookstore.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(
        @NotBlank String firstName,
        @NotBlank String lastName
) {
}
