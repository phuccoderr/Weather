package com.skyapi.weatherforecast;

import com.skyapi.weatherforecast.hourly.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // hiển thị lỗi để dev bt rõ ràng nguyên nhân



    @ExceptionHandler(Exception.class) //bắt lỗi exception
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // phương thức lỗi 500
    @ResponseBody //trả về 1 đối tượng k nên xử lý trả qua trang web
    public ErrorDTO handleGenericException(HttpServletRequest request,Exception exception) {
        ErrorDTO error = new ErrorDTO();

        error.setTimeStamp(new Date());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.addError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        error.setPath(request.getServletPath());

        LOGGER.error(exception.getMessage(),exception);
        return error;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleBadRequestException(HttpServletRequest request,Exception exception) {
        ErrorDTO error = new ErrorDTO();

        error.setTimeStamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.addError(exception.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(exception.getMessage(),exception);
        return error;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleConstraintViolationException(HttpServletRequest request,Exception exception) {
        ErrorDTO error = new ErrorDTO();

        ConstraintViolationException violationException = (ConstraintViolationException) exception;

        error.setTimeStamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(request.getServletPath());

        var constraintViolations = violationException.getConstraintViolations();

        constraintViolations.forEach(constraint -> {
            error.addError(constraint.getPropertyPath() + ": " + constraint.getMessage());
        });

        LOGGER.error(exception.getMessage(),exception);
        return error;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorDTO error = new ErrorDTO();
        error.setTimeStamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(((ServletWebRequest) request).getRequest().getServletPath());

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        fieldErrors.forEach(
                fieldError -> {
                    error.addError(fieldError.getDefaultMessage());
                }
        );
        LOGGER.error(ex.getMessage(),ex);
        return new ResponseEntity<>(error,headers,status);
    }
}
