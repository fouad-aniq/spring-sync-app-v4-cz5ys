package ai.shreds.adapter.primary;

import ai.shreds.application.exceptions.*;
import ai.shreds.application.ports.ApplicationMetadataOutputPort;
import ai.shreds.shared.dtos.SharedErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class AdapterGlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AdapterGlobalExceptionHandler.class);
    private final ApplicationMetadataOutputPort metadataOutputPort;

    @Autowired
    public AdapterGlobalExceptionHandler(ApplicationMetadataOutputPort metadataOutputPort) {
        this.metadataOutputPort = metadataOutputPort;
    }

    @ExceptionHandler(ApplicationValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SharedErrorResponseDTO> handleValidationException(ApplicationValidationException e) {
        logger.warn("Validation error: {}", e.getMessage(), e);
        metadataOutputPort.reportError(e);
        return ResponseEntity.badRequest().body(
            SharedErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                "VALIDATION_ERROR"
            )
        );
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<SharedErrorResponseDTO> handleNotFoundException(ApplicationNotFoundException e) {
        logger.warn("Resource not found: {}", e.getMessage(), e);
        metadataOutputPort.reportError(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            SharedErrorResponseDTO.of(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                "RESOURCE_NOT_FOUND"
            )
        );
    }

    @ExceptionHandler(ApplicationConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<SharedErrorResponseDTO> handleConflictException(ApplicationConflictException e) {
        logger.warn("Conflict detected: {}", e.getMessage(), e);
        metadataOutputPort.reportError(e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            SharedErrorResponseDTO.of(
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                "CONFLICT_ERROR"
            )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SharedErrorResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String details = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        logger.warn("Invalid method arguments: {}", details, e);
        metadataOutputPort.reportError(e);
        
        return ResponseEntity.badRequest().body(
            SharedErrorResponseDTO.withDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                "INVALID_ARGUMENTS",
                details
            )
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SharedErrorResponseDTO> handleConstraintViolation(ConstraintViolationException e) {
        String details = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        logger.warn("Constraint violation: {}", details, e);
        metadataOutputPort.reportError(e);

        return ResponseEntity.badRequest().body(
            SharedErrorResponseDTO.withDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Constraint violation",
                "CONSTRAINT_VIOLATION",
                details
            )
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<SharedErrorResponseDTO> handleException(Exception e) {
        logger.error("Unexpected error occurred: {}", e.getMessage(), e);
        metadataOutputPort.reportError(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            SharedErrorResponseDTO.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                "INTERNAL_ERROR"
            )
        );
    }
}
