package com.example.asm.service;

import com.example.asm.dao.CategoryDAO;
import com.example.asm.dao.ProductDAO;
import com.example.asm.model.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private ProductDAO productDAO;

    public List<ProductCategory> getAllCategories() {
        return categoryDAO.findAll();
    }

    public void saveCategory(ProductCategory category) {
        categoryDAO.save(category);
    }
    public ProductCategory findById(Long categoryID) {
        Optional<ProductCategory> optionalCategory = categoryDAO.findById(categoryID);
        return optionalCategory.orElse(null); // handle null as appropriate
    }
}