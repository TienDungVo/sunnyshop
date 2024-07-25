package com.example.asm.dao;

import com.example.asm.model.Order;
import com.example.asm.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailDAO extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderIn(List<Order> orders);
    List<OrderDetail> findByOrder(Order order);
}
