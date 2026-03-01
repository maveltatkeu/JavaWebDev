package com.udacity.tdd.livecodingtdd;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Job API",
        description = "Display APIs, to handle Jobs",
        version = "v1"
    )
)
public class LiveCodingTddApplication {

  public static void main(String[] args) {
    SpringApplication.run(LiveCodingTddApplication.class, args);
  }

}
