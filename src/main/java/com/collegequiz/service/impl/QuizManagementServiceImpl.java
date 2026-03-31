package com.collegequiz.service.impl;

import com.collegequiz.dao.QuizManagementDao;
import com.collegequiz.dao.impl.QuizManagementDaoImpl;
import com.collegequiz.exception.DatabaseConnectionException;
import com.collegequiz.exception.ServiceException;
import com.collegequiz.model.Department;
import com.collegequiz.model.Question;
import com.collegequiz.model.QuestionOption;
import com.collegequiz.model.Quiz;
import com.collegequiz.model.Student;
import com.collegequiz.model.Subject;
import com.collegequiz.model.Teacher;
import com.collegequiz.model.StudentResultRow;
import com.collegequiz.model.TeacherDashboardRow;
import com.collegequiz.service.QuizManagementService;
import com.collegequiz.util.DbUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class QuizManagementServiceImpl implements QuizManagementService {
    private final QuizManagementDao dao;

    public QuizManagementServiceImpl() {
        this(new QuizManagementDaoImpl());
    }

    public QuizManagementServiceImpl(QuizManagementDao dao) {
        this.dao = dao;
    }

    private <T> T inTransaction(SqlWork<T> work) {
        try (Connection connection = DbUtil.getConnection()) {
            connection.setAutoCommit(false);
            try {
                T result = work.apply(connection);
                connection.commit();
                return result;
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                if (ex instanceof SQLException sqlException) {
                    throw new ServiceException("Database operation failed.", sqlException);
                }
                throw ex;
            }
        } catch (SQLException ex) {
            throw new DatabaseConnectionException("No database connectivity.", ex);
        }
    }

    @Override
    public Integer createSubject(String subjectName, String subjectCode, int semester, int departmentId) {
        return inTransaction(connection -> dao.createSubject(connection, subjectName, subjectCode, semester, departmentId));
    }

    @Override
    public Integer createQuiz(String quizTitle, int durationMinutes, LocalDateTime quizDate, int subjectId, int createdBy) {
        return inTransaction(connection -> dao.createQuiz(connection, quizTitle, durationMinutes, quizDate, subjectId, createdBy));
    }

    @Override
    public Integer addQuestion(int quizId, String questionText, double marks, int displayOrder) {
        return inTransaction(connection -> dao.addQuestion(connection, quizId, questionText, marks, displayOrder));
    }

    @Override
    public Integer addOption(int questionId, String optionText, String isCorrect, int displayOrder) {
        return inTransaction(connection -> dao.addOption(connection, questionId, optionText, isCorrect, displayOrder));
    }

    @Override
    public Integer startAttempt(int quizId, int studentId) {
        return inTransaction(connection -> dao.startAttempt(connection, quizId, studentId));
    }

    @Override
    public void autosaveAnswer(int attemptId, int questionId, int selectedOptionId) {
        inTransaction(connection -> {
            dao.autosaveAnswer(connection, attemptId, questionId, selectedOptionId);
            return null;
        });
    }

    @Override
    public void clearAnswer(int attemptId, int questionId) {
        inTransaction(connection -> {
            dao.clearAnswer(connection, attemptId, questionId);
            return null;
        });
    }

    @Override
    public double submitAttempt(int attemptId) {
        return inTransaction(connection -> dao.submitAttempt(connection, attemptId));
    }

    @Override
    public void publishResults(int quizId, int teacherId) {
        inTransaction(connection -> {
            dao.publishResults(connection, quizId, teacherId);
            return null;
        });
    }

    @Override
    public void unpublishResults(int quizId, int teacherId) {
        inTransaction(connection -> {
            dao.unpublishResults(connection, quizId, teacherId);
            return null;
        });
    }

    @Override
    public List<TeacherDashboardRow> getTeacherDashboard(int teacherId) {
        return inTransaction(connection -> dao.fetchTeacherDashboard(connection, teacherId));
    }

    @Override
    public List<StudentResultRow> getPublishedResults(int studentId) {
        return inTransaction(connection -> dao.fetchPublishedResults(connection, studentId));
    }

    @Override
    public List<Department> getDepartments() {
        return inTransaction(dao::fetchDepartments);
    }

    @Override
    public List<Teacher> getTeachersByDepartment(int departmentId) {
        return inTransaction(connection -> dao.fetchTeachersByDepartment(connection, departmentId));
    }

    @Override
    public List<Student> getStudentsByDepartment(int departmentId) {
        return inTransaction(connection -> dao.fetchStudentsByDepartment(connection, departmentId));
    }

    @Override
    public List<Subject> getSubjectsByDepartment(int departmentId) {
        return inTransaction(connection -> dao.fetchSubjectsByDepartment(connection, departmentId));
    }

    @Override
    public List<Quiz> getQuizzesBySubject(int subjectId) {
        return inTransaction(connection -> dao.fetchQuizzesBySubject(connection, subjectId));
    }

    @Override
    public List<Question> getQuestionsByQuiz(int quizId) {
        return inTransaction(connection -> dao.fetchQuestionsByQuiz(connection, quizId));
    }

    @Override
    public List<QuestionOption> getOptionsByQuestion(int questionId) {
        return inTransaction(connection -> dao.fetchOptionsByQuestion(connection, questionId));
    }

    @Override
    public Quiz getQuizById(int quizId) {
        return inTransaction(connection -> dao.findQuizById(connection, quizId));
    }

    @Override
    public Integer getAttemptId(int quizId, int studentId) {
        return inTransaction(connection -> dao.findAttemptId(connection, quizId, studentId));
    }

    @Override
    public Student findStudentByRegistrationNo(String registrationNo) {
        return inTransaction(connection -> dao.findStudentByRegistrationNo(connection, registrationNo));
    }

    @Override
    public Teacher authenticateTeacher(String teacherCode, String password) {
        return inTransaction(connection -> dao.authenticateTeacher(connection, teacherCode, password));
    }

    @Override
    public Student authenticateStudent(String registrationNo, String password) {
        return inTransaction(connection -> dao.authenticateStudent(connection, registrationNo, password));
    }

    @Override
    public Integer getSelectedOptionId(int attemptId, int questionId) {
        return inTransaction(connection -> dao.findSelectedOptionId(connection, attemptId, questionId));
    }

    @Override
    public String getAttemptStatus(int quizId, int studentId) {
        return inTransaction(connection -> dao.findAttemptStatus(connection, quizId, studentId));
    }

    @FunctionalInterface
    private interface SqlWork<T> {
        T apply(Connection connection) throws SQLException;
    }
}
