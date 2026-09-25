package br.com.maravilhasnoticias.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String FIELDS_PROPERTY = "fields";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        ProblemDetail problem = buildProblem(
                HttpStatus.BAD_REQUEST,
                "Dados inválidos",
                "Um ou mais campos são inválidos"
        );

        problem.setProperty(FIELDS_PROPERTY, extractFieldErrors(exception));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(
            InvalidCredentialsException exception
    ) {
        ProblemDetail problem = buildProblem(
                HttpStatus.UNAUTHORIZED,
                "Credenciais inválidas",
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "Recurso não encontrado", exception.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleConflict(EmailAlreadyExistsException exception) {
        return response(HttpStatus.CONFLICT, "Conflito", exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(ConflictException exception) {
        return response(HttpStatus.CONFLICT, "Conflito", exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException exception) {
        return response(HttpStatus.FORBIDDEN, "Acesso negado", "Você não possui permissão para acessar este recurso");
    }

    @ExceptionHandler(InvalidUploadException.class)
    public ResponseEntity<ProblemDetail> handleInvalidUpload(InvalidUploadException exception) {
        return response(HttpStatus.BAD_REQUEST, "Upload inválido", exception.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ProblemDetail> handleUploadSize(MaxUploadSizeExceededException exception) {
        return response(HttpStatus.BAD_REQUEST, "Upload inválido", "A imagem excede o limite permitido");
    }

    @ExceptionHandler(ExternalServiceConfigurationException.class)
    public ResponseEntity<ProblemDetail> handleExternalConfiguration(ExternalServiceConfigurationException exception) {
        return response(HttpStatus.SERVICE_UNAVAILABLE, "Serviço não configurado", exception.getMessage());
    }

    private ResponseEntity<ProblemDetail> response(HttpStatus status, String title, String detail) {
        return ResponseEntity.status(status).body(buildProblem(status, title, detail));
    }

    private ProblemDetail buildProblem(
            HttpStatus status,
            String title,
            String detail
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);

        return problem;
    }

    private Map<String, String> extractFieldErrors(MethodArgumentNotValidException exception) {
        Map<String, String> fields = new LinkedHashMap<>();

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        return fields;
    }
}
