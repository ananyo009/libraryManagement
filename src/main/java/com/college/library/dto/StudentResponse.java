package com.college.library.dto;

import com.college.library.entity.Student;

public record StudentResponse(Long studentId, String studentName, String email,
                              String department, String phoneNumber) {

    public static StudentResponse from(Student s) {
        return new StudentResponse(s.getStudentId(), s.getStudentName(), s.getEmail(),
                s.getDepartment(), s.getPhoneNumber());
    }
}
