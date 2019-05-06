package com.javaproject.javatask.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

abstract class Repository {

    Repository() {
    }

    static final String ITEMS_KEY = "items";
    static final String AUTHORS_KEY = "authors";
    static final String VOLUME_INFO_KEY = "volumeInfo";
    static final String AVERAGE_RATING_KEY = "averageRating";

    /**
     * @return JSONObject from file
     */
    static JSONObject readJSONFromFile() {
        JSONObject jsonObjectFromFile = null;
        String jsonString;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("books.json"));
            jsonString = readBufferedReaderToString(bufferedReader);
            jsonObjectFromFile = new JSONObject(jsonString);
        } catch (Exception e) {
            LoggerFactory.getLogger(Repository.class).error("readJSONFromFile error", e);
        }
        return jsonObjectFromFile;
    }

    /**
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
     * @param authorsWithSumOfAverageRatingsMap Map with authors and their sum of average ratings and sum of their books which were rated
     * @param booksJSONArray                    JSONArray with all available books and their information
     */
    static void processAllBooksInArrayToMap(Map<String, List<Double>> authorsWithSumOfAverageRatingsMap, JSONArray booksJSONArray) {
        for (int i = 0; i < booksJSONArray.length(); i++) { //For every book
            JSONObject currentBook = booksJSONArray.getJSONObject(i);
            JSONObject currentBookVolumeInfo = currentBook.getJSONObject(VOLUME_INFO_KEY);

            if (currentBookVolumeInfo.has(AUTHORS_KEY)) { //If book has authors
                JSONArray currentBookAuthors = currentBookVolumeInfo.getJSONArray(AUTHORS_KEY);

                for (int m = 0; m < currentBookAuthors.length(); m++) { //For every book author
                    String currentAuthor = currentBookAuthors.getString(m);
                    processCurrentBookAuthor(authorsWithSumOfAverageRatingsMap, currentAuthor, currentBookVolumeInfo);
                }
            }
        }
    }

    /**
     * @param authorsWithSumOfAverageRatingsMap Map with authors and their sum of average ratings and sum of their books which were rated
     * @param currentAuthor                     Current book author that is currently processed
     * @param currentBookVolumeInfo             Currently processed book information
     */
    private static void processCurrentBookAuthor(Map<String, List<Double>> authorsWithSumOfAverageRatingsMap, String currentAuthor, JSONObject currentBookVolumeInfo) {
        if (authorsWithSumOfAverageRatingsMap.containsKey(currentAuthor)) { //If author already added in map
            updateAuthorAverageRatingsSum(authorsWithSumOfAverageRatingsMap, currentAuthor, currentBookVolumeInfo);
        } else { //Add author to map if already not exists
            addNewAuthorToMap(authorsWithSumOfAverageRatingsMap, currentAuthor, currentBookVolumeInfo);
        }
    }

    /**
     * @param authorsWithSumOfAverageRatingsMap Map with authors and their sum of average ratings and sum of their books which were rated
     * @param currentAuthor                     Current book author that is currently processed
     * @param currentBookVolumeInfo             Currently processed book information
     */
    private static void updateAuthorAverageRatingsSum(Map<String, List<Double>> authorsWithSumOfAverageRatingsMap, String currentAuthor, JSONObject currentBookVolumeInfo) {
        if (currentBookVolumeInfo.has(AVERAGE_RATING_KEY)) {
            List<Double> currentValuesList = authorsWithSumOfAverageRatingsMap.get(currentAuthor);
            double currentBookAverageRating = currentBookVolumeInfo.getDouble(AVERAGE_RATING_KEY);
            updateCurrentValuesListValues(currentValuesList, currentBookAverageRating);
        }
    }

    /**
     * @param currentValuesList        Currently processed author Values List that has fields with current
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
     * @param authorsWithSumOfAverageRatingsMap Map with authors and their sum of average ratings and sum of their books which were rated
     * @param currentAuthor                     Current book author that is currently processed
     * @param currentBookVolumeInfo             Currently processed book information
     */
    private static void addNewAuthorToMap(Map<String, List<Double>> authorsWithSumOfAverageRatingsMap, String currentAuthor, JSONObject currentBookVolumeInfo) {
        double currentSumOfAuthorBooksAverageRating = 0.0;
        double currentSumOfAuthorRatedBooks = 0.0;

        if (currentBookVolumeInfo.has(AVERAGE_RATING_KEY)) {
            currentSumOfAuthorBooksAverageRating = currentBookVolumeInfo.getDouble(AVERAGE_RATING_KEY);
            currentSumOfAuthorRatedBooks = 1.0;
        }
        List<Double> tempList = new ArrayList<>(Arrays.asList(currentSumOfAuthorBooksAverageRating, currentSumOfAuthorRatedBooks));
        authorsWithSumOfAverageRatingsMap.put(currentAuthor, tempList);
    }

    /**
     * @return JSONArray with authors and their books average rating
     */
    static JSONArray findAuthorsRatings(Map<String, List<Double>> authorsWithRatingsMap) {
        JSONArray authorsWithAverageRatingsArray = new JSONArray();

        if (authorsWithRatingsMap != null) {
            for (Map.Entry<String, List<Double>> entry : authorsWithRatingsMap.entrySet()) {
                String authorName = entry.getKey();
                double sumOfRatings = entry.getValue().get(0);
                double sumOfRatedBooks = entry.getValue().get(1);
                double averageRating = 0;
                if (sumOfRatedBooks != 0) {
                    averageRating = sumOfRatings / sumOfRatedBooks;
                }

                JSONObject newObject = new JSONObject();
                newObject.put("author", authorName);
                newObject.put(AVERAGE_RATING_KEY, averageRating);
                authorsWithAverageRatingsArray.put(newObject);
            }
        }
        return authorsWithAverageRatingsArray;
    }

    /**
     * @param resultSet HashSet with Strings
     * @return JSONArray with HashSet content
     */
    static JSONArray convertSetToJSONArray(HashSet<String> resultSet) {
        JSONArray resultArray = new JSONArray();
        for (String item : resultSet) {
            resultArray.put(item);
        }
        return resultArray;
    }

    /**
     * @param array   JSONArray with elements
     * @param element String element to check if array contains it
     * @return True if array contains element, otherwise false
     */
    static boolean containsString(JSONArray array, String element) {
        for (int i = 0; i < array.length(); i++) {
            String currentElement = array.getString(i);
            if (currentElement.equals(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param url URL from where we want to read response
     * @return URL response as String
     */
    static String getResponseStringFromURL(String url) {
        try (InputStream inputStream = new URL(url).openStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            return readBufferedReaderToString(bufferedReader);
        } catch (Exception e) {
            LoggerFactory.getLogger(Repository.class).error("getResponseStringFromURL error", e);
        }
        return null;
    }

    static HashSet<String> findAuthors(JSONArray booksJSONArray) {
        HashSet<String> authorsSet = new HashSet<>();
        for (int i = 0; i < booksJSONArray.length(); i++) {
            JSONObject currentBook = booksJSONArray.getJSONObject(i);
            JSONObject currentBookVolumeInfo = currentBook.getJSONObject(VOLUME_INFO_KEY);
            if (currentBookVolumeInfo.has(AUTHORS_KEY)) {
                JSONArray currentBookAuthors = currentBookVolumeInfo.getJSONArray(AUTHORS_KEY);

                for (int m = 0; m < currentBookAuthors.length(); m++) {
                    String currentAuthor = currentBookAuthors.getString(m);
                    authorsSet.add(currentAuthor);
                }
            }
        }
        return authorsSet;
    }
}
