package com.javaproject.javatask.rest;

import com.javaproject.javatask.repository.BookRepository;
import com.javaproject.javatask.repository.GoogleAPIBookRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JavaTaskController {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private class ResourceNotFoundException extends RuntimeException {
    }

    /**
     * Find book by given ISBN in repository from file
     *
     * @param isbn parameter needed to search for a book with a given number
     * @return JSONObject with book details if found, else send 404 response
     */
    @GetMapping(value = "/bookdetails/{isbn}", produces = "application/json")
    public String bookDetailsByISBN(@PathVariable String isbn) {
        JSONObject foundBook = BookRepository.getBookByISBN(isbn);

        if (foundBook == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundBook.toString();
        }
    }

    /**
     * Find book by given ISBN in repository from Google Books API
     *
     * @param isbn parameter needed to search for a book with a given number
     * @return JSONObject with book details if found, else send 404 response
     */
    @GetMapping(value = "/bookdetails/googleapi/{isbn}", produces = "application/json")
    public String bookDetailsByISBNFromGoogleAPI(@PathVariable String isbn) {
        JSONObject foundBook = GoogleAPIBookRepository.getBookByISBN(isbn);

        if (foundBook == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundBook.toString();
        }
    }

    /**
     * Return empty array when category is not specified
     *
     * @return Empty array
     */
    @GetMapping(value = "/bookscategory/", produces = "application/json")
    public String findBooksWhenNotGivenCategory() {
        return new JSONArray().toString();
    }

    /**
     * Find books in given category in repository from file
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
     * Find books in given category in repository from Google Books API
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
     * Find authors and their books average rating in repository from file
     *
     * @return JSONArray with authors and their books average rating
     */
    @GetMapping(value = "/rating", produces = "application/json")
    public String findAuthorsRatings() {
        JSONArray ratings = BookRepository.getAuthorsRatings();
        if (ratings == null) {
            throw new ResourceNotFoundException();
        } else {
            return ratings.toString();
        }
    }

    /**
     * Find authors and their books average rating in repository from Google Books API
     *
     * @return JSONArray with authors and their books average rating
     */
    @GetMapping(value = "/rating/googleapi", produces = "application/json")
    public String findAuthorsRatingsFromGoogleAPI() {
        JSONArray foundAuthors = GoogleAPIBookRepository.getAuthorsRatings();
        if (foundAuthors == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundAuthors.toString();
        }
    }
}
