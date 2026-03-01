package com.udacity.jdnd.lab3graphql.service;

import com.udacity.jdnd.lab3graphql.entity.Dog;
import com.udacity.jdnd.lab3graphql.repository.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DogServiceImpl implements DogService {

  @Autowired
  DogRepository repository;

  @Override
  public List<Dog> findAllDogs() {
    return (List<Dog>) repository.findAll();
  }

  @Override
  public Dog findDogById(Long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<String> findAllDogNames() {
    return (List<String>) (repository.findAll());
  }

  @Override
  public boolean deleteDogBreed(Long id) {
    return false;
  }

  @Override
  public Dog updateDogName(String newName, Long id) {
    return null;
  }
}
