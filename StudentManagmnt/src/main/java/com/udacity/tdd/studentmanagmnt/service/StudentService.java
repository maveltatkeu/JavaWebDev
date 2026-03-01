package com.udacity.tdd.studentmanagmnt.service;


import com.udacity.tdd.studentmanagmnt.dto.PaginatedResponse;
import com.udacity.tdd.studentmanagmnt.dto.StudentRequest;
import com.udacity.tdd.studentmanagmnt.dto.StudentResponse;
import com.udacity.tdd.studentmanagmnt.exception.DuplicateEmailException;
import com.udacity.tdd.studentmanagmnt.exception.StudentNotFoundException;
import com.udacity.tdd.studentmanagmnt.model.Student;
import com.udacity.tdd.studentmanagmnt.model.StudentStatus;
import com.udacity.tdd.studentmanagmnt.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository studentRepository;

  @Transactional(readOnly = true)
  public PaginatedResponse<StudentResponse> getAllStudents(int page, int size, String sortBy, String direction) {
    log.debug("Fetching students page {} with size {}", page, size);

    Sort sort = direction.equalsIgnoreCase("desc")
        ? Sort.by(sortBy).descending()
        : Sort.by(sortBy).ascending();

    Pageable pageable = PageRequest.of(page, size, sort);
    Page<Student> studentPage = studentRepository.findAll(pageable);

    return mapToPaginatedResponse(studentPage);
  }

  @Transactional(readOnly = true)
  public StudentResponse getStudentById(Long id) {
    log.debug("Fetching student with id: {}", id);
    return studentRepository.findById(id)
        .map(this::mapToResponse)
        .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
  }

  @Transactional(readOnly = true)
  public StudentResponse getStudentByEmail(String email) {
    log.debug("Fetching student with email: {}", email);
    return studentRepository.findByEmail(email)
        .map(this::mapToResponse)
        .orElseThrow(() -> new StudentNotFoundException("Student not found with email: " + email));
  }

  @Transactional
  public StudentResponse createStudent(StudentRequest request) {
    log.info("Creating new student with email: {}", request.email());

    validateUniqueConstraints(request.email(), request.studentNumber(), null);

    Student student = new Student();
    updateStudentFromRequest(student, request);

    Student saved = studentRepository.save(student);
    log.info("Created student with id: {}", saved.getId());

    return mapToResponse(saved);
  }

  @Transactional
  public StudentResponse updateStudent(Long id, StudentRequest request) {
    log.info("Updating student with id: {}", id);

    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

    validateUniqueConstraints(request.email(), request.studentNumber(), id);

    updateStudentFromRequest(student, request);
    Student updated = studentRepository.save(student);

    log.info("Updated student with id: {}", updated.getId());
    return mapToResponse(updated);
  }

  @Transactional
  public void deleteStudent(Long id) {
    log.info("Deleting student with id: {}", id);

    if (!studentRepository.existsById(id)) {
      throw new StudentNotFoundException("Student not found with id: " + id);
    }

    studentRepository.deleteById(id);
    log.info("Deleted student with id: {}", id);
  }

  @Transactional
  public StudentResponse updateStudentStatus(Long id, StudentStatus status) {
    log.info("Updating status to {} for student id: {}", status, id);

    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

    student.setStatus(status);
    return mapToResponse(studentRepository.save(student));
  }

  @Transactional(readOnly = true)
  public PaginatedResponse<StudentResponse> searchStudents(String query, int page, int size) {
    log.debug("Searching students with query: {}", query);

    Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
    Page<Student> results = studentRepository.searchStudents(query, pageable);

    return mapToPaginatedResponse(results);
  }

  // Private helper methods

  private void validateUniqueConstraints(String email, String studentNumber, Long excludeId) {
    studentRepository.findByEmail(email).ifPresent(existing -> {
      if (!existing.getId().equals(excludeId)) {
        throw new DuplicateEmailException("Email already registered: " + email);
      }
    });

    studentRepository.findByStudentNumber(studentNumber).ifPresent(existing -> {
      if (!existing.getId().equals(excludeId)) {
        throw new DuplicateEmailException("Student number already exists: " + studentNumber);
      }
    });
  }

  private void updateStudentFromRequest(Student student, StudentRequest request) {
    student.setFirstName(request.firstName().trim());
    student.setLastName(request.lastName().trim());
    student.setEmail(request.email().toLowerCase().trim());
    student.setStudentNumber(request.studentNumber().toUpperCase());
    student.setDateOfBirth(request.dateOfBirth());
    student.setPhoneNumber(request.phoneNumber());
    student.setMajor(request.major().trim());
  }

  private StudentResponse mapToResponse(Student student) {
    return new StudentResponse(
        student.getId(),
        student.getFirstName(),
        student.getLastName(),
        student.getEmail(),
        student.getStudentNumber(),
        student.getDateOfBirth(),
        student.getPhoneNumber(),
        student.getStatus(),
        student.getMajor(),
        student.getCreatedAt(),
        student.getUpdatedAt()
    );
  }

  private PaginatedResponse<StudentResponse> mapToPaginatedResponse(Page<Student> page) {
    List<StudentResponse> content = page.getContent().stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());

    return new PaginatedResponse<>(
        content,
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast(),
        page.hasNext(),
        page.hasPrevious()
    );
  }
}