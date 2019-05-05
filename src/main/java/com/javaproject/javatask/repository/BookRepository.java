package com.javaproject.javatask.repository;

import com.javaproject.javatask.rest.JavaTaskController;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class BookRepository extends Repository {

    private static final Logger logger = LoggerFactory.getLogger(JavaTaskController.class);

    private static JSONObject booksJSONObject = readJSONFromFile();
    private static JSONArray booksJSONArray = booksJSONObject.getJSONArray("items");

    public static JSONArray getBooksJSONArray() {
        return booksJSONArray;
    }

    public static void setBooksJSONArray(JSONArray newBooksJSONArray) {
        logger.info("Setting JSONArray");
        booksJSONArray = newBooksJSONArray;
    }

    /**
     * @param requestedISBN Parameter needed to search for a book with a given number
     * @return Book information as JSONObject if found, return null if book not found
     */
    public static JSONObject getBookByISBN(String requestedISBN) {
        logger.info("BookRepository queried with ISBN: {}", requestedISBN);

        if (booksJSONArray != null) {
            for (int i = 0; i < booksJSONArray.length(); i++) {
                JSONObject currentBook = booksJSONArray.getJSONObject(i);
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject(VOLUME_INFO_KEY);
                JSONArray currentBookIdentifiers = currentBookVolumeInfo.getJSONArray("industryIdentifiers");

                for (int m = 0; m < currentBookIdentifiers.length(); m++) {
                    JSONObject industryIdentifier = currentBookIdentifiers.getJSONObject(m);
                    String isbn = industryIdentifier.getString("identifier");
                    if (isbn.equals(requestedISBN)) {
                        return currentBook;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param requestedCategory Category from which we want to find books
     * @return JSONArray of found books
     */
    public static JSONArray getBooksByCategory(String requestedCategory) {
        logger.info("BookRepository queried with Category: {}", requestedCategory);
        JSONArray resultArray = new JSONArray();
        if (booksJSONArray != null) {
            for (int i = 0; i < booksJSONArray.length(); i++) {
                JSONObject currentBook = booksJSONArray.getJSONObject(i);
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject(VOLUME_INFO_KEY);
                JSONArray currentBookCategories;

                if (currentBookVolumeInfo.has("categories")) {
                    currentBookCategories = currentBookVolumeInfo.getJSONArray("categories");
                    if (containsString(currentBookCategories, requestedCategory)) {
                        resultArray.put(currentBook);
                    }
                }
            }
        }
        return resultArray;
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

        if (booksJSONArray != null) {
            processAllBooksInArrayToMap(authorsWithSumOfAverageRatingsMap,
                    booksJSONArray);
        }
        return authorsWithSumOfAverageRatingsMap;
    }

    /**
     * @return JSONArray with all found authors
     */
    public static JSONArray getAllAuthors() {
        logger.info("BookRepository queried to find all authors.");
        HashSet<String> authorsSet = new HashSet<>();

        if (booksJSONArray != null) {
            authorsSet = findAuthors(booksJSONArray);
        }
        return convertSetToJSONArray(authorsSet);
    }
}
