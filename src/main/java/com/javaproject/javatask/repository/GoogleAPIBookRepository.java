package com.javaproject.javatask.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

public class GoogleAPIBookRepository {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAPIBookRepository.class);

    public static JSONObject getBookByISBN(String requestedISBN) {
        logger.info("Google API BookRepository queried with query: " + requestedISBN);
        return findBookByISBN(requestedISBN, getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=" + requestedISBN));
    }

    public static JSONObject findBookByISBN(String requestedISBN, String jsonText) {
        if (jsonText != null) {
            return new JSONObject(jsonText);
        } else {
            return null;
        }
    }

    public static JSONArray getBooksByCategory(String requestedCategory) {
        logger.info("Google API BookRepository queried with Category: " + requestedCategory);
        JSONObject responseObject;
        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=subject:" + requestedCategory);
        if (jsonText != null) {
            responseObject = new JSONObject(jsonText);
            if (responseObject.has("items")) {
                return responseObject.getJSONArray("items"); //Array of found books
            }
        }
        return new JSONArray(); //Return empty list
    }

    private static String getResponseStringFromURL(String url) {
        try {
            InputStream inputStream = new URL(url).openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            return readBufferedReaderToString(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readBufferedReaderToString(BufferedReader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString();
    }

    public static JSONArray getAuthorsRatings() {
        JSONArray authorsWithAverageRatingsArray = new JSONArray();

        Map<String, List<Double>> authorsWithRatingsMap = createAuthorsWithSumOfAverageRatingsMap();
        if (authorsWithRatingsMap != null) {
            for (Map.Entry<String, List<Double>> entry : authorsWithRatingsMap.entrySet()) {
                String authorName = entry.getKey();
                double sumOfRatings = entry.getValue().get(0);
                double sumOfRatedBooks = entry.getValue().get(1);
                double averageRating = sumOfRatings / sumOfRatedBooks;

                JSONObject newObject = new JSONObject();
                newObject.put("author", authorName);
                newObject.put("averageRating", averageRating);
                authorsWithAverageRatingsArray.put(newObject);
            }
        }
        return authorsWithAverageRatingsArray;
    }

    private static Map<String, List<Double>> createAuthorsWithSumOfAverageRatingsMap() {
        JSONObject responseObject;
        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=*");
        if (jsonText != null) {
            responseObject = new JSONObject(jsonText);
            if (responseObject.has("items")) {
                JSONArray booksJSONArray = responseObject.getJSONArray("items"); //Array of found books

                Map<String, List<Double>> authorsWithSumOfAverageRatingsMap = new HashMap<>();

                for (int i = 0; i < booksJSONArray.length(); i++) {
                    JSONObject currentBook = booksJSONArray.getJSONObject(i);
                    JSONObject currentBookVolumeInfo = currentBook.getJSONObject("volumeInfo");

                    if (currentBookVolumeInfo.has("authors")) {
                        JSONArray currentBookAuthors = currentBookVolumeInfo.getJSONArray("authors");

                        for (int m = 0; m < currentBookAuthors.length(); m++) {
                            String currentAuthor = currentBookAuthors.getString(m);
                            double currentSumOfAuthorAverageRating = 0.0;
                            double currentSumOfAuthorRatedBooks = 0.0;

                            if (authorsWithSumOfAverageRatingsMap.containsKey(currentAuthor)) {
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
                            } else {
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
                return authorsWithSumOfAverageRatingsMap;
            }
        }
        return null;
    }

    public static JSONArray getAllAuthors() {
        logger.info("BookRepository queried to find all authors.");

        JSONObject responseObject;
        HashSet<String> authorsSet = new HashSet<>();
        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=*");
        if (jsonText != null) {
            responseObject = new JSONObject(jsonText);
            if (responseObject.has("items")) {
                JSONArray booksJSONArray = responseObject.getJSONArray("items"); //Array of found books

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
        }
        return convertSetToJSONArray(authorsSet);
    }

    private static JSONArray convertSetToJSONArray(HashSet<String> resultSet) {
        JSONArray resultArray = new JSONArray();
        for (String item : resultSet) {
            resultArray.put(item);
        }
        return resultArray;
    }
}
