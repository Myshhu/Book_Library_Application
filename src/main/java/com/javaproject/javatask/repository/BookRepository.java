package com.javaproject.javatask.repository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.javaproject.javatask.rest.JavaTaskController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

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
                logger.info(currentObjectCategories.toString());
                resultArray.add(currentObject);
            }
        }
        return resultArray;
    }
}
