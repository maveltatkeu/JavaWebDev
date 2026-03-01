package com.udacity.tdd.studentmanagmnt.validation;


import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.time.LocalDate;
import java.time.Period;

@Documented
@Constraint(validatedBy = ValidStudentAge.Validator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStudentAge {
  String message() default "Invalid age";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
  int min() default 0;
  int max() default 150;

  class Validator implements ConstraintValidator<ValidStudentAge, LocalDate> {
    private int min;
    private int max;

    @Override
    public void initialize(ValidStudentAge constraintAnnotation) {
      this.min = constraintAnnotation.min();
      this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
      if (dateOfBirth == null) return true;
      int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
      return age >= min && age <= max;
    }
  }
}