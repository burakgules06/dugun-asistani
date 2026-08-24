package com.venuecrm.app.exception;

import com.venuecrm.app.constant.ErrorMessageConstant;
import com.venuecrm.app.exception.runtime.ApplicationRuntimeException;
import com.venuecrm.app.model.GenericResponse;
import com.venuecrm.app.service.impl.GenericResponseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final GenericResponseService genericResponseService;

    @ExceptionHandler(ApplicationRuntimeException.class)
    public ResponseEntity<GenericResponse<Object>> handleApplicationRuntimeException(ApplicationRuntimeException e) {
        log.warn("İş kuralı hatası [{}]: {}", e.getError_code(), e.getMessage());
        return new ResponseEntity<>(genericResponseService.createResponseWithError(e.getMessage(), e.getError_code()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Beklenmeyen hata: {}", ex.getMessage(), ex);
        GenericResponse<Void> response = genericResponseService.createResponseWithError("Beklenmeyen bir hata oluştu.", ErrorMessageConstant.UNKNOWN_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream().map(error -> error.getObjectName() + ": " + error.getDefaultMessage()).collect(Collectors.joining(", "));
        log.warn("Validasyon hatası: {}", errorMessage);
        GenericResponse<Void> response = genericResponseService.createResponseWithError(errorMessage, ErrorMessageConstant.VALIDATION_ERROR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
