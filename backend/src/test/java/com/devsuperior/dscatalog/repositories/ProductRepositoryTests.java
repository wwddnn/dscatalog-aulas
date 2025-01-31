package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {

        //arrange
        long existingId = 1L;

        //act
        repository.deleteById(existingId);

        //assert //findById retorna um Optional
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

}
