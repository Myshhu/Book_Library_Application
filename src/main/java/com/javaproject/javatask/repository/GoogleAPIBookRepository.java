package com.javaproject.javatask.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    public static JsonObject getBookByISBN(String requestedISBN) {
        logger.info("Google API BookRepository queried with query: " + requestedISBN);
        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=" + requestedISBN);
        if (jsonText != null) {
            return new JsonParser().parse(jsonText).getAsJsonObject();
        } else {
            return null;
        }
    }

    public static JsonArray getBooksByCategory(String requestedCategory) {
        logger.info("Google API BookRepository queried with Category: " + requestedCategory);
        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=subject:" + requestedCategory);
        JsonObject responseObject;
        if (jsonText != null) {
            responseObject = new JsonParser().parse(jsonText).getAsJsonObject();
            if(responseObject.get("items") != null) {
                return responseObject.get("items").getAsJsonArray(); //Array of found books
            }
        }
        return new JsonArray(); //Return empty list
    }

    private static String getResponseStringFromURL(String url) {
        try {
            InputStream inputStream = new URL(url).openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            return readResponseToString(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getAuthorsRatings() {
        Map<String, List<Double>> authorsWithRatingsMap = createAuthorsWithRatingsMap();

        JSONArray authorsWithAverageRatingsArray = new JSONArray();
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
        return authorsWithAverageRatingsArray;
    }

    private static Map<String, List<Double>> createAuthorsWithRatingsMap() {
        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=*");
        JsonObject responseObject;
        if (jsonText != null) {
            responseObject = new JsonParser().parse(jsonText).getAsJsonObject();
            if(responseObject.get("items") != null) {
                JsonArray booksJSONArray = responseObject.get("items").getAsJsonArray(); //Array of found books

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
                                    double currentSumOfAverageRating = currentValuesList.get(0);
                                    double currentSumOfRatedBooks = currentValuesList.get(1);

                                    double newSumOfAverageRating = currentSumOfAverageRating + averageRating;
                                    double newSumOfRatedBooks = currentSumOfRatedBooks + 1;

                                    currentValuesList.set(0, newSumOfAverageRating);
                                    currentValuesList.set(1, newSumOfRatedBooks);
                                }
                            } else {
                                double currentSumOfAverageRating = 0.0;
                                if(currentObjectVolumeInfo.get("averageRating") != null) {
                                    currentSumOfAverageRating = currentObjectVolumeInfo.get("averageRating").getAsDouble();
                                }
                                List<Double> tempList = new ArrayList<>(Arrays.asList(currentSumOfAverageRating, 1.0));
                                authorsWithRatingsList.put(currentAuthor.getAsString(), tempList);
                            }
                        }
                    }
                }
                return authorsWithRatingsList;
            }
        }
        return null;
    }

    public static JsonArray getAllAuthors() {
        logger.info("BookRepository queried to find all authors.");

        String jsonText = getResponseStringFromURL("https://www.googleapis.com/books/v1/volumes?q=*");
        JsonObject responseObject;
        HashSet<String> authorsSet = new HashSet<>();
        if(jsonText != null) {
            responseObject = new JsonParser().parse(jsonText).getAsJsonObject();
            if(responseObject.get("items") != null) {
                JsonArray booksJSONArray = responseObject.get("items").getAsJsonArray(); //Array of found books

                for (JsonElement currentObject : booksJSONArray) {
                    JsonObject currentObjectVolumeInfo = ((JsonObject) currentObject).getAsJsonObject("volumeInfo");
                    JsonArray currentObjectAuthors = currentObjectVolumeInfo.getAsJsonArray("authors");

                    if (currentObjectAuthors != null) {
                        for (JsonElement currentAuthor : currentObjectAuthors) {
                            authorsSet.add(currentAuthor.getAsString());
                        }
                    }
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

    private static String readResponseToString(BufferedReader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString();
    }
}
