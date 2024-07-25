package com.example.asm.dao;

import com.example.asm.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDAO extends JpaRepository<User, Long> {
    User findByUsername(String username);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.fullName = :fullName, u.email = :email, u.phoneNumber = :phoneNumber, u.address = :address, u.updatedAt = CURRENT_TIMESTAMP WHERE u.username = :username")
    void updateUserInfo(String username, String fullName, String email, String phoneNumber, String address);
    @Query("SELECT u.email FROM User u")
    List<String> findAllEmails();
}
