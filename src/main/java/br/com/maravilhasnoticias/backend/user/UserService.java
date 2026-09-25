package br.com.maravilhasnoticias.backend.user;

import br.com.maravilhasnoticias.backend.common.exception.ResourceNotFoundException;
import br.com.maravilhasnoticias.backend.user.dto.UserResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getAuthenticatedUser(Jwt jwt) {
        UUID userId;
        try {
            userId = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException exception) {
            throw new ResourceNotFoundException("Usuário autenticado não encontrado");
        }

        return userRepository.findById(userId)
                .filter(User::isActive)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
    }
}
