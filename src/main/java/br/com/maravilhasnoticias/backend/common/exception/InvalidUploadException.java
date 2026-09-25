package br.com.maravilhasnoticias.backend.common.exception;

public class InvalidUploadException extends RuntimeException {
    public InvalidUploadException(String message) { super(message); }
    public InvalidUploadException(String message, Throwable cause) { super(message, cause); }
}
