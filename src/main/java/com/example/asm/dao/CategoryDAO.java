package com.example.asm.dao;

import com.example.asm.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryDAO extends JpaRepository<ProductCategory, Long> {
    Optional<ProductCategory> findById(Long categoryID);
}
