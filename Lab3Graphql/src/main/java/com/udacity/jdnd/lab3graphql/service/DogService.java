package com.udacity.jdnd.lab3graphql.service;

import com.udacity.jdnd.lab3graphql.entity.Dog;

import java.util.List;

public interface DogService {
  List<Dog> findAllDogs();

  Dog findDogById(Long id);

  List<String> findAllDogNames();

  boolean deleteDogBreed(Long id);

  Dog updateDogName(String newName, Long id);
}
