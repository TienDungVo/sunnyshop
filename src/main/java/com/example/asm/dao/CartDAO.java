package com.example.asm.dao;

import com.example.asm.model.Cart;
import com.example.asm.model.Product;
import com.example.asm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartDAO extends JpaRepository<Cart, Integer> {
    @Query("SELECT c FROM Cart c WHERE c.user.userID = ?1")
    List<Cart> findByUserID(Integer userID);

    List<Cart> findByUserAndProduct(User user, Product product);
}
