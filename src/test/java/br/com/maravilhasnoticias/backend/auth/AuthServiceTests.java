package br.com.maravilhasnoticias.backend.auth;

import br.com.maravilhasnoticias.backend.auth.dto.LoginRequest;
import br.com.maravilhasnoticias.backend.common.exception.InvalidCredentialsException;
import br.com.maravilhasnoticias.backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;

    @Test
    void shouldRejectUnknownCredentials() {
        when(userRepository.findByEmailIgnoreCase("reader@example.com")).thenReturn(Optional.empty());
        AuthService service = new AuthService(userRepository, passwordEncoder, jwtService);

        assertThatThrownBy(() -> service.login(new LoginRequest(" Reader@Example.com ", "12345678")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
