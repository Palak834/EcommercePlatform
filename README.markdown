# E-Commerce Microservices Application

## Overview
This project is a microservices-based e-commerce platform built using Spring Boot and Spring Cloud. It consists of multiple independent services that communicate via a gateway, with Eureka Server for service discovery. The application supports user registration, login, profile management, product browsing, cart operations, order processing, and payment handling.

## Services
- **Eureka Server**: Service registry for discovering other microservices (Port: 8761).
- **Gateway API**: Entry point for routing requests to appropriate services with JWT authentication (Port: 8080).
- **Registration Service**: Handles user registration (Port: 8081).
- **Login Service**: Manages user authentication and JWT token generation (Port: 8082).
- **Profile Service**: Manages user profile updates and retrieval (Port: 8083).
- **Product Service**: Handles product catalog and category management (Port: 8086).
- **Cart Service**: Manages user shopping carts (Port: 8084).
- **Order Service**: Processes and manages orders (Port: 8085).
- **Payment Service**: Handles payment processing (Port: 8087).

## Prerequisites
- Java 17
- Maven
- MySQL (with databases: `ecommerce_db`, `ecommerce_cart_db`, `ecommerce_order_db`, `ecommerce_payment_db`)
- Docker (optional, for containerization)

## Setup Instructions

### 1. Database Configuration
- Install MySQL and create the required databases.
- Update `application.properties` in each service with your MySQL credentials:
  - Username: `root`
  - Password: `root@1234`
  - URLs are pre-configured in the properties files.

### 2. Build the Project
- Navigate to each service directory (e.g., `EurekaServer`, `GatewayAPI`, etc.).
- Run the following command to build:
  ```bash
  mvn clean install
  ```

### 3. Run the Services
- Start the services in the following order:
  1. **Eureka Server**: `java -jar target/EurekaServer-0.0.1-SNAPSHOT.jar`
  2. **Gateway API**: `java -jar target/GatewayAPI-0.0.1-SNAPSHOT.jar`
  3. **Registration Service**: `java -jar target/RegistrationService-0.0.1-SNAPSHOT.jar`
  4. **Login Service**: `java -jar target/LoginService-0.0.1-SNAPSHOT.jar`
  5. **Profile Service**: `java -jar target/ProfileService-0.0.1-SNAPSHOT.jar`
  6. **Product Service**: `java -jar target/ProductService-0.0.1-SNAPSHOT.jar`
  7. **Cart Service**: `java -jar target/CartService-0.0.1-SNAPSHOT.jar`
  8. **Order Service**: `java -jar target/OrderService-0.0.1-SNAPSHOT.jar`
  9. **Payment Service**: `java -jar target/PaymentService-0.0.1-SNAPSHOT.jar`

### 4. API Endpoints
- Access the Gateway API at `http://localhost:8080`.
- Example endpoints:
  - Register: `POST /register`
  - Login: `POST /auth/login`
  - Get Profile: `GET /profile/{userId}` (requires JWT)
  - Add to Cart: `POST /cart` (requires JWT)
  - Create Order: `POST /order/user/{userId}` (requires JWT)

### 5. JWT Authentication
- Use the Login Service to generate a JWT token.
- Include the token in the `Authorization` header as `Bearer <token>` for protected endpoints.

## Configuration
- **JWT Secret**: `az19urDV3Ukf9srVBxvZxftj3PE2pgAKTGi4EOS5ObI=` (same across services).
- **Eureka URL**: `http://localhost:8761/eureka/`.
- Logs are configured with DEBUG level for troubleshooting.

## Running with Docker (Optional)
- Ensure Docker is installed.
- Build Docker images for each service using the provided `Dockerfile` (if added).
- Use `docker-compose` to orchestrate services (create a `docker-compose.yml` file if needed).

## Contributing
- Fork the repository.
- Create a new branch for your feature or bug fix.
- Submit a pull request with detailed changes.

## License
This project is currently unlicensed. Please contact the maintainers for details.

## Contact
For issues or questions, open an issue on this repository or contact the project maintainers.