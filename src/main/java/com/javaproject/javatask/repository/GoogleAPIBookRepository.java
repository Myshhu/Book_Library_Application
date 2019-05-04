package com.javaproject.javatask.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.net.URL;
import java.nio.charset.Charset;

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

    private static String readResponseToString(BufferedReader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString();
    }
}
