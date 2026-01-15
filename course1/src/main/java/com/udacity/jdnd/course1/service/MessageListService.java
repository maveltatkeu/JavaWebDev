package com.udacity.jdnd.course1.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageListService {

  private List<String> messages;

  @PostConstruct
  public void postConstruct() {
    this.messages = new ArrayList<>();
  }

  public void addMessage(String message) {
    messages.add(message);
  }

  public List<String> getMessages() {
    return new ArrayList<>(this.messages);
  }
}