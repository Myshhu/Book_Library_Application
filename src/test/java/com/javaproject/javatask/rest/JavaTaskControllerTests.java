package com.javaproject.javatask.rest;

import org.junit.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class JavaTaskControllerTests {

    @Test
    public void bookDetailsByISBN() {
        when().
                get("/bookdetails/{isbn}", "").
                then().
                statusCode(404);

        when().
                get("/bookdetails/{isbn}", "9781575211534"). //ISBN of book from testingArray
                then().
                statusCode(200).
                body("title", equalTo("Java unleashed"));
    }

    @Test
    public void bookDetailsByISBNFromGoogleAPI() {
        when().
                get("/bookdetails/googleapi/{isbn}", "").
                then().
                statusCode(404);

        when().
                get("/bookdetails/googleapi/{isbn}", "1234"). //Random query
                then().
                statusCode(200);

        when().
                get("/bookdetails/googleapi/{isbn}", "9780596001438").
                then().
                statusCode(200).body("items[0].volumeInfo.title", equalTo("Java and XSLT"));
    }

    @Test
    public void findBooksDetailsByCategory() {
        when().
                get("/bookscategory/{category}", "").
                then().
                statusCode(200).body("$", hasSize(0));

        when().
                get("/bookscategory/{category}", "Computers").
                then().
                statusCode(200).body("$", hasSize(22)); // "books.json" file has 22 books in Computers category
    }

    @Test
    public void findBooksDetailsByCategoryFromGoogleAPI() {
        when().
                get("/bookscategory/googleapi/{category}", "").
                then().
                statusCode(200).body("$", hasSize(0));

        when().
                get("/bookscategory/googleapi/{category}", "Computers").
                then().
                statusCode(200).body("$.size()", greaterThan(0));
    }

    @Test
    public void findAuthorsRatings() {
        when().
                get("/rating").
                then().
                statusCode(200);

        when().
                get("/rating").
                then().
                statusCode(200).body("$", hasSize(43)); // "books.json" file has 43 authors

        when().
                get("/rating").
                then().
                statusCode(200).body("[0]", hasKey("author"),
                "[0]", hasKey("averageRating"),
                "[0].size()", equalTo(2));
    }

    @Test
    public void findAuthorsRatingsFromGoogleAPI() {
        when().
                get("/rating/googleapi").
                then().
                statusCode(200);

        when().
                get("/rating/googleapi").
                then().
                statusCode(200).body("$", hasSize(10)); // "By default it should return 10 items

        when().
                get("/rating/googleapi").
                then().
                statusCode(200).body("[0]", hasKey("author"),
                "[0]", hasKey("averageRating"),
                "[0].size()", equalTo(2));
    }
}