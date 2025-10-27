package com.payu.assessment.bookcatalogueweb.controller;

import com.payu.assessment.bookcatalogueweb.model.Book;
import com.payu.assessment.bookcatalogueweb.service.BookWebService;
import com.payu.assessment.bookcatalogueweb.exception.BookAlreadyExistsException;
import com.payu.assessment.bookcatalogueweb.exception.BookNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookWebController {

    private final BookWebService bookWebService;

    @Autowired
    public BookWebController(BookWebService bookWebService) {
        this.bookWebService = bookWebService;
    }

    // List all books
    @GetMapping({"", "/"})
    public String getAllBooks(Model model) {
        List<Book> books = bookWebService.getAllBooks();
        model.addAttribute("books", books);
        return "book-list";
    }

    // Show create form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-create";
    }

    // Handle new book creation
    @PostMapping("/create")
    public String createBook(@ModelAttribute("book") Book book, Model model) {
        try {
            bookWebService.createBook(book);
            return "redirect:/books";
        } catch (BookAlreadyExistsException ex) {
            model.addAttribute("errorTitle", "Book Already Exists");
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        } catch (Exception ex) {
            model.addAttribute("errorTitle", "Unexpected Error");
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }

    // Show edit form
    @GetMapping("/edit/{isbn}")
    public String showEditForm(@PathVariable String isbn, Model model) {
        try {
            Book book = bookWebService.getBookByIsbn(isbn);
            model.addAttribute("book", book);
            return "book-edit";
        } catch (BookNotFoundException ex) {
            model.addAttribute("errorTitle", "Book Not Found");
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }

    // Handle book update
    @PostMapping("/update/{isbn}")
    public String updateBook(@PathVariable String isbn, @ModelAttribute("book") Book book, Model model) {
        try {
            bookWebService.updateBookByIsbn(isbn, book);
            return "redirect:/books";
        } catch (BookNotFoundException ex) {
            model.addAttribute("errorTitle", "Book Not Found");
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        } catch (Exception ex) {
            model.addAttribute("errorTitle", "Unexpected Error");
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }

    // Handle delete
    @GetMapping("/delete/{isbn}")
    public String deleteBook(@PathVariable String isbn, Model model) {
        try {
            bookWebService.deleteBookByIsbn(isbn);
            return "redirect:/books";
        } catch (BookNotFoundException ex) {
            model.addAttribute("errorTitle", "Book Not Found");
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        } catch (Exception ex) {
            model.addAttribute("errorTitle", "Unexpected Error");
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }
}
