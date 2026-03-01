package com.kora.batch.events.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.batch")
public class BatchConfigProperties {

  private Thresholds thresholds = new Thresholds();
  private List<String> categories;

  // Getters and Setters for top level
  public Thresholds getThresholds() {
    return thresholds;
  }

  public void setThresholds(Thresholds thresholds) {
    this.thresholds = thresholds;
  }

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public static class Thresholds {
    private double juniorMax;
    private double middleMax;
    private double seniorMax;

    // Getters and Setters
    public double getJuniorMax() {
      return juniorMax;
    }

    public void setJuniorMax(double juniorMax) {
      this.juniorMax = juniorMax;
    }

    public double getMiddleMax() {
      return middleMax;
    }

    public void setMiddleMax(double middleMax) {
      this.middleMax = middleMax;
    }

    public double getSeniorMax() {
      return seniorMax;
    }

    public void setSeniorMax(double seniorMax) {
      this.seniorMax = seniorMax;
    }
  }
}