# 📍 Points of Interest (POI) Service

A RESTful API service for creating, managing, and querying Points of Interest. Built with Java and Spring Boot, this application allows clients to interact with POIs efficiently using modern web standards and secure authentication.

---

## 🚀 Features

- Full CRUD operations on Points of Interest
- Search POIs by ID or by geographic area (latitude, longitude, and radius)
- RESTful API with well-structured endpoints
- Token-based authentication
- Layered architecture for modularity and maintainability

---

## 🧱 Architecture

The project follows a layered architecture:

- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Business logic and validations
- **Repository Layer**: Data persistence using PostgreSQL
- **Model Layer**: Defines POI data structures

---

## 🧪 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/points-of-interest` | Retrieve all POIs in a given area |
| `POST` | `/api/v1/points-of-interest` | Add a new POI |
| `GET` | `/api/v1/points-of-interest/{id}` | Get a POI by its ID |
| `PUT` | `/api/v1/points-of-interest/{id}` | Update an existing POI |
| `DELETE` | `/api/v1/points-of-interest/{id}` | Delete a POI |

---

## 🔐 Authentication

Authentication is implemented using tokens. Users must include a valid token in the `Authorization` header (e.g., `Bearer <token>`) for all protected endpoints.

---

## 🛠 Technologies

- **Java**
- **Spring Boot**
- **PostgreSQL**
- **Maven**
- **JWT (JSON Web Token)**

---

## 🏁 Getting Started

### Prerequisites
- Java 17+
- Maven
- PostgreSQL

### Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/poi-service.git
   cd poi-service
