package com.javaproject.javatask.repository;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

import java.net.URL;
import java.nio.charset.Charset;

public class GoogleAPIBookRepository {
    public static JsonObject getBookByISBN(String requestedISBN) {
        try {
            InputStream is = new URL("https://www.googleapis.com/books/v1/volumes?q=" + requestedISBN).openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(bufferedReader);
            return new JsonParser().parse(jsonText).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
