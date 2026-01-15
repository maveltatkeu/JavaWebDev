package com.udacity.jdnd.course1.controller;

import com.udacity.jdnd.course1.model.AnimalForm;
import com.udacity.jdnd.course1.model.MessageForm;
import com.udacity.jdnd.course1.service.MessageListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.util.Date;

@Controller
@RequestMapping("/v1")
public class HomeController {

  private final MessageListService messageListService;

  public HomeController(MessageListService messageListService) {
    this.messageListService = messageListService;
  }

//  @GetMapping
//  public String getHome(@ModelAttribute MessageForm newMessage, Model model) {
//    model.addAttribute("title", "Home");
//    model.addAttribute("message", "Hello, Welcome To PDM!");
//    model.addAttribute("date", new Date().toString());
//    model.addAttribute("time", Instant.now().toString());
//    model.addAttribute("greetings", this.messageListService.getMessages());
//    return "home";
//  }

  @GetMapping("/home")
  public String getHomePage(@ModelAttribute("newMessage") MessageForm newMessage, Model model) {
    model.addAttribute("greetings", this.messageListService.getMessages());
    return "home";
  }

  @PostMapping("/home")
  public String addMessage(@ModelAttribute("newMessage") MessageForm messageForm, Model model) {
    messageListService.addMessage(messageForm.getText());
    model.addAttribute("greetings", messageListService.getMessages());
    messageForm.setText("");
    return "home";
  }

  @GetMapping("/animal")
  public String getAnimalHomePage(@ModelAttribute("animalForm") AnimalForm animalForm, Model model) {
    model.addAttribute("greetings", this.messageListService.getMessages());
    return "animal";
  }

  @PostMapping("/animal")
  public String addAnimal(@ModelAttribute("animalForm") AnimalForm animalForm, Model model) {
    messageListService.addMessage("Name: "+animalForm.getAnimalName()+" Description: "+animalForm.getAdjective());
    model.addAttribute("greetings", messageListService.getMessages());
    animalForm.setAnimalName("");
    animalForm.setAdjective("");
    return "animal";
  }
}
