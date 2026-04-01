package batial.geekshop.api.service;

import batial.geekshop.api.exception.ApiException;
import batial.geekshop.api.model.User;
import batial.geekshop.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_shouldCreateUser_whenEmailIsNotTaken() {
        when(userRepository.existsByEmail("sebas@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.register("Sebas", "sebas@mail.com", "123456");

        assertThat(result.getName()).isEqualTo("Sebas");
        assertThat(result.getEmail()).isEqualTo("sebas@mail.com");
        assertThat(result.getPasswordHash()).isEqualTo("hashed_password");
        assertThat(result.getRole()).isEqualTo(User.Role.CUSTOMER);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail("sebas@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("Sebas", "sebas@mail.com", "123456"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByEmail_shouldReturnUser_whenExists() {
        User user = User.builder()
                .name("Sebas")
                .email("sebas@mail.com")
                .passwordHash("hashed")
                .role(User.Role.CUSTOMER)
                .build();

        when(userRepository.findByEmail("sebas@mail.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("sebas@mail.com");

        assertThat(result.getEmail()).isEqualTo("sebas@mail.com");
    }

    @Test
    void findByEmail_shouldThrowException_whenNotFound() {
        when(userRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("noexiste@mail.com"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(id))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void checkPassword_shouldReturnTrue_whenPasswordMatches() {
        when(passwordEncoder.matches("123456", "hashed")).thenReturn(true);

        boolean result = userService.checkPassword("123456", "hashed");

        assertThat(result).isTrue();
    }
}