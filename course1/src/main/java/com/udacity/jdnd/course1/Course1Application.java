package com.udacity.jdnd.course1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Course1Application {

  public static void main(String[] args) {
    SpringApplication.run(Course1Application.class, args);
  }

  @Bean
  public String sayHello() {
    System.out.println("Hello World = Beans");
    return "Hello World!";
  }
}
