package com.javaproject.javatask.repository;

import com.javaproject.javatask.book.BookCreator;
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

    static JSONArray getBooksJSONArray() {
        return booksJSONArray;
    }

    static void setBooksJSONArray(JSONArray newBooksJSONArray) {
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
            JSONObject searchedBook = searchBookByISBN(requestedISBN);

            if (searchedBook == null) {
                searchedBook = searchBookByID(requestedISBN);
            }
            return searchedBook;
        }
        return null;
    }

    private static JSONObject searchBookByISBN(String requestedISBN) {
        for (int i = 0; i < booksJSONArray.length(); i++) {
            JSONObject currentBook = booksJSONArray.getJSONObject(i);
            JSONObject currentBookVolumeInfo = currentBook.getJSONObject(VOLUME_INFO_KEY);
            JSONArray currentBookIdentifiers = currentBookVolumeInfo.getJSONArray("industryIdentifiers");

            for (int m = 0; m < currentBookIdentifiers.length(); m++) {
                JSONObject industryIdentifier = currentBookIdentifiers.getJSONObject(m);
                String isbn = industryIdentifier.getString("identifier");
                if (isbn.equals(requestedISBN)) {
                    return BookCreator.createBookRecord(currentBook);
                }
            }
        }
        return null;
    }

    private static JSONObject searchBookByID(String requestedISBN) {
        for (int i = 0; i < booksJSONArray.length(); i++) {
            JSONObject currentBook = booksJSONArray.getJSONObject(i);
            String bookId = currentBook.getString("id");
            if (bookId.equals(requestedISBN)) {
                return BookCreator.createBookRecord(currentBook);
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
                        JSONObject bookRecord = BookCreator.createBookRecord(currentBook);
                        resultArray.put(bookRecord);
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
        logger.info("BookRepository queried to find authors books average rating");
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
    static JSONArray getAllAuthors() {
        logger.info("BookRepository queried to find all authors.");
        HashSet<String> authorsSet = new HashSet<>();
        if (booksJSONArray != null) {
            authorsSet = findAuthors(booksJSONArray);
        }
        return convertSetToJSONArray(authorsSet);
    }
}
