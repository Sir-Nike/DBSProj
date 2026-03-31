# JavaFX Frontend Layer

This document explains the JavaFX UI layer of the College Quiz Management System.
The frontend is responsible for how the application looks, feels, and flows for
teachers, students, and admins.

## Purpose Of This Layer

The frontend exists to make the system usable and pleasant:

- Present each role with a clear screen.
- Keep forms compact and readable.
- Hide database complexity from users.
- Show validation and errors in a friendly way.
- Keep quiz actions intuitive and fast.

The UI is intentionally minimalist so the app feels clean rather than crowded.

## Project Structure

The frontend is made up of:

- `QuizManagementApp` as the JavaFX entry point
- `AppNavigator` for screen switching
- `ThemeManager` for light/dark theme selection
- `BaseController` for shared controller helpers
- `AppSession` for current user state
- `QuizRuntimeContext` for the active quiz attempt state
- FXML views in `src/main/resources/com/collegequiz/view`
- CSS in `src/main/resources/com/collegequiz/css`

## Screen Design

The app uses separate views for separate tasks. This keeps each screen focused
and prevents oversized forms.

### Login And Role Selection

- `auth-choice.fxml` lets the user choose teacher, student, or admin.
- `teacher-login.fxml` handles teacher login.
- `student-login.fxml` handles student login.
- `admin-login.fxml` handles admin login.

### Teacher Screens

- `teacher-home.fxml` acts as the teacher dashboard.
- `teacher-create-subject.fxml` creates subjects.
- `teacher-create-quiz.fxml` creates quizzes.
- `teacher-add-question.fxml` adds questions and options.
- `teacher-review.fxml` handles publish, unpublish, and delete quiz actions.

### Student Screens

- `student-dashboard.fxml` shows available quizzes and published results.
- `quiz-attempt.fxml` runs the timed quiz attempt flow.

### Admin Screen

- `admin-dashboard.fxml` manages departments, teachers, and students.

## Controller Responsibilities

Each controller handles one clear user task:

- Login controllers validate credentials and start sessions.
- Dashboard controllers load lists, counts, and tables.
- Creation controllers handle forms and validation.
- Attempt controller manages timing, autosave, and submission.
- Admin controller manages records and deletion workflows.

This keeps the UI easy to debug because each controller has a narrow job.

## Navigation

`AppNavigator` changes scenes and windows between screens. It also applies the
selected theme stylesheet and uses a small fade transition so the screen change
feels smoother.

## Theming

The UI supports both light and dark modes.

- `app.css` contains shared sizing and component rules.
- `theme-light.css` defines the light color palette.
- `theme-dark.css` defines the dark color palette.
- `ThemeManager` chooses which stylesheet to apply.

This keeps the visual style consistent across all screens.

## UI Behavior

The frontend handles a few important usability rules:

- It keeps forms small enough to fit in a normal desktop window.
- It shows placeholders when tables or lists are empty.
- It uses separate role-based login screens.
- It hides quiz results until they are published.
- It disables invalid actions, such as publishing a quiz with zero attempts.

## Why The UI Is Structured This Way

The app is built for a college environment, so the UI needs to be practical:

- quick to learn
- easy to scan
- not overloaded with unrelated content
- usable on standard desktop screens

The chosen layout style helps each task stand on its own without making the app
feel cluttered.

## Why This Layer Matters

The frontend is the part users see every day, so it needs to be calm and clear.
It also acts as the bridge between the human workflow and the backend logic.
Even though most business rules live in the database and backend, the frontend
controls how understandable and pleasant the system feels.
