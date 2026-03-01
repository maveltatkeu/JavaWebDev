package com.udacity.jdnd.itemservice.repository;

import com.udacity.jdnd.itemservice.entity.Dog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface DogRepository extends CrudRepository<Dog, Long> {
}
