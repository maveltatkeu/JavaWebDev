package com.udacity.tdd.swagger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private List<String> categories;

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
}