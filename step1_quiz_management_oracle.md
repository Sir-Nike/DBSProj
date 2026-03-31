# Step 1: Oracle SQL and PL/SQL Setup

This file explains [`step1_quiz_management_oracle.sql`](./step1_quiz_management_oracle.sql), which sets up the database layer for the College Quiz Management System.

## What The Script Does

The script is written for SQL*Plus and is safe to rerun because it starts by dropping any previously created views, triggers, packages, sequences, and tables. After cleanup, it rebuilds the schema, loads sample data, and creates the PL/SQL logic needed for quiz creation, timed attempts, autosave, scoring, and result publication.

The script was designed for Oracle DB using the `system / Hello123!` account, as requested.

## Main Database Design

The core entities are:

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

The schema follows your ER diagram closely, with one important simplification: questions are single-choice only. That means each question has one correct option and each student answer stores one selected option.

For authentication, teachers now log in with a separate teacher code such as `CSE001` plus a password, and students log in with their 9-digit registration number plus a password.

## Why The Schema Was Slightly Adjusted

These changes were made to keep the design normalized and to make the required quiz flow work cleanly:

- `QUESTION_TYPE` is retained, but it is constrained to `SCQ` only.
- `QUESTION_OPTION` is used instead of `OPTION` because it is clearer and safer in Oracle naming.
- `STUDENT_ANSWER` stores a single `SELECTED_OPTION_ID`, which matches the single-choice MCQ requirement and the ER diagram more naturally.
- `RESULT_PUBLISH_LOG` was added to track manual publishing by teachers.
- `ANSWER_AUTOSAVE_LOG` was added to support immediate server-side autosave auditing.
- `TEACHER_CODE` and password columns were added to `TEACHER`, and a password column was added to `STUDENT`.
- `START_TIME`, `LAST_SAVED_AT`, and `STATUS` were added to `QUIZ_ATTEMPT` so timed quizzes and editing locks can be enforced properly.

## Constraints And Integrity

The script enforces integrity at the database level using:

- Primary keys
- Foreign keys
- Unique constraints
- Check constraints

Examples:

- Student registration numbers must be exactly 9 digits.
- Quiz duration must be between 1 and 300 minutes.
- Quiz results can only be `Y` or `N`.
- A student can have only one attempt row per quiz.
- Each question can have only one correct option.

## PL/SQL Logic

### `FN_IS_CORRECT_OPTION`

This helper function checks whether a selected option is marked correct. It is used during scoring.

### `PKG_QUIZ_ATTEMPT`

This package handles the student side of quiz attempts:

- `START_ATTEMPT` creates or reuses an in-progress attempt.
- `AUTOSAVE_ANSWER` saves a student choice immediately.
- `CLEAR_ANSWER` removes a saved answer if needed.
- `COMPUTE_ATTEMPT_SCORE` evaluates the attempt using a cursor.
- `SUBMIT_ATTEMPT` finalizes the attempt and stores the score.

It also uses row-level locking so two transactions cannot update the same attempt or answer at the same time.

### `PKG_QUIZ_ADMIN`

This package handles teacher-side quiz management:

- `CREATE_SUBJECT`
- `CREATE_QUIZ`
- `ADD_QUESTION`
- `ADD_OPTION`
- `RECOMPUTE_QUIZ_TOTALS`
- `PUBLISH_RESULTS`
- `UNPUBLISH_RESULTS`

It uses cursors to review attempts before publishing results and checks that every question is complete before publication.

## Triggers

The script uses triggers in a few important places:

- ID-generation triggers populate primary keys from sequences.
- Validation triggers enforce teacher name rules, student registration format, and answer consistency.
- An autosave trigger writes to `ANSWER_AUTOSAVE_LOG` whenever an answer is inserted, updated, or deleted.
- A compound trigger on `QUESTION` keeps `QUIZ.TOTAL_MARKS` synchronized with the sum of question marks.

## Views

Three views support the later UI and reporting layers:

- `VW_TEACHER_QUIZ_DASHBOARD` for teacher summary data
- `VW_TEACHER_SUBMISSIONS` for reviewing student submissions
- `VW_STUDENT_PUBLISHED_RESULTS` for showing results only after publication

## Test Data

The script loads:

- 2 departments
- 2 teachers
- 5 students
- 2 subjects
- 2 quizzes
- 6 questions
- option rows for all questions
- 3 sample attempts

This gives enough data to test quiz creation, autosave, scoring, submission, and result visibility.

## Validation

I executed the script successfully in local Oracle SQL*Plus. The setup completed without errors and the sample attempts were inserted correctly.

## Step 2 Preview

The next step is the Java backend:

- Model classes
- DAO layer
- Service layer
- JDBC transaction handling
- Calls into the PL/SQL packages

Once that is in place, the JavaFX frontend can sit on top of a clean 3-tier backend.
