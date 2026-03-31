package com.collegequiz.service;

import com.collegequiz.model.StudentResultRow;
import com.collegequiz.model.Department;
import com.collegequiz.model.Question;
import com.collegequiz.model.QuestionOption;
import com.collegequiz.model.Quiz;
import com.collegequiz.model.Student;
import com.collegequiz.model.Subject;
import com.collegequiz.model.Teacher;
import com.collegequiz.model.TeacherDashboardRow;
import com.collegequiz.model.Teacher;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizManagementService {
    Integer createSubject(String subjectName, String subjectCode, int semester, int departmentId);

    Integer createQuiz(String quizTitle, int durationMinutes, LocalDateTime quizDate, int subjectId, int createdBy);

    Integer addQuestion(int quizId, String questionText, double marks, int displayOrder);

    Integer addOption(int questionId, String optionText, String isCorrect, int displayOrder);

    Integer addQuestionWithOptions(int quizId, String questionText, double marks, int displayOrder,
                                   List<String> options, int correctIndex);

    Integer startAttempt(int quizId, int studentId);

    void autosaveAnswer(int attemptId, int questionId, int selectedOptionId);

    void clearAnswer(int attemptId, int questionId);

    double submitAttempt(int attemptId);

    void publishResults(int quizId, int teacherId);

    void unpublishResults(int quizId, int teacherId);

    List<TeacherDashboardRow> getTeacherDashboard(int teacherId);

    List<StudentResultRow> getPublishedResults(int studentId);

    List<Department> getDepartments();

    List<Teacher> getTeachersByDepartment(int departmentId);

    List<Student> getStudentsByDepartment(int departmentId);

    List<Subject> getSubjectsByDepartment(int departmentId);

    List<Quiz> getQuizzesBySubject(int subjectId);

    List<Question> getQuestionsByQuiz(int quizId);

    List<QuestionOption> getOptionsByQuestion(int questionId);

    Quiz getQuizById(int quizId);

    Integer getAttemptId(int quizId, int studentId);

    Student findStudentByRegistrationNo(String registrationNo);

    Teacher authenticateTeacher(String teacherCode, String password);

    Student authenticateStudent(String registrationNo, String password);

    Integer getSelectedOptionId(int attemptId, int questionId);

    String getAttemptStatus(int quizId, int studentId);
}
