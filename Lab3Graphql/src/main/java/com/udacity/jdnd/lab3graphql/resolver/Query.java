package com.udacity.jdnd.lab3graphql.resolver;

import com.udacity.jdnd.lab3graphql.entity.Dog;
import com.udacity.jdnd.lab3graphql.repository.DogRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class Query{
    private final DogRepository dogRepository;

    public Query(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    @QueryMapping
    public Iterable<Dog> findAllDogs() {
        return dogRepository.findAll();
    }

    @QueryMapping
    public Dog findDogById(@Argument Long id) {
        Optional<Dog> optionalDog = dogRepository.findById(id);
        return optionalDog.get();
    }
}