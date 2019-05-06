package com.javaproject.javatask.repository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BookRepositoryTests {

    @Before
    public void initBookRepository() {
        try {
            BookRepository.setBooksJSONArray(new JSONArray(testingArray));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBookByISBN() throws JSONException {
        assertNull(BookRepository.getBookByISBN("12"));

        if(BookRepository.getBooksJSONArray() != null) {
            JSONObject firstBook = (JSONObject) BookRepository.getBooksJSONArray().get(0);
            assertEquals(firstBook, BookRepository.getBookByISBN("0596001436")); //ISBN of first book
        } else {
            assertNull(BookRepository.getBookByISBN("0596001436")); //ISBN of first book
        }
    }

    @Test
    public void getBooksByCategory() {
        assertEquals(new JSONArray(), BookRepository.getBooksByCategory(""));

        JSONArray foundBooks = BookRepository.getBooksByCategory("Computers");
        assertEquals(4, foundBooks.length());

        foundBooks = BookRepository.getBooksByCategory("Biology");
        assertEquals(1, foundBooks.length());
    }

    private String testingArray = "[" +
            "{\n" +
            "   \"kind\": \"books#volume\",\n" +
            "   \"id\": \"eSRnOKwU4hUC\",\n" +
            "   \"etag\": \"psb5XQ7mMZQ\",\n" +
            "   \"selfLink\": \"https://www.googleapis.com/books/v1/volumes/eSRnOKwU4hUC\",\n" +
            "   \"volumeInfo\": {\n" +
            "    \"title\": \"Java and XSLT\",\n" +
            "    \"authors\": [\n" +
            "     \"Janice C. Newberry\"\n" +
            "    ],\n" +
            "    \"publisher\": \"\\\"O'Reilly Media, Inc.\\\"\",\n" +
            "    \"publishedDate\": \"2001\",\n" +
            "    \"description\": \"A guide for Java programmers explains how to use XSLT's ability to provide platform-independent data to build Web-based applications incorporating transformations as well as interactive Web site and wireless services.\",\n" +
            "    \"industryIdentifiers\": [\n" +
            "     {\n" +
            "      \"type\": \"ISBN_10\",\n" +
            "      \"identifier\": \"0596001436\"\n" +
            "     },\n" +
            "     {\n" +
            "      \"type\": \"ISBN_13\",\n" +
            "      \"identifier\": \"9780596001438\"\n" +
            "     }\n" +
            "    ],\n" +
            "    \"readingModes\": {\n" +
            "     \"text\": false,\n" +
            "     \"image\": true\n" +
            "    },\n" +
            "    \"pageCount\": 510,\n" +
            "    \"printType\": \"BOOK\",\n" +
            "    \"categories\": [\n" +
            "     \"Computers\"\n" +
            "    ],\n" +
            "    \"ratingsCount\": 2,\n" +
            "    \"maturityRating\": \"NOT_MATURE\",\n" +
            "    \"allowAnonLogging\": false,\n" +
            "    \"contentVersion\": \"preview-1.0.0\",\n" +
            "    \"imageLinks\": {\n" +
            "     \"smallThumbnail\": \"http://books.google.com/books/content?id=eSRnOKwU4hUC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api\",\n" +
            "     \"thumbnail\": \"http://books.google.com/books/content?id=eSRnOKwU4hUC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api\"\n" +
            "    },\n" +
            "    \"language\": \"en\",\n" +
            "    \"previewLink\": \"http://books.google.pl/books?id=eSRnOKwU4hUC&printsec=frontcover&dq=java&hl=&cd=29&source=gbs_api\",\n" +
            "    \"infoLink\": \"http://books.google.pl/books?id=eSRnOKwU4hUC&dq=java&hl=&source=gbs_api\",\n" +
            "    \"canonicalVolumeLink\": \"https://books.google.com/books/about/Java_and_XSLT.html?hl=&id=eSRnOKwU4hUC\"\n" +
            "   },\n" +
            "   \"saleInfo\": {\n" +
            "    \"country\": \"PL\",\n" +
            "    \"saleability\": \"NOT_FOR_SALE\",\n" +
            "    \"isEbook\": false\n" +
            "   },\n" +
            "   \"accessInfo\": {\n" +
            "    \"country\": \"PL\",\n" +
            "    \"viewability\": \"PARTIAL\",\n" +
            "    \"embeddable\": true,\n" +
            "    \"publicDomain\": false,\n" +
            "    \"textToSpeechPermission\": \"ALLOWED\",\n" +
            "    \"epub\": {\n" +
            "     \"isAvailable\": false\n" +
            "    },\n" +
            "    \"pdf\": {\n" +
            "     \"isAvailable\": true\n" +
            "    },\n" +
            "    \"webReaderLink\": \"http://play.google.com/books/reader?id=eSRnOKwU4hUC&hl=&printsec=frontcover&source=gbs_api\",\n" +
            "    \"accessViewStatus\": \"SAMPLE\",\n" +
            "    \"quoteSharingAllowed\": false\n" +
            "   },\n" +
            "   \"searchInfo\": {\n" +
            "    \"textSnippet\": \"A guide for Java programmers explains how to use XSLT&#39;s ability to provide platform-independent data to build Web-based applications incorporating transformations as well as interactive Web site and wireless services.\"\n" +
            "   }\n" +
            "  },\n" +
            "{\n" +
            "   \"kind\": \"books#volume\",\n" +
            "   \"id\": \"eSRnOKwU4hUC\",\n" +
            "   \"etag\": \"psb5XQ7mMZQ\",\n" +
            "   \"selfLink\": \"https://www.googleapis.com/books/v1/volumes/eSRnOKwU4hUC\",\n" +
            "   \"volumeInfo\": {\n" +
            "    \"title\": \"Java and XSLT\",\n" +
            "    \"authors\": [\n" +
            "     \"Janice C. Newberry\"\n" +
            "    ],\n" +
            "    \"publisher\": \"\\\"O'Reilly Media, Inc.\\\"\",\n" +
            "    \"publishedDate\": \"2001\",\n" +
            "    \"description\": \"A guide for Java programmers explains how to use XSLT's ability to provide platform-independent data to build Web-based applications incorporating transformations as well as interactive Web site and wireless services.\",\n" +
            "    \"industryIdentifiers\": [\n" +
            "     {\n" +
            "      \"type\": \"ISBN_10\",\n" +
            "      \"identifier\": \"0596001433\"\n" +
            "     },\n" +
            "     {\n" +
            "      \"type\": \"ISBN_13\",\n" +
            "      \"identifier\": \"9780596001435\"\n" +
            "     }\n" +
            "    ],\n" +
            "    \"readingModes\": {\n" +
            "     \"text\": false,\n" +
            "     \"image\": true\n" +
            "    },\n" +
            "    \"pageCount\": 510,\n" +
            "    \"printType\": \"BOOK\",\n" +
            "    \"categories\": [\n" +
            "     \"Computers\"\n" +
            "    ],\n" +
            "    \"averageRating\": 4.0,\n" +
            "    \"ratingsCount\": 2,\n" +
            "    \"maturityRating\": \"NOT_MATURE\",\n" +
            "    \"allowAnonLogging\": false,\n" +
            "    \"contentVersion\": \"preview-1.0.0\",\n" +
            "    \"imageLinks\": {\n" +
            "     \"smallThumbnail\": \"http://books.google.com/books/content?id=eSRnOKwU4hUC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api\",\n" +
            "     \"thumbnail\": \"http://books.google.com/books/content?id=eSRnOKwU4hUC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api\"\n" +
            "    },\n" +
            "    \"language\": \"en\",\n" +
            "    \"previewLink\": \"http://books.google.pl/books?id=eSRnOKwU4hUC&printsec=frontcover&dq=java&hl=&cd=29&source=gbs_api\",\n" +
            "    \"infoLink\": \"http://books.google.pl/books?id=eSRnOKwU4hUC&dq=java&hl=&source=gbs_api\",\n" +
            "    \"canonicalVolumeLink\": \"https://books.google.com/books/about/Java_and_XSLT.html?hl=&id=eSRnOKwU4hUC\"\n" +
            "   },\n" +
            "   \"saleInfo\": {\n" +
            "    \"country\": \"PL\",\n" +
            "    \"saleability\": \"NOT_FOR_SALE\",\n" +
            "    \"isEbook\": false\n" +
            "   },\n" +
            "   \"accessInfo\": {\n" +
            "    \"country\": \"PL\",\n" +
            "    \"viewability\": \"PARTIAL\",\n" +
            "    \"embeddable\": true,\n" +
            "    \"publicDomain\": false,\n" +
            "    \"textToSpeechPermission\": \"ALLOWED\",\n" +
            "    \"epub\": {\n" +
            "     \"isAvailable\": false\n" +
            "    },\n" +
            "    \"pdf\": {\n" +
            "     \"isAvailable\": true\n" +
            "    },\n" +
            "    \"webReaderLink\": \"http://play.google.com/books/reader?id=eSRnOKwU4hUC&hl=&printsec=frontcover&source=gbs_api\",\n" +
            "    \"accessViewStatus\": \"SAMPLE\",\n" +
            "    \"quoteSharingAllowed\": false\n" +
            "   },\n" +
            "   \"searchInfo\": {\n" +
            "    \"textSnippet\": \"A guide for Java programmers explains how to use XSLT&#39;s ability to provide platform-independent data to build Web-based applications incorporating transformations as well as interactive Web site and wireless services.\"\n" +
            "   }\n" +
            "  },\n" +
            "  {\n" +
            "   \"kind\": \"books#volume\",\n" +
            "   \"id\": \"aCkNegj6NnoC\",\n" +
            "   \"etag\": \"N//en1LxjaU\",\n" +
            "   \"selfLink\": \"https://www.googleapis.com/books/v1/volumes/aCkNegj6NnoC\",\n" +
            "   \"volumeInfo\": {\n" +
            "    \"title\": \"Object Oriented Programming Using C++ and Java\",\n" +
            "    \"authors\": [\n" +
            "     \"Douglas Lea\"\n" +
            "    ],\n" +
            "    \"publisher\": \"Pearson Education India\",\n" +
            "    \"publishedDate\": \"2011\",\n" +
            "    \"industryIdentifiers\": [\n" +
            "     {\n" +
            "      \"type\": \"ISBN_10\",\n" +
            "      \"identifier\": \"8131754553\"\n" +
            "     },\n" +
            "     {\n" +
            "      \"type\": \"ISBN_13\",\n" +
            "      \"identifier\": \"9788131754559\"\n" +
            "     }\n" +
            "    ],\n" +
            "    \"readingModes\": {\n" +
            "     \"text\": false,\n" +
            "     \"image\": true\n" +
            "    },\n" +
            "    \"pageCount\": 500,\n" +
            "    \"printType\": \"BOOK\",\n" +
            "    \"categories\": [\n" +
            "     \"Biology\"\n" +
            "    ],\n" +
            "    \"averageRating\": 5.0,\n" +
            "    \"maturityRating\": \"NOT_MATURE\",\n" +
            "    \"allowAnonLogging\": false,\n" +
            "    \"contentVersion\": \"0.0.1.0.preview.1\",\n" +
            "    \"panelizationSummary\": {\n" +
            "     \"containsEpubBubbles\": false,\n" +
            "     \"containsImageBubbles\": false\n" +
            "    },\n" +
            "    \"imageLinks\": {\n" +
            "     \"smallThumbnail\": \"http://books.google.com/books/content?id=aCkNegj6NnoC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api\",\n" +
            "     \"thumbnail\": \"http://books.google.com/books/content?id=aCkNegj6NnoC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api\"\n" +
            "    },\n" +
            "    \"language\": \"en\",\n" +
            "    \"previewLink\": \"http://books.google.pl/books?id=aCkNegj6NnoC&printsec=frontcover&dq=java&hl=&cd=30&source=gbs_api\",\n" +
            "    \"infoLink\": \"http://books.google.pl/books?id=aCkNegj6NnoC&dq=java&hl=&source=gbs_api\",\n" +
            "    \"canonicalVolumeLink\": \"https://books.google.com/books/about/Object_Oriented_Programming_Using_C++_an.html?hl=&id=aCkNegj6NnoC\"\n" +
            "   },\n" +
            "   \"saleInfo\": {\n" +
            "    \"country\": \"PL\",\n" +
            "    \"saleability\": \"NOT_FOR_SALE\",\n" +
            "    \"isEbook\": false\n" +
            "   },\n" +
            "   \"accessInfo\": {\n" +
            "    \"country\": \"PL\",\n" +
            "    \"viewability\": \"PARTIAL\",\n" +
            "    \"embeddable\": true,\n" +
            "    \"publicDomain\": false,\n" +
            "    \"textToSpeechPermission\": \"ALLOWED\",\n" +
            "    \"epub\": {\n" +
            "     \"isAvailable\": false\n" +
            "    },\n" +
            "    \"pdf\": {\n" +
            "     \"isAvailable\": false\n" +
            "    },\n" +
            "    \"webReaderLink\": \"http://play.google.com/books/reader?id=aCkNegj6NnoC&hl=&printsec=frontcover&source=gbs_api\",\n" +
            "    \"accessViewStatus\": \"SAMPLE\",\n" +
            "    \"quoteSharingAllowed\": false\n" +
            "   }\n" +
            "  },\n" +
            "  {\n" +
            "   \"kind\": \"books#volume\",\n" +
            "   \"id\": \"w7BmPTFgBXIC\",\n" +
            "   \"etag\": \"bumjnMWoVVo\",\n" +
            "   \"selfLink\": \"https://www.googleapis.com/books/v1/volumes/w7BmPTFgBXIC\",\n" +
            "   \"volumeInfo\": {\n" +
            "    \"title\": \"Java unleashed\",\n" +
            "    \"authors\": [\n" +
            "     \"Bret Barker\"\n" +
            "    ],\n" +
            "    \"publishedDate\": \"1996\",\n" +
            "    \"industryIdentifiers\": [\n" +
            "     {\n" +
            "      \"type\": \"ISBN_10\",\n" +
            "      \"identifier\": \"157521153X\"\n" +
            "     },\n" +
            "     {\n" +
            "      \"type\": \"ISBN_13\",\n" +
            "      \"identifier\": \"9781575211534\"\n" +
            "     }\n" +
            "    ],\n" +
            "    \"readingModes\": {\n" +
            "     \"text\": false,\n" +
            "     \"image\": false\n" +
            "    },\n" +
            "    \"pageCount\": 971,\n" +
            "    \"printType\": \"BOOK\",\n" +
            "    \"categories\": [\n" +
            "     \"Computers\"\n" +
            "    ],\n" +
            "    \"averageRating\": 3.5,\n" +
            "    \"maturityRating\": \"NOT_MATURE\",\n" +
            "    \"allowAnonLogging\": false,\n" +
            "    \"contentVersion\": \"preview-1.0.0\",\n" +
            "    \"imageLinks\": {\n" +
            "     \"smallThumbnail\": \"http://books.google.com/books/content?id=w7BmPTFgBXIC&printsec=frontcover&img=1&zoom=5&source=gbs_api\",\n" +
            "     \"thumbnail\": \"http://books.google.com/books/content?id=w7BmPTFgBXIC&printsec=frontcover&img=1&zoom=1&source=gbs_api\"\n" +
            "    },\n" +
            "    \"language\": \"en\",\n" +
            "    \"previewLink\": \"http://books.google.pl/books?id=w7BmPTFgBXIC&q=java&dq=java&hl=&cd=31&source=gbs_api\",\n" +
            "    \"infoLink\": \"http://books.google.pl/books?id=w7BmPTFgBXIC&dq=java&hl=&source=gbs_api\",\n" +
            "    \"canonicalVolumeLink\": \"https://books.google.com/books/about/Java_unleashed.html?hl=&id=w7BmPTFgBXIC\"\n" +
            "   },\n" +
            "   \"saleInfo\": {\n" +
            "    \"country\": \"PL\",\n" +
            "    \"saleability\": \"NOT_FOR_SALE\",\n" +
            "    \"isEbook\": false\n" +
            "   },\n" +
            "   \"accessInfo\": {\n" +
            "    \"country\": \"PL\",\n" +
            "    \"viewability\": \"NO_PAGES\",\n" +
            "    \"embeddable\": false,\n" +
            "    \"publicDomain\": false,\n" +
            "    \"textToSpeechPermission\": \"ALLOWED\",\n" +
            "    \"epub\": {\n" +
            "     \"isAvailable\": false\n" +
            "    },\n" +
            "    \"pdf\": {\n" +
            "     \"isAvailable\": false\n" +
            "    },\n" +
            "    \"webReaderLink\": \"http://play.google.com/books/reader?id=w7BmPTFgBXIC&hl=&printsec=frontcover&source=gbs_api\",\n" +
            "    \"accessViewStatus\": \"NONE\",\n" +
            "    \"quoteSharingAllowed\": false\n" +
            "   }\n" +
            "  },\n" +
            "  {\n" +
            "   \"kind\": \"books#volume\",\n" +
            "   \"id\": \"7S4ZpTR0DGwC\",\n" +
            "   \"etag\": \"FoKDrDEqH2o\",\n" +
            "   \"selfLink\": \"https://www.googleapis.com/books/v1/volumes/7S4ZpTR0DGwC\",\n" +
            "   \"volumeInfo\": {\n" +
            "    \"title\": \"Developing Games in Java\",\n" +
            "    \"authors\": [\n" +
            "     \"Douglas Lea\",\n" +
            "     \"Bret Barker\",\n" +
            "     \"Laurence Vanhelsuwé\"\n" +
            "    ],\n" +
            "    \"publisher\": \"New Riders\",\n" +
            "    \"publishedDate\": \"2004\",\n" +
            "    \"description\": \"A guide to Java game programming techniques covers such topics as 2D and 3D graphics, sound, artificial intelligence, multi-player games, collision detection, game scripting and customizing keyboard and mouse controls.\",\n" +
            "    \"industryIdentifiers\": [\n" +
            "     {\n" +
            "      \"type\": \"ISBN_10\",\n" +
            "      \"identifier\": \"1592730051\"\n" +
            "     },\n" +
            "     {\n" +
            "      \"type\": \"ISBN_13\",\n" +
            "      \"identifier\": \"9781592730056\"\n" +
            "     }\n" +
            "    ],\n" +
            "    \"readingModes\": {\n" +
            "     \"text\": false,\n" +
            "     \"image\": true\n" +
            "    },\n" +
            "    \"pageCount\": 972,\n" +
            "    \"printType\": \"BOOK\",\n" +
            "    \"categories\": [\n" +
            "     \"Computers\"\n" +
            "    ],\n" +
            "    \"averageRating\": 4.0,\n" +
            "    \"maturityRating\": \"NOT_MATURE\",\n" +
            "    \"allowAnonLogging\": false,\n" +
            "    \"contentVersion\": \"preview-1.0.0\",\n" +
            "    \"imageLinks\": {\n" +
            "     \"smallThumbnail\": \"http://books.google.com/books/content?id=7S4ZpTR0DGwC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api\",\n" +
            "     \"thumbnail\": \"http://books.google.com/books/content?id=7S4ZpTR0DGwC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api\"\n" +
            "    },\n" +
            "    \"language\": \"en\",\n" +
            "    \"previewLink\": \"http://books.google.pl/books?id=7S4ZpTR0DGwC&printsec=frontcover&dq=java&hl=&cd=32&source=gbs_api\",\n" +
            "    \"infoLink\": \"http://books.google.pl/books?id=7S4ZpTR0DGwC&dq=java&hl=&source=gbs_api\",\n" +
            "    \"canonicalVolumeLink\": \"https://books.google.com/books/about/Developing_Games_in_Java.html?hl=&id=7S4ZpTR0DGwC\"\n" +
            "   },\n" +
            "   \"saleInfo\": {\n" +
            "    \"country\": \"PL\",\n" +
            "    \"saleability\": \"NOT_FOR_SALE\",\n" +
            "    \"isEbook\": false\n" +
            "   },\n" +
            "   \"accessInfo\": {\n" +
            "    \"country\": \"PL\",\n" +
            "    \"viewability\": \"PARTIAL\",\n" +
            "    \"embeddable\": true,\n" +
            "    \"publicDomain\": false,\n" +
            "    \"textToSpeechPermission\": \"ALLOWED_FOR_ACCESSIBILITY\",\n" +
            "    \"epub\": {\n" +
            "     \"isAvailable\": false\n" +
            "    },\n" +
            "    \"pdf\": {\n" +
            "     \"isAvailable\": false\n" +
            "    },\n" +
            "    \"webReaderLink\": \"http://play.google.com/books/reader?id=7S4ZpTR0DGwC&hl=&printsec=frontcover&source=gbs_api\",\n" +
            "    \"accessViewStatus\": \"SAMPLE\",\n" +
            "    \"quoteSharingAllowed\": false\n" +
            "   },\n" +
            "   \"searchInfo\": {\n" +
            "    \"textSnippet\": \"A guide to Java game programming techniques covers such topics as 2D and 3D graphics, sound, artificial intelligence, multi-player games, collision detection, game scripting and customizing keyboard and mouse controls.\"\n" +
            "   }\n" +
            "   }\n" +
            "]";
}
