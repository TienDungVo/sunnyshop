package com.example.asm.dao;

import com.example.asm.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product, Long> {
    List<Product> findTop3ByOrderByProductIDAsc();
}
