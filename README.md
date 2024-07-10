# BTC Trading Platform

This project is a Bitcoin trading platform implemented using Spring Boot. It allows users to buy and sell BTC, view their transaction history, and check the current BTC price.

## Features

1. User management (create and soft delete users)
2. BTC buying and selling
3. Transaction history
4. Real-time BTC price simulation

## Technologies Used

- Java 11
- Spring Boot 2.5.5
- PostgreSQL
- Maven
- Docker

## Setup and Installation

### Prerequisites

- Java 11 or higher
- Maven
- Docker and Docker Compose (for containerized setup)

### Running the Application

1. Clone the repository:
    ```bash
    git clone ....
    ```   
2. Build
    ```bash
    make build
    ```
3. Run
    ```bash
    make run
    ```
    The application will be available at `http://localhost:8080`.

4. End to end test
    ```bash
    make test
    ```

## API Endpoints

- POST `/users` - Create a new user
- DELETE `/users/{id}` - Soft delete a user
- POST `/transactions/buy` - Buy BTC
- POST `/transactions/sell` - Sell BTC
- GET `/transactions/{userId}` - Get user's transaction history
- GET `/transactions/btc/price` - Get current BTC price

## API document
./swagger.yml

## Testing

### Unit Tests

Run the unit tests using Maven:
```bash
mvn test
```

### End-to-End (e2e) Tests

We have implemented comprehensive e2e tests using Python. These tests cover the entire flow of the application, including user creation, BTC price checking, buying and selling BTC, and retrieving transaction history.

To run the e2e tests:

1. Ensure the application is running (either locally or in Docker).
2. Install the required Python packages:
    ```bash
    pip install requests
    ```
3. Run the e2e test script:
    ```bash
    python btc_trading_test.py
    ```
The e2e tests perform the following operations:

- Create a new user
- Check BTC price fluctuation
- Buy BTC
- Sell BTC
- Retrieve user transactions
- Delete the user