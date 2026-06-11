package pl.bookstore.dto;

import pl.bookstore.domain.Role;
import pl.bookstore.domain.User;

public record UserResponse(Long id, String username, String email, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
