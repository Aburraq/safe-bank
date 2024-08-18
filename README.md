# Safe Bank

Safe Bank is a secure banking application built with Spring Boot and JWT for authentication. This application includes features for user registration, login, and basic account management.

You can check swagger for detailed endpoints after running the app adjust the app.props: http://localhost:8080/swagger-ui/index.html#/

## Features

- **User Registration**: Allows users to sign up with personal details and create an account.
- **User Login**: Authenticates users and generates a JWT for secure access.
- **Account Management**: Provides basic functionality for managing user accounts.

## Technologies

- **Spring Boot**: Framework for building the application.
- **Spring Security**: For authentication and authorization.
- **JWT (JSON Web Token)**: For securing endpoints and user sessions.
- **H2 Database**: Embedded database for development and testing.
- **BCrypt**: For password hashing.

## Setup and Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- An IDE (e.g., IntelliJ IDEA, Eclipse)

### Clone the Repository

```bash
git clone https://github.com/Aburraq/safe-bank.git
cd safe-bank
```

## Configure Application  

### Application Properties
Adjust the file named application.properties in the src/main/resources directory with the following content:
```jwt secret
app.jwt-secret=YOUR_BASE64_ENCODED_SECRET
app.jwt-expiration=86400000
```
```email settings
spring.mail.username=yourEmailAddress - gmail
spring.mail.password=appPassword - you can get from gmail app passwords
```

## Contact
For any questions or inquiries, please contact Burak(https://github.com/Aburraq).


