package com.payu.assessment.bookcatalogueweb.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public String handleBookNotFound(BookNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Book Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error"; // Thymeleaf template
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    public String handleBookAlreadyExists(BookAlreadyExistsException ex, Model model) {
        model.addAttribute("errorTitle", "Book Already Exists");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Unexpected Error");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
}
