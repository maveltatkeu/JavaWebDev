package com.udacity.jdnd.lab3graphql.mutator;

import java.util.Optional;

import com.udacity.jdnd.lab3graphql.entity.Dog;
import com.udacity.jdnd.lab3graphql.repository.DogRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;


@Controller
public class Mutation {
    private final DogRepository dogRepository;

    public Mutation(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    @MutationMapping
    public boolean deleteDogBreed(@Argument String breed) {
        boolean deleted = false;
        Iterable<Dog> allDogs = dogRepository.findAll();
        // Loop through all dogs to check their breed
        for (Dog d:allDogs) {
           if (d.getBreed().equals(breed)) {
               // Delete if the breed is found
               dogRepository.delete(d);
               deleted = true;
           }
        }
        
        return deleted;
    }

    @MutationMapping
    public Dog updateDogName(@Argument String newName, @Argument Long id) {
        Optional<Dog> optionalDog = dogRepository.findById(id);

            Dog dog = optionalDog.get();
            // Set the new name and save the updated dog
            dog.setName(newName);
            dogRepository.save(dog);
            return dog;
               
    }
}