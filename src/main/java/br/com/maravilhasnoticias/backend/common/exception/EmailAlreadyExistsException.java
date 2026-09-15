package br.com.maravilhasnoticias.backend.common.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("Este e-mail já está cadastrado");
    }
}
