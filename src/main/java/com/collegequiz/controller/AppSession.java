package com.collegequiz.controller;

import com.collegequiz.model.Student;
import com.collegequiz.model.Teacher;

public final class AppSession {
    private static Teacher loggedInTeacher;
    private static Integer loggedInStudentId;
    private static String loggedInRole;
    private static Student loggedInStudent;
    private static String loggedInAdminUsername;

    private AppSession() {
    }

    public static void setTeacherSession(Teacher teacher) {
        loggedInTeacher = teacher;
        loggedInStudentId = null;
        loggedInRole = "TEACHER";
        loggedInStudent = null;
    }

    public static void setStudentSession(int studentId) {
        loggedInStudentId = studentId;
        loggedInTeacher = null;
        loggedInRole = "STUDENT";
        loggedInStudent = null;
    }

    public static void setStudentSession(Student student) {
        loggedInStudentId = student.studentId();
        loggedInTeacher = null;
        loggedInRole = "STUDENT";
        loggedInStudent = student;
        loggedInAdminUsername = null;
    }

    public static void setAdminSession(String username) {
        loggedInTeacher = null;
        loggedInStudentId = null;
        loggedInStudent = null;
        loggedInRole = "ADMIN";
        loggedInAdminUsername = username;
    }

    public static Integer getLoggedInTeacherId() {
        return loggedInTeacher == null ? null : loggedInTeacher.teacherId();
    }

    public static Teacher getLoggedInTeacher() {
        return loggedInTeacher;
    }

    public static Integer getLoggedInStudentId() {
        return loggedInStudentId;
    }

    public static String getLoggedInRole() {
        return loggedInRole;
    }

    public static Student getLoggedInStudent() {
        return loggedInStudent;
    }

    public static String getLoggedInAdminUsername() {
        return loggedInAdminUsername;
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(loggedInRole);
    }

    public static void clear() {
        loggedInTeacher = null;
        loggedInStudentId = null;
        loggedInRole = null;
        loggedInStudent = null;
        loggedInAdminUsername = null;
    }
}
