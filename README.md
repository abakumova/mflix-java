# [M220J: MongoDB for Java Developers](https://university.mongodb.com/courses/M220J/about) 

MFlix software project is a training project provided by MongoDB University

## Project Structure
- **Frontend**: the built-in React application
- **Backend**: Java Spring Boot project (managed by Maven)

## How to run the application
```java
mvn spring-boot:run
```

## How to run tests of the application
Run _single_ test class:
```java
mvn -Dtest=<Test Class> test
```
Run _all Java files that end with Test.java_ (from all subdirectories)
```java 
cd mflix
mvn -Dtest="**/*Test.java" test
```
Run _whole test package_
```java
cd mflix
mvn -Dtest="src.test.java.**" test
```
## Chapters

### Chapter 1: Driver Setup
Database client configuration, basic reads

### Chapter 2: User-Facing Backend
Basic aggregation, updates, deletes, and joins

### Chapter 3: Admin Backend
Read concerns and bulk operations

### Chapter 4: Resiliency
Connection pooling, error and timeout handling, and principle of least privilege
