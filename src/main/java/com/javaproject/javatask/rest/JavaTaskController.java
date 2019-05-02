package com.javaproject.javatask.rest;

import com.google.gson.JsonObject;
import com.javaproject.javatask.repository.BookRepository;
import com.javaproject.javatask.repository.GoogleAPIBookRepository;
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
    public String bookDetails(@PathVariable String ISBN) {//@RequestParam(value = "ISBN") String ISBN) {

        //JsonObject foundBook = BookRepository.getBookByISBN(ISBN);
        JsonObject foundBook = GoogleAPIBookRepository.getBookByISBN(ISBN);

        if(foundBook == null) {
            throw new ResourceNotFoundException();
        } else {
            return foundBook.toString();
        }
    }
}
