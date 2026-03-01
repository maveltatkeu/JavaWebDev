package com.udacity.jdnd.livespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LiveSpringApplication {

  public static void main(String[] args) {
    SpringApplication.run(LiveSpringApplication.class, args);
  }

}
