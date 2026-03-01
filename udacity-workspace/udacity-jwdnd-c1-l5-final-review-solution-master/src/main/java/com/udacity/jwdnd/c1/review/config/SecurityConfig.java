package com.udacity.jwdnd.c1.review.config;

import com.udacity.jwdnd.c1.review.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationService authenticationService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(httpForm -> {
          httpForm.loginPage("/login").permitAll();
          httpForm.defaultSuccessUrl("/chat");

        })
        .authorizeHttpRequests(registry -> {
          registry.requestMatchers("/signup", "/css/**", "/js/**").permitAll();
          registry.anyRequest().authenticated();
        })
        .authenticationProvider(authenticationService)
        .build();
  }
}


