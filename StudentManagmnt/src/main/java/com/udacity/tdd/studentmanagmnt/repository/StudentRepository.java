package com.udacity.tdd.studentmanagmnt.repository;


import com.udacity.tdd.studentmanagmnt.model.Student;
import com.udacity.tdd.studentmanagmnt.model.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

  Optional<Student> findByEmail(String email);

  Optional<Student> findByStudentNumber(String studentNumber);

  boolean existsByEmail(String email);

  boolean existsByStudentNumber(String studentNumber);

  @Query("SELECT s FROM Student s WHERE " +
      "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
      "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
      "LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
      "LOWER(s.studentNumber) LIKE LOWER(CONCAT('%', :search, '%'))")
  Page<Student> searchStudents(@Param("search") String search, Pageable pageable);

  @Query("SELECT s FROM Student s WHERE s.status = :status")
  Page<Student> findByStatus(StudentStatus status, Pageable pageable);
}