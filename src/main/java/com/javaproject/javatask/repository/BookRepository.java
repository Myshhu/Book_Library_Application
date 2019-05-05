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

    public static JSONObject getBookByISBN(String requestedISBN) {
        logger.info("BookRepository queried with ISBN: " + requestedISBN);

        if (booksJSONArray != null) {
            //for (Object currentBook : booksJSONArray) {
            for (int i = 0; i < booksJSONArray.length(); i++) {
                JSONObject currentBook = booksJSONArray.getJSONObject(i);
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject("volumeInfo");
                JSONArray currentBookIdentifiers = currentBookVolumeInfo.getJSONArray("industryIdentifiers");

                //for (Object industryIdentifier : currentBookIdentifiers) {
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

    private static boolean containsString(JSONArray array, String element) {
        for (int i = 0; i < array.length(); i++) {
            String currentElement = array.getString(i);
            if (currentElement.equals(element)) {
                return true;
            }
        }
        return false;
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
        //JSONArray booksJSONArray = booksJSONObject.getAsJSONArray("items");
        Map<String, List<Double>> authorsWithSumOfAverageRatingsList = new HashMap<>();

        if (booksJSONArray != null) {
            for (int i = 0; i < booksJSONArray.length(); i++) {
                JSONObject currentBook = booksJSONArray.getJSONObject(i);
                JSONObject currentBookVolumeInfo = currentBook.getJSONObject("volumeInfo");
                if (currentBookVolumeInfo.has("authors")) {
                    JSONArray currentBookAuthors = currentBookVolumeInfo.getJSONArray("authors");

                    for (int m = 0; m < currentBookAuthors.length(); m++) {
                        String currentAuthor = currentBookAuthors.getString(m);
                        if (authorsWithSumOfAverageRatingsList.containsKey(currentAuthor)) {
                            if (currentBookVolumeInfo.has("averageRating")) {
                                List<Double> currentValuesList = authorsWithSumOfAverageRatingsList.get(currentAuthor);
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
                            if (currentBookVolumeInfo.has("averageRating")) {
                                currentSumOfAverageRating = currentBookVolumeInfo.getDouble("averageRating");
                            }
                            List<Double> tempList = new ArrayList<>(Arrays.asList(currentSumOfAverageRating, 1.0));
                            authorsWithSumOfAverageRatingsList.put(currentAuthor, tempList);
                        }
                    }
                }
            }
        }
        return authorsWithSumOfAverageRatingsList;
    }

    public static JSONArray getAllAuthors() {
        logger.info("BookRepository queried to find all authors.");

        //JSONArray booksJSONArray = booksJSONObject.getAsJSONArray("items");
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
