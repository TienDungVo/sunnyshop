package com.example.asm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Integer orderID;

    @ManyToOne
    @JoinColumn(name = "CustomerID", referencedColumnName = "UserID")
    private User customer;

    @Column(name = "OrderDate")
    private Date orderDate;

    @Column(name = "TotalAmount")
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    // Getters and Setters
}
