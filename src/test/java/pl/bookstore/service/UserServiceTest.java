package pl.bookstore.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.bookstore.domain.Role;
import pl.bookstore.dto.RegisterRequest;
import pl.bookstore.dto.UserResponse;
import pl.bookstore.exception.BusinessException;
import pl.bookstore.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    UserService userService;

    private UserService service() {
        return new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registerCreatesUserWithEncodedPasswordAndUserRole() {
        when(userRepository.existsByUsername("filip")).thenReturn(false);
        when(userRepository.existsByEmail("f@x.pl")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserResponse response = service().register(new RegisterRequest("filip", "secret1", "f@x.pl"));

        assertEquals("filip", response.username());
        assertEquals(Role.USER, response.role());
        verify(passwordEncoder).encode("secret1");
    }

    @Test
    void registerRejectsDuplicateUsername() {
        when(userRepository.existsByUsername("filip")).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> service().register(new RegisterRequest("filip", "secret1", "f@x.pl")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByUsername("filip")).thenReturn(false);
        when(userRepository.existsByEmail("f@x.pl")).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> service().register(new RegisterRequest("filip", "secret1", "f@x.pl")));
        verify(userRepository, never()).save(any());
    }
}
