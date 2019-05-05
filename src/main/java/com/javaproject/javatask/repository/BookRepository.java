package com.javaproject.javatask.repository;

import com.javaproject.javatask.rest.JavaTaskController;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BookRepository {

    private BookRepository() {}

    private static final Logger logger = LoggerFactory.getLogger(JavaTaskController.class);

    private static JSONObject booksJSONObject = readJSONFromFile();
    private static JSONArray booksJSONArray = booksJSONObject.getJSONArray("items");

    private static final String KEY_AUTHORS = "authors";
    private static final String KEY_VOLUME_INFO = "volumeInfo";
    private static final String KEY_AVERAGE_RATING = "averageRating";

    public static JSONArray getBooksJSONArray() {
        return booksJSONArray;
    }

    public static void setBooksJSONArray(JSONArray newBooksJSONArray) {
        logger.info("Setting JSONArray");
        booksJSONArray = newBooksJSONArray;
    }

    /**
     *
     * @return JSONObject from file
     */
    private static JSONObject readJSONFromFile() {
        if (logger != null) {
            logger.info("Reading JSON from file");
        }
        JSONObject jsonObjectFromFile = null;
        String jsonString;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("books.json"));
            jsonString = readBufferedReaderToString(bufferedReader);
            jsonObjectFromFile = new JSONObject(jsonString);
        } catch (Exception e) {
            if (logger != null) {
                logger.error("readJSONFromFile error", e);
            }
        }
        return jsonObjectFromFile;
    }

    /**
     *
     * @param reader BufferedReader whose content will be converted to String
     * @return BufferedReader content as String
     * @throws IOException When readLine function fails
     */
    private static String readBufferedReaderToString(BufferedReader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString();
    }

    /**
     *
     * @param requestedISBN Parameter needed to search for a book with a given number
     * @return Book information as JSONObject if found, return null if book not found
     */
    public static JSONObject getBookByISBN(String requestedISBN) {
        logger.info("BookRepository queried with ISBN: {}", requestedISBN);

        if (booksJSONArray != null) {
            for (int i = 0; i < booksJSONArray.length(); i++) {
                JSONObject currentBook = booksJSONArray.getJSONObject(i);
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject(KEY_VOLUME_INFO);
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
     *
     * @param requestedCategory Category from which we want to find books
     * @return JSONArray of found books
     */
    public static JSONArray getBooksByCategory(String requestedCategory) {
        logger.info("BookRepository queried with Category: {}", requestedCategory);
        JSONArray resultArray = new JSONArray();
        if (booksJSONArray != null) {
            for (int i = 0; i < booksJSONArray.length(); i++) {
                JSONObject currentBook = booksJSONArray.getJSONObject(i);
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject(KEY_VOLUME_INFO);
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
     *
     * @param array JSONArray with elements
     * @param element String element to check if array contains it
     * @return True if array contains element, otherwise false
     */
    private static boolean containsString(JSONArray array, String element) {
        for (int i = 0; i < array.length(); i++) {
            String currentElement = array.getString(i);
            if (currentElement.equals(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return JSONArray with authors and their books average rating
     */
    public static JSONArray getAuthorsRatings() {
        JSONArray authorsWithAverageRatingsArray = new JSONArray();

        Map<String, List<Double>> authorsWithRatingsMap = createAuthorsWithSumOfAverageRatingsMap();
        for (Map.Entry<String, List<Double>> entry : authorsWithRatingsMap.entrySet()) { //Create result JSONArray from Map
            String authorName = entry.getKey();
            double sumOfRatings = entry.getValue().get(0);
            double sumOfRatedBooks = entry.getValue().get(1);
            double averageRating = sumOfRatings / sumOfRatedBooks;

            JSONObject newObject = new JSONObject();
            newObject.put(KEY_AUTHORS, authorName);
            newObject.put(KEY_AVERAGE_RATING, averageRating);
            authorsWithAverageRatingsArray.put(newObject);
        }
        return authorsWithAverageRatingsArray;
    }

    /**
     *
     * @return Map with authors and their sum of average ratings and sum of their books which were rated
     */
    private static Map<String, List<Double>> createAuthorsWithSumOfAverageRatingsMap() {
        Map<String, List<Double>> authorsWithSumOfAverageRatingsMap = new HashMap<>();

        if (booksJSONArray != null) {
            for (int i = 0; i < booksJSONArray.length(); i++) { //For every book
                JSONObject currentBook = booksJSONArray.getJSONObject(i);
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject(KEY_VOLUME_INFO);

                if (currentBookVolumeInfo.has(KEY_AUTHORS)) { //If book has authors
                    JSONArray currentBookAuthors = currentBookVolumeInfo.getJSONArray(KEY_AUTHORS);

                    for (int m = 0; m < currentBookAuthors.length(); m++) { //For every book author
                        String currentAuthor = currentBookAuthors.getString(m);
                        processCurrentBookAuthor(authorsWithSumOfAverageRatingsMap, currentAuthor, currentBookVolumeInfo);
                    }
                }
            }
        }
        return authorsWithSumOfAverageRatingsMap;
    }

    /**
     *
     * @param authorsWithSumOfAverageRatingsMap Map with authors and their sum of average ratings and sum of their books which were rated
     * @param currentAuthor Current book author that is currently processed
     * @param currentBookVolumeInfo Currently processed book information
     */
    private static void processCurrentBookAuthor(Map<String, List<Double>> authorsWithSumOfAverageRatingsMap, String currentAuthor, JSONObject currentBookVolumeInfo) {
        if (authorsWithSumOfAverageRatingsMap.containsKey(currentAuthor)) { //If author already added in map
            updateAuthorAverageRatingsSum(authorsWithSumOfAverageRatingsMap, currentAuthor, currentBookVolumeInfo);
        } else { //Add author to map if already not exists
            addNewAuthorToMap(authorsWithSumOfAverageRatingsMap, currentAuthor, currentBookVolumeInfo);
        }
    }

    /**
     *
     * @param authorsWithSumOfAverageRatingsMap Map with authors and their sum of average ratings and sum of their books which were rated
     * @param currentAuthor Current book author that is currently processed
     * @param currentBookVolumeInfo Currently processed book information
     */
    private static void updateAuthorAverageRatingsSum(Map<String, List<Double>> authorsWithSumOfAverageRatingsMap, String currentAuthor, JSONObject currentBookVolumeInfo) {
        if (currentBookVolumeInfo.has(KEY_AVERAGE_RATING)) {
            List<Double> currentValuesList = authorsWithSumOfAverageRatingsMap.get(currentAuthor);
            double currentBookAverageRating = currentBookVolumeInfo.getDouble(KEY_AVERAGE_RATING);
            updateCurrentValuesListValues(currentValuesList, currentBookAverageRating);
        }
    }

    /**
     *
     * @param currentValuesList Currently processed author Values List that has fields with current
     * @param currentBookAverageRating Currently processed book average rating
     */
    private static void updateCurrentValuesListValues(List<Double> currentValuesList, double currentBookAverageRating) {
        double currentSumOfAuthorBooksAverageRating = currentValuesList.get(0);
        double currentSumOfAuthorRatedBooks = currentValuesList.get(1);

        double newSumOfAuthorAverageRating = currentSumOfAuthorBooksAverageRating + currentBookAverageRating;
        double newSumOfAuthorRatedBooks = currentSumOfAuthorRatedBooks + 1;

        currentValuesList.set(0, newSumOfAuthorAverageRating);
        currentValuesList.set(1, newSumOfAuthorRatedBooks);
    }

    /**
     *
     * @param authorsWithSumOfAverageRatingsMap Map with authors and their sum of average ratings and sum of their books which were rated
     * @param currentAuthor Current book author that is currently processed
     * @param currentBookVolumeInfo Currently processed book information
     */
    private static void addNewAuthorToMap(Map<String, List<Double>> authorsWithSumOfAverageRatingsMap, String currentAuthor, JSONObject currentBookVolumeInfo) {
        double currentSumOfAuthorBooksAverageRating = 0.0;
        double currentSumOfAuthorRatedBooks = 0.0;

        if (currentBookVolumeInfo.has(KEY_AVERAGE_RATING)) {
            currentSumOfAuthorBooksAverageRating = currentBookVolumeInfo.getDouble(KEY_AVERAGE_RATING);
            currentSumOfAuthorRatedBooks = 1.0;
        }
        List<Double> tempList = new ArrayList<>(Arrays.asList(currentSumOfAuthorBooksAverageRating, currentSumOfAuthorRatedBooks));
        authorsWithSumOfAverageRatingsMap.put(currentAuthor, tempList);
    }

    /**
     *
     * @return JSONArray with all found authors
     */
    public static JSONArray getAllAuthors() {
        logger.info("BookRepository queried to find all authors.");
        HashSet<String> authorsSet = new HashSet<>();

        if (booksJSONArray != null) {
            for (int i = 0; i < booksJSONArray.length(); i++) {
                JSONObject currentBook = booksJSONArray.getJSONObject(i);
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject(KEY_VOLUME_INFO);
                if (currentBookVolumeInfo.has(KEY_AUTHORS)) {
                    JSONArray currentBookAuthors = currentBookVolumeInfo.getJSONArray(KEY_AUTHORS);

                    for (int m = 0; m < currentBookAuthors.length(); m++) {
                        String currentAuthor = currentBookAuthors.getString(m);
                        authorsSet.add(currentAuthor);
                    }
                }
            }
        }
        return convertSetToJSONArray(authorsSet);
    }

    /**
     *
     * @param resultSet HashSet with Strings
     * @return JSONArray with HashSet content
     */
    private static JSONArray convertSetToJSONArray(HashSet<String> resultSet) {
        JSONArray resultArray = new JSONArray();
        for (String item : resultSet) {
            resultArray.put(item);
        }
        return resultArray;
    }
}
