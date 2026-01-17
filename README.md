# 🏦 Digital Bank Simple App

A Spring Boot–based backend application that simulates a digital banking system, designed with scalability, consistency, and high concurrency in mind.

---

## 🎯 Design Decisions

### 1. **Layered Architecture**

* Clear separation between **Controller**, **Service**, and **Repository** layers
* Use of **DTOs** to isolate the persistence layer from the API contract

### 2. **Design Patterns**

* **Observer Pattern**: Applied to create a decoupled notification system
* **Repository Pattern**: Abstracts data access logic using Spring Data JPA

### 3. **Data Consistency**

* Use of `@Transactional` to ensure atomic operations
* Pessimistic locking to prevent race conditions in concurrent transactions
* Validations applied across multiple layers (DTO, service, and database)

### 4. **Resilience**

* Centralized exception handling
* Retry mechanisms for notification delivery
* Detailed logging to support debugging and monitoring

### 5. **Performance**

* Asynchronous processing for notifications
* Optimized database queries with proper indexing
* Pagination support for listing endpoints

---

## 🛠️ Tech Stack

* **Java 21**
* **Spring Boot**
* **Spring Data JPA / Hibernate**
* **H2 / PostgreSQL** (configurable)
* **Lombok**
* **Swagger / OpenAPI**
* **Maven**

---

## 🚀 How to Run

### Prerequisites

* Java 21 installed
* Maven installed

### Steps

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd <project-folder>
   ```

2. **Build the project**

   ```bash
   mvn clean install
   ```

3. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

4. **Access the API documentation**

   ```
   http://localhost:8080/digital-bank/swagger-ui.html
   ```

---

## 🧪 API Documentation

The application exposes its endpoints via **Swagger UI**, allowing easy testing and exploration of the API.

* Swagger UI: `http://localhost:8080/digital-bank/swagger-ui.html`

---

## 📌 Notes

* The application is designed to handle high-concurrency scenarios.
* Notifications are processed asynchronously to avoid impacting transaction performance.
* Database integrity is enforced via foreign keys and transactional boundaries.

---

## ✅ Status

✔️ Production-ready backend application following Java and Spring Boot best practices.
