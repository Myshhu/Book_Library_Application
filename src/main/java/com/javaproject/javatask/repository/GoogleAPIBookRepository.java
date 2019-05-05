package com.javaproject.javatask.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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
        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=subject:" + requestedCategory);
        JSONObject responseObject;
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

    public static JSONArray getAuthorsRatings() {
        Map<String, List<Double>> authorsWithRatingsMap = createAuthorsWithSumOfAverageRatingsMap();

        JSONArray authorsWithAverageRatingsArray = new JSONArray();
        if (authorsWithRatingsMap != null) {
            for (Map.Entry<String, List<Double>> entry : authorsWithRatingsMap.entrySet()) {
                JSONObject newObject = new JSONObject();
                String authorName = entry.getKey();
                double sumOfRatings = entry.getValue().get(0);
                double sumOfRatedBooks = entry.getValue().get(1);
                double averageRating = sumOfRatings / sumOfRatedBooks;
                newObject.put("author", authorName);
                newObject.put("averageRating", averageRating);
                authorsWithAverageRatingsArray.put(newObject);
            }
        }
        return authorsWithAverageRatingsArray;
    }

    private static Map<String, List<Double>> createAuthorsWithSumOfAverageRatingsMap() {
        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=*");
        JSONObject responseObject;
        if (jsonText != null) {
            responseObject = new JSONObject(jsonText);
            if (responseObject.has("items")) {
                JSONArray booksJSONArray = responseObject.getJSONArray("items"); //Array of found books

                Map<String, List<Double>> authorsWithRatingsList = new HashMap<>();

                for (int i = 0; i < booksJSONArray.length(); i++) {
                    JSONObject currentBook = booksJSONArray.getJSONObject(i);
                    JSONObject currentBookVolumeInfo = currentBook.getJSONObject("volumeInfo");
                    if (currentBookVolumeInfo.has("authors")) {
                        JSONArray currentBookAuthors = currentBookVolumeInfo.getJSONArray("authors");

                        for (int m = 0; m < currentBookAuthors.length(); m++) {
                            String currentAuthor = currentBookAuthors.getString(m);

                            if (authorsWithRatingsList.containsKey(currentAuthor)) {
                                if (currentBookVolumeInfo.has("averageRating")) {
                                    List<Double> currentValuesList = authorsWithRatingsList.get(currentAuthor);
                                    double averageRating = currentBookVolumeInfo.getDouble("averageRating");
                                    double currentSumOfAverageRating = currentValuesList.get(0);
                                    double currentSumOfRatedBooks = currentValuesList.get(1);

                                    double newSumOfAverageRating = currentSumOfAverageRating + averageRating;
                                    double newSumOfRatedBooks = currentSumOfRatedBooks + 1;

                                    currentValuesList.set(0, newSumOfAverageRating);
                                    currentValuesList.set(1, newSumOfRatedBooks);
                                }
                            } else {
                                double currentSumOfAverageRating = 0.0;
                                double currentSumOfRatedBooks = 0.0;
                                if (currentBookVolumeInfo.has("averageRating")) {
                                    currentSumOfAverageRating = currentBookVolumeInfo.getDouble("averageRating");
                                    currentSumOfRatedBooks = 1.0;
                                }
                                List<Double> tempList = new ArrayList<>(Arrays.asList(currentSumOfAverageRating, currentSumOfRatedBooks));
                                authorsWithRatingsList.put(currentAuthor, tempList);
                            }
                        }
                    }
                }
                return authorsWithRatingsList;
            }
        }
        return null;
    }

    public static JSONArray getAllAuthors() {
        logger.info("BookRepository queried to find all authors.");

        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=*");
        JSONObject responseObject;
        HashSet<String> authorsSet = new HashSet<>();
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

    private static String readBufferedReaderToString(BufferedReader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString();
    }
}
