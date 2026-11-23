package com.college.library.service;

import com.college.library.dto.StudentRequest;
import com.college.library.dto.StudentResponse;
import com.college.library.entity.IssueStatus;
import com.college.library.entity.Student;
import com.college.library.exception.BusinessRuleException;
import com.college.library.exception.DuplicateResourceException;
import com.college.library.exception.ResourceNotFoundException;
import com.college.library.repository.BookIssueRepository;
import com.college.library.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final BookIssueRepository issueRepository;

    @Transactional
    public StudentResponse registerStudent(StudentRequest req) {
        if (studentRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Student with email " + req.getEmail() + " already exists");
        }
        Student student = Student.builder()
                .studentName(req.getStudentName())
                .email(req.getEmail())
                .department(req.getDepartment())
                .phoneNumber(req.getPhoneNumber())
                .build();
        return StudentResponse.from(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream().map(StudentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        return StudentResponse.from(findStudent(id));
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest req) {
        Student student = findStudent(id);
        if (studentRepository.existsByEmailAndStudentIdNot(req.getEmail(), id)) {
            throw new DuplicateResourceException("Student with email " + req.getEmail() + " already exists");
        }
        student.setStudentName(req.getStudentName());
        student.setEmail(req.getEmail());
        student.setDepartment(req.getDepartment());
        student.setPhoneNumber(req.getPhoneNumber());
        return StudentResponse.from(studentRepository.save(student));
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = findStudent(id);
        if (issueRepository.existsByStudentStudentIdAndStatus(id, IssueStatus.ISSUED)) {
            throw new BusinessRuleException("Cannot delete a student who still has unreturned books");
        }
        studentRepository.delete(student);
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
    }
}
