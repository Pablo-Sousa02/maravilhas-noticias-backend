package br.com.maravilhasnoticias.backend.auth;

import br.com.maravilhasnoticias.backend.auth.dto.RegisterRequest;
import br.com.maravilhasnoticias.backend.common.exception.EmailAlreadyExistsException;
import br.com.maravilhasnoticias.backend.user.User;
import br.com.maravilhasnoticias.backend.user.UserRepository;
import br.com.maravilhasnoticias.backend.user.UserRole;
import br.com.maravilhasnoticias.backend.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.maravilhasnoticias.backend.auth.dto.LoginRequest;
import br.com.maravilhasnoticias.backend.auth.dto.LoginResponse;
import br.com.maravilhasnoticias.backend.common.exception.InvalidCredentialsException;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User(
                request.name().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.password()),
                UserRole.USER
        );

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()
                || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                UserResponse.from(user)
        );
    }
}