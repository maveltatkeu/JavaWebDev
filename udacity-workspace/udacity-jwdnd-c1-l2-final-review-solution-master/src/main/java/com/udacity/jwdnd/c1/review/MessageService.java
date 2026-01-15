package com.udacity.jwdnd.c1.review;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

  private static final Logger log = LoggerFactory.getLogger(MessageService.class);
  private List<ChatMessage> chatMessages;

  public List<ChatMessage> getChatMessages() {
    return chatMessages;
  }

  public void addMessage(ChatForm chatForm) {
    ChatMessage chatMessage = new ChatMessage();

    chatMessage.setSender(chatForm.getUsername());

    switch (chatForm.getMessageType()) {
      case "Say" -> chatMessage.setChatMessage(chatForm.getMessage());
      case "Shout" -> chatMessage.setChatMessage(chatForm.getMessage().toUpperCase());
      case "Whisper" -> chatMessage.setChatMessage(chatForm.getMessage().toLowerCase());
      default -> chatMessage.setChatMessage(chatForm.getMessage());
    }
    chatMessages.add(chatMessage);
    log.info("Added a new message to the chat messages list: {}", chatMessage);
  }

  @PostConstruct
  public void postConstruct() {
    System.out.println("Creating MessageService bean");
    this.chatMessages = new ArrayList<>();
  }
}
