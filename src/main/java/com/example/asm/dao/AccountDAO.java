package com.example.asm.dao;

import com.example.asm.model.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface AccountDAO extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);

    @Query("SELECT a.user.userID FROM Account a WHERE a.username = :username")
    Integer findUserIdByUsername(@Param("username") String username);

    @Query("SELECT a.updatedAt FROM Account a WHERE a.username = :username ORDER BY a.updatedAt DESC")
    Date findMostRecentUpdatedDateByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.passwordHash = :passwordHash, a.updatedAt = CURRENT_TIMESTAMP WHERE a.username = :username")
    void updatePasswordAndUpdatedAt(String username, String passwordHash);
}
