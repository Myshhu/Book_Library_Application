package com.javaproject.javatask.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GoogleAPIBookRepository extends Repository {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAPIBookRepository.class);

    /**
     * @param requestedISBN Parameter needed to search for a book with a given number
     * @return Book information as JSONObject if found, return null if book not found
     */
    public static JSONObject getBookByISBN(String requestedISBN) {
        logger.info("Google API BookRepository queried with query: {}", requestedISBN);
        return findBookByISBN(getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=" + requestedISBN));
    }

    /**
     * @param jsonText JSONObject as string with found book informations
     * @return Book information as JSONObject if jsonString not null, otherwise return null
     */
    public static JSONObject findBookByISBN(String jsonText) {
        if (jsonText != null) {
            return new JSONObject(jsonText);
        } else {
            return null;
        }
    }

    /**
     * @param requestedCategory Category from which we want to find books
     * @return JSONArray of found books
     */
    public static JSONArray getBooksByCategory(String requestedCategory) {
        logger.info("Google API BookRepository queried with Category: {}", requestedCategory);
        JSONObject responseObject;
        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=subject:" + requestedCategory);
        if (jsonText != null) {
            responseObject = new JSONObject(jsonText);
            if (responseObject.has(ITEMS_KEY)) {
                return responseObject.getJSONArray(ITEMS_KEY); //Array of found books
            }
        }
        return new JSONArray(); //Return empty list
    }

    /**
     * @return JSONArray with authors and their books average rating
     */
    public static JSONArray getAuthorsRatings() {
        return findAuthorsRatings(createAuthorsWithSumOfAverageRatingsMap());
    }

    /**
     * @return Map with authors and their sum of average ratings and sum of their books which were rated
     */
    private static Map<String, List<Double>> createAuthorsWithSumOfAverageRatingsMap() {
        Map<String, List<Double>> authorsWithSumOfAverageRatingsMap = new HashMap<>();

        JSONObject responseObject;
        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=*");
        if (jsonText != null) {
            responseObject = new JSONObject(jsonText);
            if (responseObject.has(ITEMS_KEY)) {
                JSONArray booksJSONArray = responseObject.getJSONArray(ITEMS_KEY); //Array of found books
                processAllBooksInArrayToMap(authorsWithSumOfAverageRatingsMap,
                        booksJSONArray);
            }
        }
        return authorsWithSumOfAverageRatingsMap;
    }

    /**
     * @return JSONArray with all found authors
     */
    public static JSONArray getAllAuthors() {
        logger.info("GoogleAPIBookRepository queried to find all authors.");

        HashSet<String> authorsSet = new HashSet<>();
        JSONObject responseObject;
        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=*");
        if (jsonText != null) {
            responseObject = new JSONObject(jsonText);
            if (responseObject.has(ITEMS_KEY)) {
                JSONArray booksJSONArray = responseObject.getJSONArray(ITEMS_KEY); //Array of found books
                authorsSet = findAuthors(booksJSONArray);

            }
        }
        return convertSetToJSONArray(authorsSet);
    }
}
