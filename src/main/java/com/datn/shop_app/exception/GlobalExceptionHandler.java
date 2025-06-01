package com.datn.shop_app.exception;

import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestControllerAdvice // Chỉ định lớp này xử lý ngoại lệ chung
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final LocalizationUtils localizationUtils;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseObject> handleGeneralException(Exception exception) {
        return ResponseEntity.internalServerError().body(
           ResponseObject.builder()
                   .status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .message("Hiện tại đang có lỗi!!!")
                   .build()
        );
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseObject> handleDataNotFoundException(DataNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseObject.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseObject> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseObject.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseObject> handlePhoneNumberAlreadyExistsException(PhoneNumberAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseObject.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseObject> handleInvalidDateException(InvalidDateException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseObject> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidParamException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseObject> handleInvalidParamException(InvalidParamException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(exception.getMessage())
                        .build());
    }


    @ExceptionHandler(MessagingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseObject> handleMessagingException(MessagingException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ResponseObject> handleAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseObject.builder()
                        .status(HttpStatus.FORBIDDEN)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.ACCESS_DENIED))
                        .build());
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseObject> handleIOException(IOException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.ACCESS_DENIED))
                        .build());
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseObject> handleFileNotFoundException(FileNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.ACCESS_DENIED))
                        .build());
    }
}
