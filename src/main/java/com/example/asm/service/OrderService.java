package com.example.asm.service;

import com.example.asm.dao.*;
import com.example.asm.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    OrderDAO orderDAO;
    @Autowired
    OrderDetailDAO orderDetailDAO;
    @Autowired
    CartDAO cartDAO;
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    SessionService sessionService;
    @Autowired
    private UserDAO userDAO;


    public Order createOrderFromCart() {
        String username = sessionService.get("username");
        if (username == null) {
            throw new RuntimeException("User is not logged in.");
        }

        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        List<Cart> carts = cartDAO.findByUserID(user.getUserID());
        if (carts.isEmpty()) {
            throw new RuntimeException("Cart is empty.");
        }

        // Create Order
        Order order = new Order();
        order.setCustomer(user);
        order.setOrderDate(new Date());
        BigDecimal totalAmount = carts.stream()
                .map(cart -> cart.getProduct().getUnitPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        order = orderDAO.save(order);

        // Create OrderDetails
        for (Cart cart : carts) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cart.getProduct());
            orderDetail.setQuantity(cart.getQuantity());
            orderDetail.setUnitPrice(cart.getProduct().getUnitPrice());
            orderDetailDAO.save(orderDetail);
        }

        // Clear Cart
        cartDAO.deleteAll(carts);

        return order;
    }

    public List<Product> getOrderedProductsByUsername(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        List<Order> orders = orderDAO.findByCustomer(user);
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found for the user.");
        }

        List<OrderDetail> orderDetails = orderDetailDAO.findByOrderIn(orders);
        return orderDetails.stream().map(OrderDetail::getProduct).collect(Collectors.toList());
    }

    public List<Order> getOrdersCreatedToday() {
        Date currentDate = new Date();
        return orderDAO.findOrdersByOrderDate(currentDate);
    }

    public List<Order> getOrdersCreatedInLastThreeDays() {
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -2);
        Date startDate = cal.getTime();
        return orderDAO.findOrdersInDateRange(startDate, endDate);
    }

    public List<Order> getOrdersCreatedThisWeek() {
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Date startDate = cal.getTime();
        return orderDAO.findOrdersInDateRange(startDate, endDate);
    }


}
