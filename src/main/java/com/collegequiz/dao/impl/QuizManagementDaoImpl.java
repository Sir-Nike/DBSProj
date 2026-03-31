package com.collegequiz.dao.impl;

import com.collegequiz.dao.QuizManagementDao;
import com.collegequiz.model.Department;
import com.collegequiz.model.Question;
import com.collegequiz.model.QuestionOption;
import com.collegequiz.model.Quiz;
import com.collegequiz.model.Student;
import com.collegequiz.model.Subject;
import com.collegequiz.model.Teacher;
import com.collegequiz.model.StudentResultRow;
import com.collegequiz.model.TeacherDashboardRow;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuizManagementDaoImpl implements QuizManagementDao {
    @Override
    public Integer createSubject(Connection connection, String subjectName, String subjectCode, int semester, int departmentId)
            throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{ call PKG_QUIZ_ADMIN.CREATE_SUBJECT(?,?,?,?,?) }")) {
            cs.setString(1, subjectName);
            cs.setString(2, subjectCode);
            cs.setInt(3, semester);
            cs.setInt(4, departmentId);
            cs.registerOutParameter(5, java.sql.Types.INTEGER);
            cs.execute();
            return cs.getInt(5);
        }
    }

    @Override
    public Integer createDepartment(Connection connection, String departmentCode, String departmentName)
            throws SQLException {
        int departmentId = nextSequenceValue(connection, "SEQ_DEPARTMENT");
        String sql = """
                INSERT INTO DEPARTMENT (DEPARTMENT_ID, DEPARTMENT_CODE, DEPARTMENT_NAME)
                VALUES (?, UPPER(TRIM(?)), ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            ps.setString(2, departmentCode);
            ps.setString(3, departmentName);
            ps.executeUpdate();
        }
        return departmentId;
    }

    @Override
    public Integer createQuiz(Connection connection, String quizTitle, int durationMinutes, LocalDateTime quizDate,
                              int subjectId, int createdBy) throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{ call PKG_QUIZ_ADMIN.CREATE_QUIZ(?,?,?,?,?,?) }")) {
            cs.setString(1, quizTitle);
            cs.setInt(2, durationMinutes);
            cs.setTimestamp(3, Timestamp.valueOf(quizDate));
            cs.setInt(4, subjectId);
            cs.setInt(5, createdBy);
            cs.registerOutParameter(6, java.sql.Types.INTEGER);
            cs.execute();
            return cs.getInt(6);
        }
    }

    @Override
    public Integer createTeacher(Connection connection, String name, String password, int departmentId)
            throws SQLException {
        int teacherId = nextSequenceValue(connection, "SEQ_TEACHER");
        String sql = """
                INSERT INTO TEACHER (TEACHER_ID, NAME, PASSWORD, DEPARTMENT_ID)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setString(2, name);
            ps.setString(3, password);
            ps.setInt(4, departmentId);
            ps.executeUpdate();
        }
        return teacherId;
    }

    @Override
    public Integer createStudent(Connection connection, String registrationNo, String name, String password,
                                 int departmentId) throws SQLException {
        int studentId = nextSequenceValue(connection, "SEQ_STUDENT");
        String sql = """
                INSERT INTO STUDENT (STUDENT_ID, REGISTRATION_NO, NAME, PASSWORD, DEPARTMENT_ID)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, registrationNo);
            ps.setString(3, name);
            ps.setString(4, password);
            ps.setInt(5, departmentId);
            ps.executeUpdate();
        }
        return studentId;
    }

    @Override
    public Integer addQuestion(Connection connection, int quizId, String questionText, double marks, int displayOrder)
            throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{ call PKG_QUIZ_ADMIN.ADD_QUESTION(?,?,?,?,?) }")) {
            cs.setInt(1, quizId);
            cs.setString(2, questionText);
            cs.setDouble(3, marks);
            cs.setInt(4, displayOrder);
            cs.registerOutParameter(5, java.sql.Types.INTEGER);
            cs.execute();
            return cs.getInt(5);
        }
    }

    @Override
    public Integer addOption(Connection connection, int questionId, String optionText, String isCorrect, int displayOrder)
            throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{ call PKG_QUIZ_ADMIN.ADD_OPTION(?,?,?,?,?) }")) {
            cs.setInt(1, questionId);
            cs.setString(2, optionText);
            cs.setString(3, isCorrect);
            cs.setInt(4, displayOrder);
            cs.registerOutParameter(5, java.sql.Types.INTEGER);
            cs.execute();
            return cs.getInt(5);
        }
    }

    @Override
    public Integer startAttempt(Connection connection, int quizId, int studentId) throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{ ? = call PKG_QUIZ_ATTEMPT.START_ATTEMPT(?,?) }")) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, quizId);
            cs.setInt(3, studentId);
            cs.execute();
            return cs.getInt(1);
        }
    }

    @Override
    public void autosaveAnswer(Connection connection, int attemptId, int questionId, int selectedOptionId)
            throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{ call PKG_QUIZ_ATTEMPT.AUTOSAVE_ANSWER(?,?,?) }")) {
            cs.setInt(1, attemptId);
            cs.setInt(2, questionId);
            cs.setInt(3, selectedOptionId);
            cs.execute();
        }
    }

    @Override
    public void clearAnswer(Connection connection, int attemptId, int questionId) throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{ call PKG_QUIZ_ATTEMPT.CLEAR_ANSWER(?,?) }")) {
            cs.setInt(1, attemptId);
            cs.setInt(2, questionId);
            cs.execute();
        }
    }

    @Override
    public double submitAttempt(Connection connection, int attemptId) throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{ call PKG_QUIZ_ATTEMPT.SUBMIT_ATTEMPT(?) }");
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT TOTAL_SCORE FROM QUIZ_ATTEMPT WHERE ATTEMPT_ID = ?")) {
            cs.setInt(1, attemptId);
            cs.execute();

            ps.setInt(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
            return 0.0d;
        }
    }

    @Override
    public void publishResults(Connection connection, int quizId, int teacherId) throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{ call PKG_QUIZ_ADMIN.PUBLISH_RESULTS(?,?) }")) {
            cs.setInt(1, quizId);
            cs.setInt(2, teacherId);
            cs.execute();
        }
    }

    @Override
    public void unpublishResults(Connection connection, int quizId, int teacherId) throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{ call PKG_QUIZ_ADMIN.UNPUBLISH_RESULTS(?,?) }")) {
            cs.setInt(1, quizId);
            cs.setInt(2, teacherId);
            cs.execute();
        }
    }

    @Override
    public void removeQuiz(Connection connection, int quizId, int teacherId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT CREATED_BY FROM QUIZ WHERE QUIZ_ID = ? FOR UPDATE")) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Quiz not found.");
                }
                if (rs.getInt(1) != teacherId) {
                    throw new SQLException("Only the quiz creator can delete this quiz.");
                }
            }
        }

        setAdminCleanupMode(connection, true);
        try {
            deleteQuizGraph(connection, quizId);
        } finally {
            setAdminCleanupMode(connection, false);
        }
    }

    @Override
    public void removeTeacher(Connection connection, int teacherId) throws SQLException {
        setAdminCleanupMode(connection, true);
        try {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM RESULT_PUBLISH_LOG WHERE PUBLISHED_BY = ?")) {
                ps.setInt(1, teacherId);
                ps.executeUpdate();
            }

            List<Integer> quizIds = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT QUIZ_ID FROM QUIZ WHERE CREATED_BY = ?")) {
                ps.setInt(1, teacherId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        quizIds.add(rs.getInt(1));
                    }
                }
            }

            for (Integer quizId : quizIds) {
                deleteQuizGraph(connection, quizId);
            }

            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM TEACHER WHERE TEACHER_ID = ?")) {
                ps.setInt(1, teacherId);
                ps.executeUpdate();
            }
        } finally {
            setAdminCleanupMode(connection, false);
        }
    }

    @Override
    public void removeStudent(Connection connection, int studentId) throws SQLException {
        setAdminCleanupMode(connection, true);
        try {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM ANSWER_AUTOSAVE_LOG WHERE ATTEMPT_ID IN (SELECT ATTEMPT_ID FROM QUIZ_ATTEMPT WHERE STUDENT_ID = ?)")) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM STUDENT_ANSWER WHERE ATTEMPT_ID IN (SELECT ATTEMPT_ID FROM QUIZ_ATTEMPT WHERE STUDENT_ID = ?)")) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM QUIZ_ATTEMPT WHERE STUDENT_ID = ?")) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM STUDENT WHERE STUDENT_ID = ?")) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }
        } finally {
            setAdminCleanupMode(connection, false);
        }
    }

    @Override
    public void clearAllOperationalData(Connection connection) throws SQLException {
        setAdminCleanupMode(connection, true);
        try {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM ANSWER_AUTOSAVE_LOG")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM RESULT_PUBLISH_LOG")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM STUDENT_ANSWER")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM QUIZ_ATTEMPT")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM QUESTION_OPTION")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM QUESTION")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM QUIZ")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM SUBJECT")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM TEACHER")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM STUDENT")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM DEPARTMENT")) {
                ps.executeUpdate();
            }
        } finally {
            setAdminCleanupMode(connection, false);
        }
    }

    @Override
    public List<TeacherDashboardRow> fetchTeacherDashboard(Connection connection, int teacherId) throws SQLException {
        String sql = """
                SELECT TEACHER_ID, TEACHER_NAME, DEPARTMENT_NAME, SUBJECT_ID, SUBJECT_CODE, SUBJECT_NAME,
                       QUIZ_ID, QUIZ_TITLE, QUIZ_DATE, DURATION_MINUTES, TOTAL_MARKS, RESULTS_PUBLISHED,
                       QUESTION_COUNT, ATTEMPT_COUNT, SUBMITTED_COUNT, AVG_SCORE, TOP_SCORE
                  FROM VW_TEACHER_QUIZ_DASHBOARD
                 WHERE TEACHER_ID = ?
                 ORDER BY QUIZ_DATE DESC, QUIZ_ID DESC
                """;
        List<TeacherDashboardRow> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new TeacherDashboardRow(
                            rs.getInt("TEACHER_ID"),
                            rs.getString("TEACHER_NAME"),
                            rs.getString("DEPARTMENT_NAME"),
                            rs.getInt("SUBJECT_ID"),
                            rs.getString("SUBJECT_CODE"),
                            rs.getString("SUBJECT_NAME"),
                            rs.getInt("QUIZ_ID"),
                            rs.getString("QUIZ_TITLE"),
                            rs.getTimestamp("QUIZ_DATE").toLocalDateTime(),
                            rs.getInt("DURATION_MINUTES"),
                            rs.getDouble("TOTAL_MARKS"),
                            rs.getString("RESULTS_PUBLISHED"),
                            rs.getInt("QUESTION_COUNT"),
                            rs.getInt("ATTEMPT_COUNT"),
                            rs.getInt("SUBMITTED_COUNT"),
                            rs.getObject("AVG_SCORE") == null ? null : rs.getDouble("AVG_SCORE"),
                            rs.getObject("TOP_SCORE") == null ? null : rs.getDouble("TOP_SCORE")
                    ));
                }
            }
        }
        return rows;
    }

    @Override
    public List<StudentResultRow> fetchPublishedResults(Connection connection, int studentId) throws SQLException {
        String sql = """
                SELECT ATTEMPT_ID, STUDENT_ID, REGISTRATION_NO, STUDENT_NAME, QUIZ_ID, QUIZ_TITLE,
                       SUBJECT_CODE, SUBJECT_NAME, TOTAL_SCORE, TOTAL_MARKS, PERCENTAGE, SUBMISSION_TIME
                  FROM VW_STUDENT_PUBLISHED_RESULTS
                 WHERE STUDENT_ID = ?
                 ORDER BY SUBMISSION_TIME DESC NULLS LAST, QUIZ_ID DESC
                """;
        List<StudentResultRow> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new StudentResultRow(
                            rs.getInt("ATTEMPT_ID"),
                            rs.getInt("STUDENT_ID"),
                            rs.getString("REGISTRATION_NO"),
                            rs.getString("STUDENT_NAME"),
                            rs.getInt("QUIZ_ID"),
                            rs.getString("QUIZ_TITLE"),
                            rs.getString("SUBJECT_CODE"),
                            rs.getString("SUBJECT_NAME"),
                            rs.getDouble("TOTAL_SCORE"),
                            rs.getDouble("TOTAL_MARKS"),
                            rs.getObject("PERCENTAGE") == null ? null : rs.getDouble("PERCENTAGE"),
                            rs.getTimestamp("SUBMISSION_TIME").toLocalDateTime()
                    ));
                }
            }
        }
        return rows;
    }

    @Override
    public List<Department> fetchDepartments(Connection connection) throws SQLException {
        String sql = """
                SELECT DEPARTMENT_ID, DEPARTMENT_NAME
                  FROM DEPARTMENT
                 ORDER BY DEPARTMENT_NAME
                """;
        List<Department> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new Department(rs.getInt("DEPARTMENT_ID"), rs.getString("DEPARTMENT_NAME")));
            }
        }
        return rows;
    }

    @Override
    public List<Teacher> fetchAllTeachers(Connection connection) throws SQLException {
        String sql = """
                SELECT TEACHER_ID, TEACHER_CODE, NAME, DEPARTMENT_ID
                  FROM TEACHER
                 ORDER BY NAME
                """;
        List<Teacher> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new Teacher(
                        rs.getInt("TEACHER_ID"),
                        rs.getString("TEACHER_CODE"),
                        rs.getString("NAME"),
                        rs.getInt("DEPARTMENT_ID")));
            }
        }
        return rows;
    }

    @Override
    public List<Student> fetchAllStudents(Connection connection) throws SQLException {
        String sql = """
                SELECT STUDENT_ID, REGISTRATION_NO, NAME, DEPARTMENT_ID
                  FROM STUDENT
                 ORDER BY NAME
                """;
        List<Student> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new Student(
                        rs.getInt("STUDENT_ID"),
                        rs.getString("REGISTRATION_NO"),
                        rs.getString("NAME"),
                        rs.getInt("DEPARTMENT_ID")));
            }
        }
        return rows;
    }

    @Override
    public List<Teacher> fetchTeachersByDepartment(Connection connection, int departmentId) throws SQLException {
        String sql = """
                SELECT TEACHER_ID, TEACHER_CODE, NAME, DEPARTMENT_ID
                  FROM TEACHER
                 WHERE DEPARTMENT_ID = ?
                 ORDER BY NAME
                """;
        List<Teacher> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Teacher(
                            rs.getInt("TEACHER_ID"),
                            rs.getString("TEACHER_CODE"),
                            rs.getString("NAME"),
                            rs.getInt("DEPARTMENT_ID")));
                }
            }
        }
        return rows;
    }

    @Override
    public List<Student> fetchStudentsByDepartment(Connection connection, int departmentId) throws SQLException {
        String sql = """
                SELECT STUDENT_ID, REGISTRATION_NO, NAME, DEPARTMENT_ID
                  FROM STUDENT
                 WHERE DEPARTMENT_ID = ?
                 ORDER BY NAME
                """;
        List<Student> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Student(
                            rs.getInt("STUDENT_ID"),
                            rs.getString("REGISTRATION_NO"),
                            rs.getString("NAME"),
                            rs.getInt("DEPARTMENT_ID")));
                }
            }
        }
        return rows;
    }

    @Override
    public List<Subject> fetchSubjectsByDepartment(Connection connection, int departmentId) throws SQLException {
        String sql = """
                SELECT SUBJECT_ID, SUBJECT_NAME, SUBJECT_CODE, SEMESTER, DEPARTMENT_ID
                  FROM SUBJECT
                 WHERE DEPARTMENT_ID = ?
                 ORDER BY SEMESTER, SUBJECT_NAME
                """;
        List<Subject> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Subject(
                            rs.getInt("SUBJECT_ID"),
                            rs.getString("SUBJECT_NAME"),
                            rs.getString("SUBJECT_CODE"),
                            rs.getInt("SEMESTER"),
                            rs.getInt("DEPARTMENT_ID")));
                }
            }
        }
        return rows;
    }

    @Override
    public List<Quiz> fetchQuizzesBySubject(Connection connection, int subjectId) throws SQLException {
        String sql = """
                SELECT QUIZ_ID, QUIZ_TITLE, DURATION_MINUTES, TOTAL_MARKS, QUIZ_DATE,
                       RESULTS_PUBLISHED, SUBJECT_ID, CREATED_BY
                  FROM QUIZ
                 WHERE SUBJECT_ID = ?
                 ORDER BY QUIZ_DATE DESC, QUIZ_ID DESC
                """;
        List<Quiz> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Quiz(
                            rs.getInt("QUIZ_ID"),
                            rs.getString("QUIZ_TITLE"),
                            rs.getInt("DURATION_MINUTES"),
                            rs.getDouble("TOTAL_MARKS"),
                            rs.getTimestamp("QUIZ_DATE").toLocalDateTime(),
                            rs.getString("RESULTS_PUBLISHED"),
                            rs.getInt("SUBJECT_ID"),
                            rs.getInt("CREATED_BY")));
                }
            }
        }
        return rows;
    }

    @Override
    public List<Question> fetchQuestionsByQuiz(Connection connection, int quizId) throws SQLException {
        String sql = """
                SELECT QUESTION_ID, QUESTION_TEXT, QUESTION_TYPE, MARKS, QUIZ_ID, DISPLAY_ORDER
                  FROM QUESTION
                 WHERE QUIZ_ID = ?
                 ORDER BY DISPLAY_ORDER
                """;
        List<Question> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Question(
                            rs.getInt("QUESTION_ID"),
                            rs.getString("QUESTION_TEXT"),
                            rs.getString("QUESTION_TYPE"),
                            rs.getDouble("MARKS"),
                            rs.getInt("QUIZ_ID"),
                            rs.getInt("DISPLAY_ORDER")));
                }
            }
        }
        return rows;
    }

    @Override
    public List<QuestionOption> fetchOptionsByQuestion(Connection connection, int questionId) throws SQLException {
        String sql = """
                SELECT OPTION_ID, OPTION_TEXT, IS_CORRECT, QUESTION_ID, DISPLAY_ORDER
                  FROM QUESTION_OPTION
                 WHERE QUESTION_ID = ?
                 ORDER BY DISPLAY_ORDER
                """;
        List<QuestionOption> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new QuestionOption(
                            rs.getInt("OPTION_ID"),
                            rs.getString("OPTION_TEXT"),
                            rs.getString("IS_CORRECT"),
                            rs.getInt("QUESTION_ID"),
                            rs.getInt("DISPLAY_ORDER")));
                }
            }
        }
        return rows;
    }

    @Override
    public Quiz findQuizById(Connection connection, int quizId) throws SQLException {
        String sql = """
                SELECT QUIZ_ID, QUIZ_TITLE, DURATION_MINUTES, TOTAL_MARKS, QUIZ_DATE,
                       RESULTS_PUBLISHED, SUBJECT_ID, CREATED_BY
                  FROM QUIZ
                 WHERE QUIZ_ID = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Quiz(
                            rs.getInt("QUIZ_ID"),
                            rs.getString("QUIZ_TITLE"),
                            rs.getInt("DURATION_MINUTES"),
                            rs.getDouble("TOTAL_MARKS"),
                            rs.getTimestamp("QUIZ_DATE").toLocalDateTime(),
                            rs.getString("RESULTS_PUBLISHED"),
                            rs.getInt("SUBJECT_ID"),
                            rs.getInt("CREATED_BY"));
                }
            }
        }
        return null;
    }

    @Override
    public Integer findAttemptId(Connection connection, int quizId, int studentId) throws SQLException {
        String sql = """
                SELECT ATTEMPT_ID
                  FROM QUIZ_ATTEMPT
                 WHERE QUIZ_ID = ?
                   AND STUDENT_ID = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ATTEMPT_ID");
                }
            }
        }
        return null;
    }

    @Override
    public Student findStudentByRegistrationNo(Connection connection, String registrationNo) throws SQLException {
        String sql = """
                SELECT STUDENT_ID, REGISTRATION_NO, NAME, DEPARTMENT_ID
                  FROM STUDENT
                 WHERE REGISTRATION_NO = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, registrationNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("STUDENT_ID"),
                            rs.getString("REGISTRATION_NO"),
                            rs.getString("NAME"),
                            rs.getInt("DEPARTMENT_ID"));
                }
            }
        }
        return null;
    }

    @Override
    public Teacher authenticateTeacher(Connection connection, String teacherCode, String password) throws SQLException {
        String sql = """
                SELECT TEACHER_ID, TEACHER_CODE, NAME, DEPARTMENT_ID
                  FROM TEACHER
                 WHERE UPPER(TRIM(TEACHER_CODE)) = UPPER(TRIM(?))
                   AND PASSWORD = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, teacherCode);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Teacher(
                            rs.getInt("TEACHER_ID"),
                            rs.getString("TEACHER_CODE"),
                            rs.getString("NAME"),
                            rs.getInt("DEPARTMENT_ID"));
                }
            }
        }
        return null;
    }

    @Override
    public Student authenticateStudent(Connection connection, String registrationNo, String password) throws SQLException {
        String sql = """
                SELECT STUDENT_ID, REGISTRATION_NO, NAME, DEPARTMENT_ID
                  FROM STUDENT
                 WHERE REGISTRATION_NO = ?
                   AND PASSWORD = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, registrationNo);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("STUDENT_ID"),
                            rs.getString("REGISTRATION_NO"),
                            rs.getString("NAME"),
                            rs.getInt("DEPARTMENT_ID"));
                }
            }
        }
        return null;
    }

    @Override
    public Integer findSelectedOptionId(Connection connection, int attemptId, int questionId) throws SQLException {
        String sql = """
                SELECT SELECTED_OPTION_ID
                  FROM STUDENT_ANSWER
                 WHERE ATTEMPT_ID = ?
                   AND QUESTION_ID = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            ps.setInt(2, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Object value = rs.getObject("SELECTED_OPTION_ID");
                    return value == null ? null : rs.getInt("SELECTED_OPTION_ID");
                }
            }
        }
        return null;
    }

    @Override
    public String findAttemptStatus(Connection connection, int quizId, int studentId) throws SQLException {
        String sql = """
                SELECT STATUS
                  FROM QUIZ_ATTEMPT
                 WHERE QUIZ_ID = ?
                   AND STUDENT_ID = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("STATUS");
                }
            }
        }
        return null;
    }

    private void setAdminCleanupMode(Connection connection, boolean enabled) throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{ call PKG_QUIZ_ADMIN.SET_ADMIN_CLEANUP_MODE(?) }")) {
            cs.setInt(1, enabled ? 1 : 0);
            cs.execute();
        }
    }

    private int nextSequenceValue(Connection connection, String sequenceName) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT " + sequenceName + ".NEXTVAL FROM DUAL");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Unable to fetch next value for sequence " + sequenceName);
    }

    private void deleteQuizGraph(Connection connection, int quizId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM ANSWER_AUTOSAVE_LOG WHERE ATTEMPT_ID IN (SELECT ATTEMPT_ID FROM QUIZ_ATTEMPT WHERE QUIZ_ID = ?)")) {
            ps.setInt(1, quizId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM STUDENT_ANSWER WHERE ATTEMPT_ID IN (SELECT ATTEMPT_ID FROM QUIZ_ATTEMPT WHERE QUIZ_ID = ?)")) {
            ps.setInt(1, quizId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM QUIZ_ATTEMPT WHERE QUIZ_ID = ?")) {
            ps.setInt(1, quizId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM QUESTION_OPTION WHERE QUESTION_ID IN (SELECT QUESTION_ID FROM QUESTION WHERE QUIZ_ID = ?)")) {
            ps.setInt(1, quizId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM QUESTION WHERE QUIZ_ID = ?")) {
            ps.setInt(1, quizId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM RESULT_PUBLISH_LOG WHERE QUIZ_ID = ?")) {
            ps.setInt(1, quizId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM QUIZ WHERE QUIZ_ID = ?")) {
            ps.setInt(1, quizId);
            ps.executeUpdate();
        }
    }
}
