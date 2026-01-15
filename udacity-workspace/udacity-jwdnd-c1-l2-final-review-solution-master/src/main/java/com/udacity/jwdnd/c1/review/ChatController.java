package com.udacity.jwdnd.c1.review;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

  private final MessageService messageService;

  @ModelAttribute("allMessagesTypes")
  public String[] allMessagesTypes() {
    return new String[]{"Say","Shout","Whisper"};
  }

  public ChatController(MessageService messageService) {
    this.messageService = messageService;
  }

  @GetMapping
  public String chat(@ModelAttribute("chatForm") ChatForm chatForm, Model model) {
    model.addAttribute("chatForm", chatForm);
    return "chat";
  }

  @PostMapping
  public String sendMessage(@ModelAttribute("chatForm") ChatForm chatForm, Model model) {
    messageService.addMessage(chatForm);
    chatForm.setMessage("");
    chatForm.setUsername("");
    model.addAttribute("chats", messageService.getChatMessages());
    return "chat";
  }

}
