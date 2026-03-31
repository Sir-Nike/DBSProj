# Java Backend Layer

This document explains the Java backend of the College Quiz Management System.
The backend is built with JDBC and follows a 3-tier structure:

- Model
- DAO
- Service

The backend sits between the database and the JavaFX UI.

## Purpose Of This Layer

The backend is responsible for:

- Opening and managing JDBC connections.
- Translating database rows into Java model objects.
- Calling PL/SQL packages and stored procedures.
- Managing transactions, commit, and rollback.
- Converting SQL exceptions into application-friendly exceptions.

This keeps the UI simple and keeps SQL out of the controllers.

## Project Structure

The main backend packages are:

- `config`
- `dao`
- `dao.impl`
- `service`
- `service.impl`
- `model`
- `exception`
- `util`

Important classes include:

- `DbUtil` for database connectivity
- `QuizManagementDao` for data access contracts
- `QuizManagementDaoImpl` for Oracle calls and queries
- `QuizManagementService` for business operations
- `QuizManagementServiceImpl` for transaction management

## Model Layer

The model package contains immutable records and simple domain objects such as:

- `Teacher`
- `Student`
- `Department`
- `Subject`
- `Quiz`
- `Question`
- `QuestionOption`
- `QuizAttempt`
- `StudentAnswer`
- `TeacherDashboardRow`
- `StudentResultRow`
- `AvailableQuizRow`

These classes are used to move data cleanly between layers without exposing raw
database rows to the UI.

## DAO Layer

The DAO layer contains the SQL and PL/SQL calls.

### Responsibilities

- Insert and fetch records.
- Call Oracle packages and procedures.
- Run reporting queries for dashboards.
- Perform delete operations with row locking and cleanup support.

### Examples

- `createTeacher` inserts a teacher and lets the database generate the code.
- `addQuestionWithOptions` saves a question and its four options in one flow.
- `startAttempt` calls the quiz attempt package.
- `publishResults` calls the admin package.
- `removeQuiz` enforces creator ownership before deletion.

This layer is intentionally Oracle-aware because the project is built around
SQL*Plus-compatible scripts and PL/SQL packages.

## Service Layer

The service layer is where transaction management lives.

### Responsibilities

- Open a connection.
- Turn off auto-commit.
- Execute a DAO method.
- Commit on success.
- Roll back on failure.
- Map SQL failures to meaningful exceptions.

### Why This Is Important

Without this layer, the UI would have to know too much about database behavior.
With it, the JavaFX controllers can call a method like `createQuiz` or
`submitAttempt` without worrying about transaction boundaries.

## Exception Handling

The backend uses custom exceptions to keep error handling clean:

- `DatabaseConnectionException`
- `ServiceException`
- `QuizException`

This makes it easier to show a human-readable error in the UI while still
preserving the root cause for debugging.

## Authentication Flow

The backend supports three login roles:

- Teacher login by teacher code and password
- Student login by registration number and password
- Admin login using the fixed credentials `admin / admin123`

The DAO methods `authenticateTeacher` and `authenticateStudent` map the database
rows into session objects used by the UI.

## Business Logic In The Backend

The backend provides the main application rules that are easier to keep in Java:

- Teachers can create subjects, quizzes, and questions.
- Students can start timed attempts and autosave answers.
- Teachers can review and publish results manually.
- Admin can manage departments, teachers, and students.

The database still enforces the important constraints, but the backend makes the
application flow simpler and more user-friendly.

## Concurrency And Transactions

The backend relies on Oracle row locking and transactional boundaries for safe
multi-user behavior.

- A student attempt is locked while being edited.
- Quiz submission commits once the answer score is finalized.
- Publish and delete operations run in a single transaction so partial cleanup
  does not leave broken data behind.

## Why This Layer Matters

This layer isolates database access from the UI and keeps the application
maintainable. It is the bridge that turns Oracle tables and PL/SQL packages into
Java objects and user actions.
