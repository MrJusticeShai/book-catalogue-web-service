package com.payu.assessment.bookcatalogueweb.service;

import com.payu.assessment.bookcatalogueweb.model.Book;
import com.payu.assessment.bookcatalogueweb.exception.BookAlreadyExistsException;
import com.payu.assessment.bookcatalogueweb.exception.BookNotFoundException;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

@Service
public class BookWebService {

    private final Client client = ClientBuilder.newClient().register(JacksonFeature.class);

    @Value("${book.api.base-url}")
    private String apiBaseUrl;

    public List<Book> getAllBooks() {
        Book[] books = client.target(apiBaseUrl)
                .request(MediaType.APPLICATION_JSON)
                .get(Book[].class);
        return Arrays.asList(books);
    }

    public Book getBookByIsbn(String isbn) {
        Response response = client.target(apiBaseUrl + "/isbn/" + isbn)
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() == 404) {
            throw new BookNotFoundException("Book with ISBN " + isbn + " not found");
        }
        return response.readEntity(Book.class);
    }

    public Book createBook(Book book) {
        Response response = client.target(apiBaseUrl)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(book, MediaType.APPLICATION_JSON));

        if (response.getStatus() == 409) {
            throw new BookAlreadyExistsException("Book with ISBN " + book.getIsbn() + " already exists");
        }

        return response.readEntity(Book.class);
    }

    public Book updateBookByIsbn(String isbn, Book book) {
        Response response = client.target(apiBaseUrl + "/isbn/" + isbn)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(book, MediaType.APPLICATION_JSON));

        if (response.getStatus() == 404) {
            throw new BookNotFoundException("Book with ISBN " + isbn + " not found");
        }

        return response.readEntity(Book.class);
    }

    public void deleteBookByIsbn(String isbn) {
        Response response = client.target(apiBaseUrl + "/isbn/" + isbn)
                .request()
                .delete();

        if (response.getStatus() == 404) {
            throw new BookNotFoundException("Book with ISBN " + isbn + " not found");
        }
    }
}
