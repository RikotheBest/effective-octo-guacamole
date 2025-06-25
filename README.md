# E-Commerce Web Application

A full-stack e-commerce web application built as a pet project for learning purposes, featuring product management, search functionality, and category-based filtering.

## Features

- **Product Management**: Complete CRUD operations for products
- **Search & Filter**: Advanced search functionality with category-based filtering
- **Real-time Performance**: Redis caching for improved response times
- **Containerized Deployment**: Docker Compose for easy setup

## Technology Stack

### Backend
- **Java Spring Boot** - REST API development
- **PostgreSQL** - Primary database
- **JPA (Java Persistence API)** - Object-Relational Mapping
- **Redis** - Caching layer for performance optimization

### Frontend
- **React.js** - User interface and component management

### DevOps
- **Docker & Docker Compose** - Containerization and orchestration

## Prerequisites

Before running this project, make sure you have the following installed:

- [Docker](https://www.docker.com/get-started) (version 20.10 or higher)
- [Docker Compose](https://docs.docker.com/compose/install/) (version 2.0 or higher)

## Quick Start with Docker

1. **Clone the repository**
   ```bash
   git clone <your-repository-url>
   cd <project-directory>
   ```

2. **Start the application**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost:5173
   - Backend API: http://localhost:8080

4. **Stop the application**
   ```bash
   docker-compose down
   ```

## Manual Setup (Development)

### Backend Setup

1. **Prerequisites**
   - Java 17 or higher
   - Maven 3.6 or higher
   - PostgreSQL 17 or higher
   - Redis 6 or higher

2. **Database Configuration**
   ```sql
   CREATE DATABASE your_db;
   ```

3. **Application Configuration**
   Update the `application.properties` with your database credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.redis.host=localhost
   spring.redis.port=6379
   ```
   make sure to add the `spring.jpa.hibernate.ddl-auto=update` line,
   if you want JPA to create the tables for you based on the Entity classes.

5. **Run the Backend**
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend Setup

1. **Prerequisites**
   - Node.js 16 or higher
   - npm or yarn

2. **Install Dependencies**
   ```bash
   cd frontend
   npm install
   ```

3. **Run the Development Server**
   ```bash
   npm run dev
   ```



**Happy coding! 🚀**
