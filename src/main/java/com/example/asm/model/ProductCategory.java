package com.example.asm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ProductCategories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID")
    private Integer categoryID;


    @Column(name = "CategoryName", columnDefinition = "NVARCHAR(255)")
    private String categoryName;

    @Column(name = "Description", columnDefinition = "NVARCHAR(255)")
    private String description;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    // Getters and Setters
}
