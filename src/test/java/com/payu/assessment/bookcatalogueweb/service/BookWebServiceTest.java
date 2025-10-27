package com.payu.assessment.bookcatalogueweb.service;

import com.payu.assessment.bookcatalogueweb.exception.BookAlreadyExistsException;
import com.payu.assessment.bookcatalogueweb.exception.BookNotFoundException;
import com.payu.assessment.bookcatalogueweb.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookWebServiceTest {

    @Mock
    private Client client;
    @Mock
    private WebTarget target;
    @Mock
    private Invocation.Builder builder;
    @Mock
    private Response response;

    private final String baseUrl = "http://localhost:9001/api/books";

    private BookWebService bookWebService;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        bookWebService = new BookWebService(client, baseUrl);

        book1 = new Book(1L, "Book1", "111", "24/10/2024", 149.99, "HARDCOVER");
        book2 = new Book(2L, "Book2", "222", "24/10/2024", 189.99, "SOFTCOVER");
    }

    // ----------------- getAllBooks -----------------
    @Test
    void getAllBooks_returnsListOfBooks() {
        when(client.target(baseUrl)).thenReturn(target);
        when(target.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.get(Book[].class)).thenReturn(new Book[]{book1, book2});

        List<Book> result = bookWebService.getAllBooks();

        assertEquals(2, result.size());
        assertEquals("Book1", result.get(0).getName());
    }

    // ----------------- getBookByIsbn -----------------
    @Test
    void getBookByIsbn_existing_returnsBook() {
        when(client.target(baseUrl + "/isbn/111")).thenReturn(target);
        when(target.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.get()).thenReturn(response);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(Book.class)).thenReturn(book1);

        Book result = bookWebService.getBookByIsbn("111");

        assertEquals("Book1", result.getName());
    }

    @Test
    void getBookByIsbn_notFound_throwsException() {
        when(client.target(baseUrl + "/isbn/111")).thenReturn(target);
        when(target.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.get()).thenReturn(response);
        when(response.getStatus()).thenReturn(404);

        assertThrows(BookNotFoundException.class, () -> bookWebService.getBookByIsbn("111"));
    }

    // ----------------- createBook -----------------
    @Test
    void createBook_uniqueBook_returnsCreatedBook() {
        when(client.target(baseUrl)).thenReturn(target);
        when(target.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.post(any(Entity.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(response.readEntity(Book.class)).thenReturn(book1);

        Book result = bookWebService.createBook(book1);

        assertEquals("Book1", result.getName());
        verify(builder, times(1)).post(any(Entity.class));
    }

    @Test
    void createBook_existingIsbn_throwsException() {
        when(client.target(baseUrl)).thenReturn(target);
        when(target.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.post(any(Entity.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(409);

        assertThrows(BookAlreadyExistsException.class, () -> bookWebService.createBook(book1));
    }

    // ----------------- updateBookByIsbn -----------------
    @Test
    void updateBook_existing_updatesSuccessfully() {
        when(client.target(baseUrl + "/isbn/111")).thenReturn(target);
        when(target.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.put(any(Entity.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(Book.class)).thenReturn(book2);

        Book result = bookWebService.updateBookByIsbn("111", book2);

        assertEquals("Book2", result.getName());
    }

    @Test
    void updateBook_notFound_throwsException() {
        when(client.target(baseUrl + "/isbn/111")).thenReturn(target);
        when(target.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.put(any(Entity.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(404);

        assertThrows(BookNotFoundException.class, () -> bookWebService.updateBookByIsbn("111", book2));
    }

    // ----------------- deleteBookByIsbn -----------------
    @Test
    void deleteBook_existing_deletesSuccessfully() {
        when(client.target(baseUrl + "/isbn/111")).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.delete()).thenReturn(response);
        when(response.getStatus()).thenReturn(204);

        assertDoesNotThrow(() -> bookWebService.deleteBookByIsbn("111"));
    }

    @Test
    void deleteBook_notFound_throwsException() {
        when(client.target(baseUrl + "/isbn/111")).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.delete()).thenReturn(response);
        when(response.getStatus()).thenReturn(404);

        assertThrows(BookNotFoundException.class, () -> bookWebService.deleteBookByIsbn("111"));
    }
}
