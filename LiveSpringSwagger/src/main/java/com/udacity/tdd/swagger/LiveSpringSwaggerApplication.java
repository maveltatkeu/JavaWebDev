package com.udacity.tdd.swagger;

import com.udacity.tdd.swagger.config.AppConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@OpenAPIDefinition(
    info = @Info(
        title = "Student API",
        version = "1.0",
        description = "API documentation for managing students"
    )
)
@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class LiveSpringSwaggerApplication {

  private static AppConfig appConfig = null;

  public LiveSpringSwaggerApplication(AppConfig appConfig) {
    this.appConfig = appConfig;
  }

  public static void main(String[] args) {
    SpringApplication.run(LiveSpringSwaggerApplication.class, args);

    System.out.println(appConfig.getCategories().contains("at"));
    System.out.println(appConfig);
  }

}
