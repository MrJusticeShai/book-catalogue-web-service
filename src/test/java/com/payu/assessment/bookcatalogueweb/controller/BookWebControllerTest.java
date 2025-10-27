package com.payu.assessment.bookcatalogueweb.controller;

import com.payu.assessment.bookcatalogueweb.exception.BookAlreadyExistsException;
import com.payu.assessment.bookcatalogueweb.exception.BookNotFoundException;
import com.payu.assessment.bookcatalogueweb.model.Book;
import com.payu.assessment.bookcatalogueweb.service.BookWebService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookWebController.class)
class BookWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookWebService bookWebService;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = new Book(1L, "Book1", "111", "24/10/2024", 149.99, "HARDCOVER");
        book2 = new Book(2L, "Book2", "222", "24/10/2024", 189.99, "SOFTCOVER");
    }

    // ----------------- getAllBooks -----------------
    @Test
    void getAllBooks_shouldReturnBookListView() throws Exception {
        when(bookWebService.getAllBooks()).thenReturn(Arrays.asList(book1, book2));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-list"))
                .andExpect(model().attributeExists("books"));

        verify(bookWebService, times(1)).getAllBooks();
    }

    // ----------------- showCreateForm -----------------
    @Test
    void showCreateForm_shouldReturnCreateView() throws Exception {
        mockMvc.perform(get("/books/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-create"))
                .andExpect(model().attributeExists("book"));
    }

    // ----------------- createBook -----------------
    @Test
    void createBook_shouldRedirectAfterCreation() throws Exception {
        when(bookWebService.createBook(any(Book.class))).thenReturn(book1);

        mockMvc.perform(post("/books/create")
                        .param("name", "Book1")
                        .param("isbn", "111")
                        .param("publishDate", "24/10/2024")
                        .param("price", "149.99")
                        .param("bookType", "HARDCOVER")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookWebService, times(1)).createBook(any(Book.class));
    }

    @Test
    void createBook_whenBookAlreadyExists_shouldShowErrorPage() throws Exception {
        when(bookWebService.createBook(any(Book.class)))
                .thenThrow(new BookAlreadyExistsException("Book with ISBN 111 already exists"));

        mockMvc.perform(post("/books/create")
                        .param("name", "Book1")
                        .param("isbn", "111")
                        .param("publishDate", "24/10/2024")
                        .param("price", "149.99")
                        .param("bookType", "HARDCOVER")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    // ----------------- showEditForm -----------------
    @Test
    void showEditForm_shouldReturnEditView() throws Exception {
        when(bookWebService.getBookByIsbn("111")).thenReturn(book1);

        mockMvc.perform(get("/books/edit/111"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-edit"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", book1));

        verify(bookWebService, times(1)).getBookByIsbn("111");
    }

    @Test
    void showEditForm_whenBookNotFound_shouldShowErrorPage() throws Exception {
        when(bookWebService.getBookByIsbn("111"))
                .thenThrow(new BookNotFoundException("Book with ISBN 111 not found"));

        mockMvc.perform(get("/books/edit/111"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    // ----------------- updateBook -----------------
    @Test
    void updateBook_shouldRedirectAfterUpdate() throws Exception {
        when(bookWebService.updateBookByIsbn(eq("111"), any(Book.class))).thenReturn(book1);

        mockMvc.perform(post("/books/update/111")
                        .param("name", "Updated Book")
                        .param("isbn", "111")
                        .param("publishDate", "24/10/2024")
                        .param("price", "199.99")
                        .param("bookType", "HARDCOVER")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookWebService, times(1)).updateBookByIsbn(eq("111"), any(Book.class));
    }

    @Test
    void updateBook_whenBookNotFound_shouldShowErrorPage() throws Exception {
        when(bookWebService.updateBookByIsbn(eq("111"), any(Book.class)))
                .thenThrow(new BookNotFoundException("Book with ISBN 111 not found"));

        mockMvc.perform(post("/books/update/111")
                        .param("name", "Updated Book")
                        .param("isbn", "111")
                        .param("publishDate", "24/10/2024")
                        .param("price", "199.99")
                        .param("bookType", "HARDCOVER")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    // ----------------- deleteBook -----------------
    @Test
    void deleteBook_shouldRedirectAfterDeletion() throws Exception {
        doNothing().when(bookWebService).deleteBookByIsbn("111");

        mockMvc.perform(get("/books/delete/111"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookWebService, times(1)).deleteBookByIsbn("111");
    }

    @Test
    void deleteBook_whenBookNotFound_shouldShowErrorPage() throws Exception {
        doThrow(new BookNotFoundException("Book with ISBN 111 not found"))
                .when(bookWebService).deleteBookByIsbn("111");

        mockMvc.perform(get("/books/delete/111"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    // ----------------- genericError -----------------
    @Test
    void genericException_shouldShowGenericErrorPage() throws Exception {
        when(bookWebService.getAllBooks()).thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}
