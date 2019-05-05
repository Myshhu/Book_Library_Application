package com.javaproject.javatask.rest;

import com.javaproject.javatask.repository.BookRepository;
import com.javaproject.javatask.repository.GoogleAPIBookRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class JavaTaskController {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private class ResourceNotFoundException extends RuntimeException {
    }

    /**
     *
     * @param isbn parameter needed to search for a book with a given number
     * @return JSONObject with book details if found, else send 404 response
     */
    @GetMapping(value = "/bookdetails/{ISBN}", produces = "application/json")
    public String bookDetailsByISBN(@PathVariable String isbn) {
        JSONObject foundBook = BookRepository.getBookByISBN(isbn);

        if (foundBook == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundBook.toString();
        }
    }

    /**
     *
     * @param isbn parameter needed to search for a book with a given number
     * @return JSONObject with book details if found, else send 404 response
     */
    @GetMapping(value = "/bookdetails/googleapi/{ISBN}", produces = "application/json")
    public String bookDetailsByISBNFromGoogleAPI(@PathVariable String isbn) {
        JSONObject foundBook = GoogleAPIBookRepository.getBookByISBN(isbn);

        if (foundBook == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundBook.toString();
        }
    }

    /**
     *
     * @return Empty array
     */
    @GetMapping(value = "/bookscategory/", produces = "application/json")
    public String findBooksWhenNotGivenCategory() {
        return new JSONArray().toString();
    }

    /**
     *
     * @param category Category from which we want to find books
     * @return JSONArray with found books informations, if no book found then it returns empty array
     */
    @GetMapping(value = "/bookscategory/{category}", produces = "application/json")
    public String findBooksDetailsByCategory(@PathVariable String category) {
        JSONArray foundBooks = BookRepository.getBooksByCategory(category);
        if (foundBooks == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundBooks.toString();
        }
    }

    /**
     *
     * @param category Category from which we want to find books
     * @return JSONArray with found books informations, if no book found then it returns empty array
     */
    @GetMapping(value = "/bookscategory/googleapi/{category}", produces = "application/json")
    public String findBooksDetailsByCategoryFromGoogleAPI(@PathVariable String category) {
        JSONArray foundBooks = GoogleAPIBookRepository.getBooksByCategory(category);
        if (foundBooks == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundBooks.toString();
        }
    }

    /**
     *
     * @return JSONArray with authors and their books average rating
     */
    @GetMapping(value = "/rating", produces = "application/json")
    public String findAuthors() {
        JSONArray ratings = BookRepository.getAuthorsRatings();
        if (ratings == null) {
            throw new ResourceNotFoundException();
        } else {
            return ratings.toString();
        }
    }

    /**
     *
     * @return JSONArray with authors and their books average rating
     */
    @GetMapping(value = "/rating/googleapi", produces = "application/json")
    public String findAuthorsFromGoogleAPI() {
        JSONArray foundAuthors = GoogleAPIBookRepository.getAuthorsRatings();
        if (foundAuthors == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundAuthors.toString();
        }
    }
}
