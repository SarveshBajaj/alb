# Request Forwarding Service

This project demonstrates a request forwarding service using Spring Boot. It includes a custom filter (`RequestForwardingFilter`) to forward HTTP requests from a load balancer to a backend server and return the response to the original client.

## Features
- Forwards HTTP requests to a specified backend server.
- Handles forwarding of headers, request bodies, and response statuses.
- Supports multiple HTTP methods (GET, POST, etc.).
- Easily configurable backend server details via properties.

## Requirements
- Java 17 or higher
- Maven
- Spring Boot
- Python (for the backend server example)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/request-forwarding-service.git
cd request-forwarding-service
```

### 2. Build the project
```bash
mvn clean install
```

### 3. Run the application

```bash
mvn spring-boot:run
```
### 4. Configure the Backend Server
The backend server details are configured in the application.properties file. You can change the backend.server.host and backend.server.port to point to your actual backend server.

```bash
backend.server.host=localhost
backend.server.port=8080
```
### 5. Example Backend Server
You can run a simple backend server using Python to test the forwarding functionality:


```bash
python -m http.server 8080 --directory /path/to/static/files
```
Place a static file (e.g., index.html) in the directory you're serving from. The Spring Boot application will forward requests to this Python server.