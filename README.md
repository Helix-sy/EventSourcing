# 🚀 Event Sourcing E-Commerce Application

## 📖 Overview
This project is an event-sourcing-based e-commerce application built using Kotlin, Spring Boot, and Axon Framework. It supports managing products, carts, and orders with event-sourcing principles.

## ✨ Features
- **Event Sourcing**: Uses Axon Framework for command and event handling.
- **REST API**: Endpoints for managing products and carts.
- **CSV Data Integration**: Load product data from CSV files.
- **Inventory Management**: Track and update product stock.
- **Testing**: Includes unit and integration tests.
- **Monitoring**: Integrated with Spring Boot Actuator for application health and metrics.
- **Swagger UI**: Interactive API documentation and testing.

## 🛠️ Setup

### Prerequisites
- Java 19
- Maven
- PostgreSQL

### Running the Application
1. Clone the repository.
2. Navigate to the project directory.
3. Run the following command to start the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Loading CSV Data
To load product data from a CSV file, use the `CsvDataLoader` service. Example:
```kotlin
val csvDataLoader = CsvDataLoader(commandGateway)
csvDataLoader.loadCsvData("path/to/csv/file.csv")
```

## 🌐 API Endpoints

### Products
- **Add Product**: `POST /products`
- **Update Stock**: `PUT /products/{productId}/stock`
- **List Products**: `GET /products`
- **Get Product by ID**: `GET /products/{productId}`

### Carts
- **Add Item to Cart**: `POST /additem/{aggregateId}`

## 🧪 Testing
Run the tests using:
```bash
./mvnw test
```

## 📊 Performance Testing
Use the Kaggle cosmetics dataset to simulate real-world scenarios and measure performance.

## 🔍 Monitoring

The application includes monitoring capabilities using Spring Boot Actuator. Actuator provides various endpoints to monitor and manage the application.

### Enabling Actuator
1. Ensure the application is running.
2. Access the Actuator endpoints at `http://localhost:8080/actuator`.

### Key Endpoints
- `/actuator/health`: Check the health status of the application.
- `/actuator/metrics`: View application metrics.
- `/actuator/httptrace`: Trace HTTP requests.

### Customization
You can customize the Actuator settings in `application.yml` under the `management` section.

## 🖥️ Swagger UI

The application includes Swagger UI for testing and exploring the REST API endpoints.

### Accessing Swagger UI
1. Start the application.
2. Open your browser and navigate to `http://localhost:8080/swagger-ui.html`.
3. Use the interface to test the available API endpoints.

### Example Endpoints
- **Add Product**: `POST /products`
- **Update Stock**: `PUT /products/{productId}/stock`
- **Add Item to Cart**: `POST /additem/{aggregateId}`

Swagger UI provides a user-friendly way to interact with the API without needing external tools like Postman.

## 📚 Understanding Event Sourcing - The Book

### Book
The book is written in public and current progress can always be checked [here](https://eventmodelers.de/das-eventsourcing-buch).

The GitHub Repository including all source code can be found [here](https://github.com/dilgerma/eventsourcing-book).

## 📝 License
This project is licensed under the MIT License.
