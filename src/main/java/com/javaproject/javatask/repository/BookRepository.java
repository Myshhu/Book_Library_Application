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

    private static final Logger logger = LoggerFactory.getLogger(JavaTaskController.class);

    private static JSONObject booksJSONObject = readJSONFromFile();
    private static JSONArray booksJSONArray = booksJSONObject.getJSONArray("items");

    public static JSONArray getBooksJSONArray() {
        return booksJSONArray;
    }

    public static void setBooksJSONArray(JSONArray booksJSONArray) {
        logger.info("Setting JSONArray");
        BookRepository.booksJSONArray = booksJSONArray;
    }

    /**
     *
     * @return JSONObject from file
     */
    private static JSONObject readJSONFromFile() {
        logger.info("Reading JSON from file");
        JSONObject JSONObjectFromFile = null;
        String jsonString;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("books.json"));
            //JSONObjectFromFile = gson.fromJson(bufferedReader, JsonObject.class);
            jsonString = readBufferedReaderToString(bufferedReader);
            JSONObjectFromFile = new JSONObject(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObjectFromFile;
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
        logger.info("BookRepository queried with ISBN: " + requestedISBN);

        if (booksJSONArray != null) {
            for (int i = 0; i < booksJSONArray.length(); i++) {
                JSONObject currentBook = booksJSONArray.getJSONObject(i);
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject("volumeInfo");
                JSONArray currentBookIdentifiers = currentBookVolumeInfo.getJSONArray("industryIdentifiers");

                for (int m = 0; m < currentBookIdentifiers.length(); m++) {
                    JSONObject industryIdentifier = currentBookIdentifiers.getJSONObject(m);
                    String ISBN = industryIdentifier.getString("identifier");
                    if (ISBN.equals(requestedISBN)) {
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
        logger.info("BookRepository queried with Category: " + requestedCategory);
        JSONArray resultArray = new JSONArray();
        if (booksJSONArray != null) {
            for (int i = 0; i < booksJSONArray.length(); i++) {
                JSONObject currentBook = booksJSONArray.getJSONObject(i);
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject("volumeInfo");
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
            newObject.put("author", authorName);
            newObject.put("averageRating", averageRating);
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
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject("volumeInfo");

                if (currentBookVolumeInfo.has("authors")) { //If book has authors
                    JSONArray currentBookAuthors = currentBookVolumeInfo.getJSONArray("authors");

                    for (int m = 0; m < currentBookAuthors.length(); m++) { //For every book author
                        String currentAuthor = currentBookAuthors.getString(m);
                        double currentSumOfAuthorAverageRating = 0.0;
                        double currentSumOfAuthorRatedBooks = 0.0;

                        if (authorsWithSumOfAverageRatingsMap.containsKey(currentAuthor)) { //If author already added in map
                            if (currentBookVolumeInfo.has("averageRating")) {
                                List<Double> currentValuesList = authorsWithSumOfAverageRatingsMap.get(currentAuthor);
                                double currentBookAverageRating = currentBookVolumeInfo.getDouble("averageRating");
                                currentSumOfAuthorAverageRating = currentValuesList.get(0);
                                currentSumOfAuthorRatedBooks = currentValuesList.get(1);

                                double newSumOfAuthorAverageRating = currentSumOfAuthorAverageRating + currentBookAverageRating;
                                double newSumOfAuthorRatedBooks = currentSumOfAuthorRatedBooks + 1;

                                currentValuesList.set(0, newSumOfAuthorAverageRating);
                                currentValuesList.set(1, newSumOfAuthorRatedBooks);
                            }
                        } else { //Add author to map if already not exists
                            if (currentBookVolumeInfo.has("averageRating")) {
                                currentSumOfAuthorAverageRating = currentBookVolumeInfo.getDouble("averageRating");
                                currentSumOfAuthorRatedBooks = 1.0;
                            }
                            List<Double> tempList = new ArrayList<>(Arrays.asList(currentSumOfAuthorAverageRating, currentSumOfAuthorRatedBooks));
                            authorsWithSumOfAverageRatingsMap.put(currentAuthor, tempList);
                        }
                    }
                }
            }
        }
        return authorsWithSumOfAverageRatingsMap;
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
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject("volumeInfo");
                if (currentBookVolumeInfo.has("authors")) {
                    JSONArray currentBookAuthors = currentBookVolumeInfo.getJSONArray("authors");

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
