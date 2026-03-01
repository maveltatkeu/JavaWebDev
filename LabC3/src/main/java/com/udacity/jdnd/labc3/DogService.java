package com.udacity.jdnd.labc3;

import java.util.List;

public interface DogService {
  List<Dog> retrieveDogs();

  List<String> retrieveDogBreed();

  String retrieveDogBreedById(Long id);

  List<String> retrieveDogNames();
}
