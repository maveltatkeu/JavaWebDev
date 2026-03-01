package com.udacity.tdd.studentmanagmnt.controller;

import com.udacity.tdd.studentmanagmnt.dto.PaginatedResponse;
import com.udacity.tdd.studentmanagmnt.dto.StudentRequest;
import com.udacity.tdd.studentmanagmnt.dto.StudentResponse;
import com.udacity.tdd.studentmanagmnt.model.StudentStatus;
import com.udacity.tdd.studentmanagmnt.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Validated
@Tag(name = "Student Management", description = "Operations for managing students")
public class StudentController {

  private final StudentService studentService;

  @GetMapping
  @Operation(summary = "Get all students with pagination")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successfully retrieved students"),
      @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
  })
  public ResponseEntity<PaginatedResponse<StudentResponse>> getAllStudents(
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
      @RequestParam(defaultValue = "lastName") String sortBy,
      @RequestParam(defaultValue = "asc") String direction) {

    log.info("GET /api/v1/students?page={}&size={}", page, size);
    return ResponseEntity.ok(studentService.getAllStudents(page, size, sortBy, direction));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get student by ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Student found"),
      @ApiResponse(responseCode = "404", description = "Student not found")
  })
  public ResponseEntity<StudentResponse> getStudentById(
      @PathVariable @Parameter(description = "Student ID") Long id) {

    log.info("GET /api/v1/students/{}", id);
    return ResponseEntity.ok(studentService.getStudentById(id));
  }

  @GetMapping("/search")
  @Operation(summary = "Search students by keyword")
  public ResponseEntity<PaginatedResponse<StudentResponse>> searchStudents(
      @RequestParam @Parameter(description = "Search query") String query,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

    log.info("GET /api/v1/students/search?q={}", query);
    return ResponseEntity.ok(studentService.searchStudents(query, page, size));
  }

  @PostMapping
  @Operation(summary = "Create a new student")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Student created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "409", description = "Email or student number already exists")
  })
  public ResponseEntity<StudentResponse> createStudent(
      @Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Student data",
          required = true,
          content = @Content(schema = @Schema(implementation = StudentRequest.class))
      ) StudentRequest request) {

    log.info("POST /api/v1/students - Creating student: {}", request.email());
    StudentResponse created = studentService.createStudent(request);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(created.id())
        .toUri();

    return ResponseEntity.created(location).body(created);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update an existing student")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Student updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Student not found"),
      @ApiResponse(responseCode = "409", description = "Email or student number already exists")
  })
  public ResponseEntity<StudentResponse> updateStudent(
      @PathVariable Long id,
      @Valid @RequestBody StudentRequest request) {

    log.info("PUT /api/v1/students/{} - Updating student", id);
    return ResponseEntity.ok(studentService.updateStudent(id, request));
  }

  @PatchMapping("/{id}/status")
  @Operation(summary = "Update student status")
  public ResponseEntity<StudentResponse> updateStatus(
      @PathVariable Long id,
      @RequestParam StudentStatus status) {

    log.info("PATCH /api/v1/students/{}/status - New status: {}", id, status);
    return ResponseEntity.ok(studentService.updateStudentStatus(id, status));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a student")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Student not found")
  })
  public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
    log.info("DELETE /api/v1/students/{}", id);
    studentService.deleteStudent(id);
    return ResponseEntity.noContent().build();
  }
}
