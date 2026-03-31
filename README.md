# College Quiz Management System

A three-tier college quiz application built with Oracle DB, Java JDBC, and JavaFX.

## Tech Stack

- Database: Oracle SQL*Plus, PL/SQL, triggers, packages, and views
- Backend: Java 17, JDBC, DAO, Service, and Model layers
- Frontend: JavaFX with FXML and CSS

## What It Supports

- Separate logins for admin, teacher, and student
- Department, teacher, and student management
- Teacher-created subjects and quizzes
- Single-choice MCQ quizzes with autosave
- Timed attempts with row-level locking
- Manual result publishing
- Light and dark theme support

## Default Credentials

Admin:

- Username: `admin`
- Password: `admin123`

Sample teachers:

- `CSE001` / `teach001`
- `CCE001` / `teach002`
- `ITE001` / `teach003`

Sample students:

- `240953148` / `stud001`
- `240953194` / `stud002`
- `240953382` / `stud003`

## Database Setup

1. Start Oracle Database XE locally.
2. Open SQL*Plus as `system / Hello123!`.
3. Run [`step1_quiz_management_oracle.sql`](./step1_quiz_management_oracle.sql).

The script is safe to rerun and will recreate the schema and seed data.

## Run The App

1. Make sure Oracle XE is running.
2. Confirm the JDBC URL in [`src/main/java/com/collegequiz/util/DbUtil.java`](./src/main/java/com/collegequiz/util/DbUtil.java).
3. Run:

```bash
mvn javafx:run
```

## Documentation

- [Oracle SQL / PL-SQL Layer](./sql_plsql_layer.md)
- [Java Backend Layer](./java_backend_layer.md)
- [JavaFX Frontend Layer](./javafx_frontend_layer.md)
- [Step 1 Schema Notes](./step1_quiz_management_oracle.md)

## Notes

- Results stay hidden until a teacher publishes them.
- Teachers can only publish quizzes that have at least one attempt.
- Teacher codes are generated automatically from the department prefix.
- Student registration numbers must be exactly 9 digits.

## Project Structure

- `src/main/java` - Java source code
- `src/main/resources` - FXML and CSS
- `step1_quiz_management_oracle.sql` - Oracle schema and seed script
- `README.md` - project overview and run instructions

