package com.javaproject.javatask.repository;

import com.google.gson.*;
import com.javaproject.javatask.rest.JavaTaskController;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class BookRepository {

    private static final Logger logger = LoggerFactory.getLogger(JavaTaskController.class);

    private static JsonObject booksJSONObject = readJSONFromFile();

    private static JsonObject readJSONFromFile() {
        JsonObject JsonObjectFromFile = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("books.json"));
            Gson gson = new Gson();
            JsonObjectFromFile = gson.fromJson(bufferedReader, JsonObject.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return JsonObjectFromFile;
    }

    public static JsonObject getBookByISBN(String requestedISBN) {
        logger.info("BookRepository queried with ISBN: " + requestedISBN);
        JsonArray booksJSONArray = booksJSONObject.getAsJsonArray("items");

        for (int i = 0; i < booksJSONArray.size(); i++) {
            JsonObject currentBook = (JsonObject) booksJSONArray.get(i);

            JsonObject currentBookVolumeInfo = (JsonObject) currentBook.get("volumeInfo");
            JsonArray currentBookIdentifiers = currentBookVolumeInfo.getAsJsonArray("industryIdentifiers");

            for (int m = 0; m < currentBookIdentifiers.size(); m++) {
                JsonObject industryIdentifier = (JsonObject) currentBookIdentifiers.get(m);
                String ISBN = industryIdentifier.get("identifier").getAsString();

                if (ISBN.equals(requestedISBN)) {
                    return currentBook;
                }
            }
        }
        return null;
    }

    public static JsonArray getBooksByCategory(String requestedCategory) {
        logger.info("BookRepository queried with Category: " + requestedCategory);
        JsonArray booksJSONArray = booksJSONObject.getAsJsonArray("items");
        JsonArray resultArray = new JsonArray();

        for (int i = 0; i < booksJSONArray.size(); i++) {
            JsonObject currentBook = (JsonObject) booksJSONArray.get(i);
            JsonObject currentBookVolumeInfo = (JsonObject) currentBook.get("volumeInfo");

            JsonArray currentBookCategories = currentBookVolumeInfo.getAsJsonArray("categories");

            if (currentBookCategories != null && currentBookCategories.contains(new JsonParser().parse(requestedCategory))) {
                resultArray.add(currentBook);
            }
        }
        return resultArray;
    }

    public static JSONArray getAuthorsRatings() {
        Map<String, List<Double>> authorsWithRatingsMap = createAuthorsWithSumOfAverageRatingsMap();
        JSONArray authorsWithAverageRatingsArray = new JSONArray();

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
        return authorsWithAverageRatingsArray;
    }

    private static Map<String, List<Double>> createAuthorsWithSumOfAverageRatingsMap() {
        JsonArray booksJSONArray = booksJSONObject.getAsJsonArray("items");
        Map<String, List<Double>> authorsWithSumOfAverageRatingsList = new HashMap<>();

        for (JsonElement currentBook : booksJSONArray) {
            JsonObject currentBookVolumeInfo = ((JsonObject) currentBook).getAsJsonObject("volumeInfo");
            JsonArray currentBookAuthors = currentBookVolumeInfo.getAsJsonArray("authors");

            if (currentBookAuthors != null) {
                for (JsonElement currentAuthor : currentBookAuthors) {
                    if (authorsWithSumOfAverageRatingsList.containsKey(currentAuthor.getAsString())) {
                        if (currentBookVolumeInfo.get("averageRating") != null) {
                            List<Double> currentValuesList = authorsWithSumOfAverageRatingsList.get(currentAuthor.getAsString());
                            double averageRating = currentBookVolumeInfo.get("averageRating").getAsDouble();
                            double currentSumOfAverageRating = currentValuesList.get(0);
                            double currentSumOfRatedBooks = currentValuesList.get(1);

                            double newSumOfAverageRating = currentSumOfAverageRating + averageRating;
                            double newSumOfRatedBooks = currentSumOfRatedBooks + 1;

                            currentValuesList.set(0, newSumOfAverageRating);
                            currentValuesList.set(1, newSumOfRatedBooks);
                        }
                    } else {
                        double currentSumOfAverageRating = 0.0;
                        if (currentBookVolumeInfo.get("averageRating") != null) {
                            currentSumOfAverageRating = currentBookVolumeInfo.get("averageRating").getAsDouble();
                        }
                        List<Double> tempList = new ArrayList<>(Arrays.asList(currentSumOfAverageRating, 1.0));
                        authorsWithSumOfAverageRatingsList.put(currentAuthor.getAsString(), tempList);
                    }
                }
            }
        }
        return authorsWithSumOfAverageRatingsList;
    }

    public static JsonArray getAllAuthors() {
        logger.info("BookRepository queried to find all authors.");

        JsonArray booksJSONArray = booksJSONObject.getAsJsonArray("items");
        HashSet<String> authorsSet = new HashSet<>();

        for (JsonElement currentBook : booksJSONArray) {
            JsonObject currentBookVolumeInfo = ((JsonObject) currentBook).getAsJsonObject("volumeInfo");
            JsonArray currentBookAuthors = currentBookVolumeInfo.getAsJsonArray("authors");

            if (currentBookAuthors != null) {
                for (JsonElement currentAuthor : currentBookAuthors) {
                    authorsSet.add(currentAuthor.getAsString());
                }
            }
        }
        return convertSetToJSONArray(authorsSet);
    }

    private static JsonArray convertSetToJSONArray(HashSet<String> resultSet) {
        JsonArray resultArray = new JsonArray();
        for (String item : resultSet) {
            resultArray.add(item);
        }
        return resultArray;
    }
}
