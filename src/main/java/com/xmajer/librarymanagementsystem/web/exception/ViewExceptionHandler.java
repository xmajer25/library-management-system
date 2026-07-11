package com.xmajer.librarymanagementsystem.web.exception;

import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.web.BookViewController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(assignableTypes = BookViewController.class)
public class ViewExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.atDebug()
                .setMessage("Requested view entity was not found")
                .addKeyValue("exceptionType", ex.getClass().getSimpleName())
                .addKeyValue("path", request.getRequestURI())
                .addKeyValue("reason", ex.getMessage())
                .log();

        response.setStatus(HttpStatus.NOT_FOUND.value());

        return "error/404";
    }
}
