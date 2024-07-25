package com.example.asm.dao;

import com.example.asm.model.Order;
import com.example.asm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface OrderDAO extends JpaRepository<Order, Integer> {
    List<Order> findByCustomer(User customer);


    @Query(value = "SELECT * FROM orders o WHERE CAST(o.order_date AS DATE) = CAST(:date AS DATE)", nativeQuery = true)
    List<Order> findOrdersByOrderDate(@Param("date") Date date);

    @Query(value = "SELECT * FROM orders o WHERE o.order_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<Order> findOrdersInDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    BigDecimal findTotalAmountSum();
}





