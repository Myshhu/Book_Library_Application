# Book Library Application

### Compiling and running
- mvn clean package
- java -jar target/javatask-0.0.1-SNAPSHOT.jar

### Application runs on port 8080 by default
### Endpoints: http://localhost:8080
- /bookdetails/{isbn} - Book details endpoint; search book with given isbn
- /bookdetails/googleapi/{isbn} - Book details endpoint; search book with given isbn with Google Books API
- /bookscategory/{category} - Books category endpoint; search for books in given category
- /bookscategory/googleapi/{category}
- /rating - Rating endpoint; calculate author rating
- /rating/googleapi - Calculated rating will be 0 because Google Books API does not provide book ratings


### Application is written in Spring framework
### Tests written in JUnit, RestAssured
### SonarLint plugin was used to check the quality of code
