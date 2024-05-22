# Task Management Service

## Overview

This project is a task management service built using Scala and Akka HTTP. It provides APIs for managing tasks and users.

## Features

- **Task Management**: Create, update, delete tasks.
- **User Management**: Manage users and their tasks.
- **RESTful APIs**: Expose RESTful APIs for easy integration with client applications.

## Requirements

- Java 8 or higher
- Scala 3.3.3
- SBT 1.5.5
- Docker
- PostgreSQL (for database)

## Setup

1. **Clone the repository**:

   ```bash
   git clone https://github.com/your_username/task-management-service.git
   cd task-management-service
   ```

2. **Set up the database**:

    - Create a PostgreSQL database manually. You can use the following command:

      ```bash
      sudo -u postgres psql -c "CREATE DATABASE task_management_service;"
      ```

3. **Run with Docker**:

   ```bash
   sudo docker-compose -f docker-compose.services.yml up
   ```

4. **Compile and run the application**:

   ```bash
   sbt run
   ```

5. **Access the API**:

   You can access the API at `http://localhost:8080`.

## Configuration

- Database configuration: Update `src/main/resources/application.conf` with your database settings.

## Project Structure

- `src/main/scala`: Contains Scala source files for the application.
- `src/main/resources`: Contains configuration files.
- `src/test/scala`: Contains Scala test files.

## Dependencies

- Akka HTTP: Web server framework.
- Doobie: JDBC wrapper for database access.
- Scala Logging: Logging library.
- Flyway: Database migration tool.
- PureConfig: Configuration library.
- Scalafmt: Code formatter for Scala.
- Scalafix: Refactoring and linting tool for Scala.

