package pl.bookstore.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bookstore.dto.AuthorRequest;
import pl.bookstore.dto.AuthorResponse;
import pl.bookstore.service.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Authors", description = "Zarzadzanie autorami")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public List<AuthorResponse> findAll() {
        return authorService.findAll();
    }

    @GetMapping("/{id}")
    public AuthorResponse findById(@PathVariable Long id) {
        return authorService.findById(id);
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> create(@Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
