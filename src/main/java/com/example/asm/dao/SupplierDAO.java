package com.example.asm.dao;

import com.example.asm.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierDAO extends JpaRepository<Supplier, Long> {
}
