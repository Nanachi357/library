package com.ponomarenko.library.advice;

import com.ponomarenko.library.exception.NotFoundException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class NotFoundAdvice {

    @ResponseBody
    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String notFoundHandler(ChangeSetPersister.NotFoundException ex) {
        return ex.getMessage();
    }
}
