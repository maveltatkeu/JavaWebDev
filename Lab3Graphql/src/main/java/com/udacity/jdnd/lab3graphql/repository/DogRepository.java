package com.udacity.jdnd.lab3graphql.repository;

import com.udacity.jdnd.lab3graphql.entity.Dog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogRepository extends CrudRepository<Dog, Long> {
}
