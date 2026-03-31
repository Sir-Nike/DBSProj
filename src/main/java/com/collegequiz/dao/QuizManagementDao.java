package com.collegequiz.dao;

import com.collegequiz.model.StudentResultRow;
import com.collegequiz.model.Department;
import com.collegequiz.model.Question;
import com.collegequiz.model.QuestionOption;
import com.collegequiz.model.Quiz;
import com.collegequiz.model.Student;
import com.collegequiz.model.Subject;
import com.collegequiz.model.Teacher;
import com.collegequiz.model.TeacherDashboardRow;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface QuizManagementDao {
    Integer createSubject(Connection connection, String subjectName, String subjectCode, int semester, int departmentId)
            throws SQLException;

    Integer createQuiz(Connection connection, String quizTitle, int durationMinutes, java.time.LocalDateTime quizDate,
                       int subjectId, int createdBy) throws SQLException;

    Integer addQuestion(Connection connection, int quizId, String questionText, double marks, int displayOrder)
            throws SQLException;

    Integer addOption(Connection connection, int questionId, String optionText, String isCorrect, int displayOrder)
            throws SQLException;

    Integer startAttempt(Connection connection, int quizId, int studentId) throws SQLException;

    void autosaveAnswer(Connection connection, int attemptId, int questionId, int selectedOptionId) throws SQLException;

    void clearAnswer(Connection connection, int attemptId, int questionId) throws SQLException;

    double submitAttempt(Connection connection, int attemptId) throws SQLException;

    void publishResults(Connection connection, int quizId, int teacherId) throws SQLException;

    void unpublishResults(Connection connection, int quizId, int teacherId) throws SQLException;

    List<TeacherDashboardRow> fetchTeacherDashboard(Connection connection, int teacherId) throws SQLException;

    List<StudentResultRow> fetchPublishedResults(Connection connection, int studentId) throws SQLException;

    List<Department> fetchDepartments(Connection connection) throws SQLException;

    List<Teacher> fetchTeachersByDepartment(Connection connection, int departmentId) throws SQLException;

    List<Student> fetchStudentsByDepartment(Connection connection, int departmentId) throws SQLException;

    List<Subject> fetchSubjectsByDepartment(Connection connection, int departmentId) throws SQLException;

    List<Quiz> fetchQuizzesBySubject(Connection connection, int subjectId) throws SQLException;

    List<Question> fetchQuestionsByQuiz(Connection connection, int quizId) throws SQLException;

    List<QuestionOption> fetchOptionsByQuestion(Connection connection, int questionId) throws SQLException;

    Quiz findQuizById(Connection connection, int quizId) throws SQLException;

    Integer findAttemptId(Connection connection, int quizId, int studentId) throws SQLException;

    Student findStudentByRegistrationNo(Connection connection, String registrationNo) throws SQLException;

    Teacher authenticateTeacher(Connection connection, String teacherCode, String password) throws SQLException;

    Student authenticateStudent(Connection connection, String registrationNo, String password) throws SQLException;

    Integer findSelectedOptionId(Connection connection, int attemptId, int questionId) throws SQLException;

    String findAttemptStatus(Connection connection, int quizId, int studentId) throws SQLException;
}
