# Simple Manage System

A simple management system built with Spring Boot 3.2.0.

## Technology Stack

- **Spring Boot**: 3.2.0
- **Java**: 17
- **Maven**: Project management and build tool
- **Lombok**: Reduce boilerplate code

## Project Structure

```
simple-manage/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/shanling/simplemanage/
│   │   │       ├── SimpleManageApplication.java  # Main application class
│   │   │       └── controller/
│   │   │           └── HelloController.java      # Example REST controller
│   │   └── resources/
│   │       └── application.yml                   # Application configuration
│   └── test/
│       └── java/
│           └── org/shanling/simplemanage/
│               └── SimpleManageApplicationTests.java
├── pom.xml                                       # Maven configuration
└── README.md

```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Running the Application

1. **Using Maven:**
   ```bash
   mvn spring-boot:run
   ```

2. **Using Java:**
   ```bash
   mvn clean package
   java -jar target/simple-manage-1.0-SNAPSHOT.jar
   ```

### Testing the Application

Once the application is running, you can test it by visiting:

- **Health Check**: http://localhost:8080/api/hello

The response should be:
```json
{
  "message": "Welcome to Simple Manage System!",
  "timestamp": "2024-01-01T12:00:00",
  "status": "success"
}
```

## Building the Project

```bash
mvn clean install
```

## Running Tests

```bash
mvn test
```

## Features

- ✅ Spring Boot 3.2.0 with Java 17
- ✅ RESTful API structure
- ✅ Hot reload with Spring DevTools
- ✅ Lombok for cleaner code
- ✅ Validation support
- ✅ Comprehensive testing setup

## Next Steps

- Add database integration (Spring Data JPA)
- Implement security (Spring Security)
- Add API documentation (SpringDoc OpenAPI)
- Implement business logic and entities

## Author

shanling
