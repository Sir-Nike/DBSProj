# SQL / PL-SQL Layer

This document explains the database layer of the College Quiz Management System.
It covers the schema, normalization decisions, constraints, triggers, views, and
PL/SQL packages defined in [`step1_quiz_management_oracle.sql`](./step1_quiz_management_oracle.sql).

## Purpose Of This Layer

The database is the source of truth for the whole application. All core quiz
rules are enforced here so the Java backend and JavaFX UI do not have to repeat
business logic.

The main responsibilities of this layer are:

- Store the quiz domain data in normalized tables.
- Enforce integrity with primary keys, foreign keys, unique constraints, and checks.
- Implement timed quiz behavior, autosave, scoring, and publishing in PL/SQL.
- Provide reporting views for the Java backend and dashboards.

## Main Tables

The schema uses separate tables for each entity:

- `DEPARTMENT`
- `TEACHER`
- `STUDENT`
- `SUBJECT`
- `QUIZ`
- `QUESTION`
- `QUESTION_OPTION`
- `QUIZ_ATTEMPT`
- `STUDENT_ANSWER`
- `RESULT_PUBLISH_LOG`
- `ANSWER_AUTOSAVE_LOG`

This separation keeps the data close to 3NF:

- Department details are stored once.
- Teacher and student identities are separate from subjects and quizzes.
- Questions are stored independently from their options.
- Attempts and answers are stored independently from quiz definitions.

## Normalization Rationale

The design is intentionally normalized to reduce duplication and update anomalies.

### 1NF

Each column holds a single value, not a repeating list. For example, question
options are stored as separate rows in `QUESTION_OPTION` instead of as a comma
separated list inside `QUESTION`.

### 2NF

Non-key attributes depend on the whole key. The many-to-many style data is split
into proper child tables:

- `QUESTION_OPTION` depends on `QUESTION`
- `STUDENT_ANSWER` depends on `QUIZ_ATTEMPT` and `QUESTION`

### 3NF

Non-key attributes depend only on the key, not on other non-key attributes.
Examples:

- Teacher name and password belong to `TEACHER`.
- Subject code and semester belong to `SUBJECT`.
- Student registration number belongs to `STUDENT`.

### Intentional Denormalization

`QUIZ.TOTAL_MARKS` is a derived value from the sum of question marks. It is kept
physically in the table for performance and dashboard convenience, and a trigger
keeps it synchronized.

## Constraints

The script uses explicit constraints everywhere:

- `PK_*` for primary keys
- `FK_*` for foreign keys
- `UQ_*` for uniqueness
- `CHK_*` for domain checks

Examples:

- Students must have 9-digit registration numbers.
- Quiz duration must be between 1 and 300 minutes.
- Question type is limited to `SCQ`.
- Each question can have only one correct answer.
- A student can only have one attempt per quiz.

## Sequences And Triggers

Sequences generate keys for the tables. Triggers are used for:

- Auto-generating teacher codes from the department prefix.
- Validating teacher and student fields.
- Validating student answer consistency.
- Keeping `QUIZ.TOTAL_MARKS` up to date.
- Logging autosave events and result publication events.

## PL/SQL Packages

### `PKG_QUIZ_ATTEMPT`

This package handles student-side quiz logic:

- `START_ATTEMPT` starts or reuses an attempt.
- `AUTOSAVE_ANSWER` stores an answer immediately.
- `CLEAR_ANSWER` removes a saved answer.
- `COMPUTE_ATTEMPT_SCORE` calculates the score with cursor logic.
- `SUBMIT_ATTEMPT` locks in the final attempt state.

It also uses row-level locking so one attempt cannot be edited by two sessions
at the same time.

### `PKG_QUIZ_ADMIN`

This package handles teacher-side and cleanup logic:

- `CREATE_SUBJECT`
- `CREATE_QUIZ`
- `ADD_QUESTION`
- `ADD_OPTION`
- `RECOMPUTE_QUIZ_TOTALS`
- `PUBLISH_RESULTS`
- `UNPUBLISH_RESULTS`
- admin cleanup mode control

Publishing checks that:

- The quiz has at least one question.
- Each question has exactly four options.
- Each question has exactly one correct option.
- The quiz has at least one attempt before publication.

## Views

The views support dashboards and result screens:

- `VW_TEACHER_QUIZ_DASHBOARD`
- `VW_TEACHER_SUBMISSIONS`
- `VW_STUDENT_PUBLISHED_RESULTS`

These views let the Java layer query summarized information without manually
joining tables every time.

## Test Data

The current starter data includes:

- 3 departments
- 3 teachers
- 3 students
- 4 subjects
- 4 quizzes
- 20 questions

No sample attempts are inserted now, so quizzes begin in an unattempted state.

## Why This Layer Matters

This layer keeps the system trustworthy. Even if the UI changes later, the
database still enforces:

- correct keys
- correct relationships
- valid quiz workflows
- publish rules
- autosave behavior

That means the app remains consistent even if the frontend or backend is
modified later.
