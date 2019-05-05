package com.javaproject.javatask.repository;

import com.google.gson.*;
import com.javaproject.javatask.rest.JavaTaskController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

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

        for(int i = 0; i < booksJSONArray.size(); i++) {
            JsonObject currentObject = (JsonObject) booksJSONArray.get(i);

            JsonObject currentObjectVolumeInfo = (JsonObject) currentObject.get("volumeInfo");
            JsonArray currentObjectIdentifiers = currentObjectVolumeInfo.getAsJsonArray("industryIdentifiers");

            for(int m = 0; m < currentObjectIdentifiers.size(); m++) {
                JsonObject industryIdentifier = (JsonObject) currentObjectIdentifiers.get(m);
                String ISBN = industryIdentifier.get("identifier").getAsString();

                if(ISBN.equals(requestedISBN)) {
                    return currentObject;
                }
            }
        }
        return null;
    }

    public static JsonArray getBooksByCategory(String requestedCategory) {
        logger.info("BookRepository queried with Category: " + requestedCategory);
        JsonArray booksJSONArray = booksJSONObject.getAsJsonArray("items");
        JsonArray resultArray = new JsonArray();

        for(int i = 0; i < booksJSONArray.size(); i++) {
            JsonObject currentObject = (JsonObject) booksJSONArray.get(i);
            JsonObject currentObjectVolumeInfo = (JsonObject) currentObject.get("volumeInfo");

            JsonArray currentObjectCategories = currentObjectVolumeInfo.getAsJsonArray("categories");

            if(currentObjectCategories != null && currentObjectCategories.contains(new JsonParser().parse(requestedCategory))) {
                resultArray.add(currentObject);
            }
        }
        return resultArray;
    }

    public static JsonArray getAuthorsRatings() {
        JsonArray booksJSONArray = booksJSONObject.getAsJsonArray("items");
        Map<String, List<Double>> authorsWithRatingsMap = createAuthorsWithRatingsMap();

        JsonArray authorsWithAverageRatingsArray = new JsonArray();
        for (Map.Entry<String, List<Double>> entry : authorsWithRatingsMap.entrySet()) {
            JsonObject newObject = new JsonObject();
            String authorName = entry.getKey();
            double sumOfRatings = entry.getValue().get(0);
            double sumOfRatedBooks = entry.getValue().get(1);
            double averageRating = sumOfRatings / sumOfRatedBooks;
            JsonElement element = new JsonParser().parse(authorName);
            newObject.add("author", element);
            authorsWithAverageRatingsArray.add(newObject);
        }
        return authorsWithAverageRatingsArray;
    }

    private static Map<String, List<Double>> createAuthorsWithRatingsMap() {
        JsonArray booksJSONArray = booksJSONObject.getAsJsonArray("items");
        Map<String, List<Double>> authorsWithRatingsList = new HashMap<>();

        for (JsonElement currentObject : booksJSONArray) {
            JsonObject currentObjectVolumeInfo = ((JsonObject) currentObject).getAsJsonObject("volumeInfo");
            JsonArray currentObjectAuthors = currentObjectVolumeInfo.getAsJsonArray("authors");

            if (currentObjectAuthors != null) {
                for (JsonElement currentAuthor : currentObjectAuthors) {

                    if (authorsWithRatingsList.containsKey(currentAuthor.getAsString())) {
                        if(currentObjectVolumeInfo.get("averageRating") != null) {
                            List<Double> currentValuesList = authorsWithRatingsList.get(currentAuthor.getAsString());
                            double averageRating = currentObjectVolumeInfo.get("averageRating").getAsDouble();
                            double newAverageRating = currentValuesList.get(0) + averageRating;
                            double newAmountOfRatedBooks = currentValuesList.get(1) + 1;
                            currentValuesList.set(0, newAverageRating);
                            currentValuesList.set(1, newAmountOfRatedBooks);
                        }
                    } else {
                        double averageRating = 0.0;
                        if(currentObjectVolumeInfo.get("averageRating") != null) {
                            averageRating = currentObjectVolumeInfo.get("averageRating").getAsDouble();
                        }
                        List<Double> tempList = new ArrayList<>(Arrays.asList(averageRating, 1.0));
                        authorsWithRatingsList.put(currentAuthor.getAsString(), tempList);
                    }
                }
            }
        }
        return authorsWithRatingsList;
    }

    public static JsonArray getAllAuthors() {
        logger.info("BookRepository queried to find all authors.");

        JsonArray booksJSONArray = booksJSONObject.getAsJsonArray("items");
        HashSet<String> authorsSet = new HashSet<>();

        for(JsonElement currentObject : booksJSONArray) {
            JsonObject currentObjectVolumeInfo = ((JsonObject) currentObject).getAsJsonObject("volumeInfo");
            JsonArray currentObjectAuthors = currentObjectVolumeInfo.getAsJsonArray("authors");

            if (currentObjectAuthors != null) {
                for (JsonElement currentAuthor : currentObjectAuthors) {
                    authorsSet.add(currentAuthor.getAsString());
                }
            }
        }
        return convertSetToJSONArray(authorsSet);
    }

    private static JsonArray convertSetToJSONArray(HashSet<String> resultSet) {
        JsonArray resultArray = new JsonArray();
        for(String item : resultSet) {
            resultArray.add(item);
        }
        return resultArray;
    }
}
