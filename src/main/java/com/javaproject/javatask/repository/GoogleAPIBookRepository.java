package com.javaproject.javatask.repository;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.javaproject.javatask.rest.JavaTaskController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.net.URL;
import java.nio.charset.Charset;

public class GoogleAPIBookRepository {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAPIBookRepository.class);

    public static JsonObject getBookByISBN(String requestedISBN) {
        try {
            logger.info("Google API book queried with query: " + requestedISBN);
            InputStream inputStream = new URL("https://www.googleapis.com/books/v1/volumes?q=" + requestedISBN).openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String jsonText = readResponseToString(bufferedReader);
            return new JsonParser().parse(jsonText).getAsJsonObject();
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
