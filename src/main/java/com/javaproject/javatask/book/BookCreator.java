package com.javaproject.javatask.book;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookCreator {

    private BookCreator() {
    }

    private static final String INDUSTRY_IDENTIFIERS_KEY = "industryIdentifiers";
    private static final String TITLE_KEY = "title";
    private static final String SUBTITLE_KEY = "subtitle";
    private static final String PUBLISHER_KEY = "publisher";
    private static final String PUBLISHED_DATE_KEY = "publishedDate";
    private static final String DESCRIPTION_KEY = "description";
    private static final String PAGE_COUNT_KEY = "pageCount";
    private static final String IMAGE_LINKS_KEY = "imageLinks";
    private static final String LANGUAGE_KEY = "language";
    private static final String PREVIEW_LINK_KEY = "previewLink";
    private static final String AVERAGE_RATING_KEY = "averageRating";
    private static final String AUTHORS_KEY = "authors";
    private static final String CATEGORIES_KEY = "categories";

    public static JSONObject createBookRecord(JSONObject oldRecord) {
        JSONObject oldVolumeInfo = oldRecord.getJSONObject("volumeInfo");
        JSONObject bookRecord = new JSONObject();

        if (oldVolumeInfo.has(INDUSTRY_IDENTIFIERS_KEY) && isISBN13inIdentifiers(oldVolumeInfo.getJSONArray(INDUSTRY_IDENTIFIERS_KEY))) {
            String isbn = getISBN13fromIdentifiers(oldVolumeInfo.getJSONArray(INDUSTRY_IDENTIFIERS_KEY));
            bookRecord.put("isbn", isbn);
        } else if (oldRecord.has("id")) {
            String isbn = oldRecord.getString("id");
            bookRecord.put("isbn", isbn);
        } else {
            bookRecord.put("isbn", "NO_ISBN");
        }

        if (oldVolumeInfo.has(TITLE_KEY)) {
            String title = oldVolumeInfo.getString(TITLE_KEY);
            bookRecord.put(TITLE_KEY, title);
        }

        if (oldVolumeInfo.has(SUBTITLE_KEY)) {
            String subtitle = oldVolumeInfo.getString(SUBTITLE_KEY);
            bookRecord.put(SUBTITLE_KEY, subtitle);
        }

        if (oldVolumeInfo.has(PUBLISHER_KEY)) {
            String publisher = oldVolumeInfo.getString(PUBLISHER_KEY);
            bookRecord.put(PUBLISHER_KEY, publisher);
        }

        if (oldVolumeInfo.has(PUBLISHED_DATE_KEY)) {
            String dateString = oldVolumeInfo.getString(PUBLISHED_DATE_KEY);
            long publishedDate = createUnixTimestamp(dateString);
            bookRecord.put(PUBLISHED_DATE_KEY, publishedDate);
        }

        if (oldVolumeInfo.has(DESCRIPTION_KEY)) {
            String description = oldVolumeInfo.getString(DESCRIPTION_KEY);
            bookRecord.put(DESCRIPTION_KEY, description);
        }

        if (oldVolumeInfo.has(PAGE_COUNT_KEY)) {
            int pageCount = oldVolumeInfo.getInt(PAGE_COUNT_KEY);
            bookRecord.put(PAGE_COUNT_KEY, pageCount);
        }

        if (oldVolumeInfo.has(IMAGE_LINKS_KEY) && oldVolumeInfo.getJSONObject(IMAGE_LINKS_KEY).has("thumbnail")) {
            String thumbnailUrl = oldVolumeInfo.getJSONObject(IMAGE_LINKS_KEY).getString("thumbnail");
            bookRecord.put("thumbnailUrl", thumbnailUrl);
        }

        if (oldVolumeInfo.has(LANGUAGE_KEY)) {
            String language = oldVolumeInfo.getString(LANGUAGE_KEY);
            bookRecord.put(LANGUAGE_KEY, language);
        }

        if (oldVolumeInfo.has(PREVIEW_LINK_KEY)) {
            String previewLink = oldVolumeInfo.getString(PREVIEW_LINK_KEY);
            bookRecord.put(PREVIEW_LINK_KEY, previewLink);
        }

        if (oldVolumeInfo.has(AVERAGE_RATING_KEY)) {
            double averageRating = oldVolumeInfo.getDouble(AVERAGE_RATING_KEY);
            bookRecord.put(AVERAGE_RATING_KEY, averageRating);
        }

        if (oldVolumeInfo.has(AUTHORS_KEY)) {
            JSONArray authors = oldVolumeInfo.getJSONArray(AUTHORS_KEY);
            bookRecord.put(AUTHORS_KEY, authors);
        }

        if (oldVolumeInfo.has(CATEGORIES_KEY)) {
            JSONArray categories = oldVolumeInfo.getJSONArray(CATEGORIES_KEY);
            bookRecord.put(CATEGORIES_KEY, categories);
        }

        return bookRecord;
    }

    private static long createUnixTimestamp(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        long publishedDate = 0;
        try {
            date = format.parse(dateString);
            publishedDate = date.getTime();
        } catch (ParseException e) {
            try {
                format = new SimpleDateFormat("yyyy");
                date = format.parse(dateString);
                publishedDate = date.getTime();
            } catch (ParseException e1) {
                LoggerFactory.getLogger(BookCreator.class).error("Date creation error", e1);
            }
        }
        return publishedDate;
    }

    private static boolean isISBN13inIdentifiers(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            JSONObject currentItem = array.getJSONObject(i);
            if (currentItem.has("type") && currentItem.getString("type").equals("ISBN_13")) {
                return true;
            }
        }
        return false;
    }

    private static String getISBN13fromIdentifiers(JSONArray industryIdentifiers) {
        for (int i = 0; i < industryIdentifiers.length(); i++) {
            JSONObject currentItem = industryIdentifiers.getJSONObject(i);
            if (currentItem.has("type") && currentItem.getString("type").equals("ISBN_13")) {
                return currentItem.getString("identifier");
            }
        }
        return "";
    }
}
