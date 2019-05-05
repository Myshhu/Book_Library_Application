package com.javaproject.javatask.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.javaproject.javatask.repository.BookRepository;
import com.javaproject.javatask.repository.GoogleAPIBookRepository;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class JavaTaskController {

    @RequestMapping("/first")
    public String firstPage(@RequestParam(value = "stringparam", required = false) String stringparam) {
        if(stringparam == null) {
            stringparam = "no param passed";
        }
        return "First page, " + stringparam + ".";
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private class ResourceNotFoundException extends RuntimeException {}

    @RequestMapping(value = "/bookdetails/{ISBN}", method = RequestMethod.GET, produces = "application/json")
    public String bookDetailsByISBN(@PathVariable String ISBN) {//@RequestParam(value = "ISBN") String ISBN) {
        JsonObject foundBook = BookRepository.getBookByISBN(ISBN);

        if(foundBook == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundBook.toString();
        }
    }

    @RequestMapping(value = "/bookdetails/googleapi/{ISBN}", method = RequestMethod.GET, produces = "application/json")
    public String bookDetailsByISBNFromGoogleAPI(@PathVariable String ISBN) {//@RequestParam(value = "ISBN") String ISBN) {
        JsonObject foundBook = GoogleAPIBookRepository.getBookByISBN(ISBN);

        if(foundBook == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundBook.toString();
        }
    }

    @RequestMapping(value = "/bookscategory/{category}", method = RequestMethod.GET, produces = "application/json")
    public String findBooksDetailsByCategory(@PathVariable String category) {//@RequestParam(value = "ISBN") String ISBN) {
        JsonArray foundBooks = BookRepository.getBooksByCategory(category);
        if(foundBooks == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundBooks.toString();
        }
    }

    @RequestMapping(value = "/bookscategory/googleapi/{category}", method = RequestMethod.GET, produces = "application/json")
    public String findBooksDetailsByCategoryFromGoogleAPI(@PathVariable String category) {//@RequestParam(value = "ISBN") String ISBN) {
        JsonArray foundBooks = GoogleAPIBookRepository.getBooksByCategory(category);
        if(foundBooks == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundBooks.toString();
        }
    }

    @RequestMapping(value = "/rating", method = RequestMethod.GET, produces = "application/json")
    public String findAuthors() {//@RequestParam(value = "ISBN") String ISBN) {
        //JsonArray foundAuthors = BookRepository.getAllAuthors();
        JSONArray ratings = BookRepository.getAuthorsRatings();
        if(ratings == null) {
            throw new ResourceNotFoundException();
        } else {
            return ratings.toString();
        }
    }

    @RequestMapping(value = "/rating/googleapi", method = RequestMethod.GET, produces = "application/json")
    public String findAuthorsFromGoogleAPI() {//@RequestParam(value = "ISBN") String ISBN) {
        JsonArray foundAuthors = GoogleAPIBookRepository.getAllAuthors();
        if(foundAuthors == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundAuthors.toString();
        }
    }
}
