package com.example.asm.controller;

import com.example.asm.dao.CategoryDAO;
import com.example.asm.dao.ProductDAO;
import com.example.asm.dao.SupplierDAO;
import com.example.asm.model.Product;
import com.example.asm.model.ProductCategory;
import com.example.asm.model.Supplier;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class ManagerController {
    @Autowired
    SupplierDAO supplierDAO;
    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    ProductDAO productDAO;
    @RequestMapping("/admin/index")
    public String Mana(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            // If not logged in, redirect to login page
            return "redirect:/admin/login"; // Adjust the login URL as per your application
        }
        model.addAttribute("product", new Product());
        model.addAttribute("categorys", categoryDAO.findAll());
       model.addAttribute("suppliers", supplierDAO.findAll());
        return "MN_Index";
    }

    @RequestMapping("/admin/add")
    public String add(Model model, @RequestParam("fileInput") MultipartFile photo, @ModelAttribute("product") Product product) {
        // Xử lý lưu ảnh và sản phẩm
        if (photo.isEmpty()) {
            return "MN_Index"; // Trả về trang index nếu không có ảnh
        }
        Path uploadDir = Paths.get("uploads/");
        Path imagesDir = Paths.get("images/");
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            if (!Files.exists(imagesDir)) {
                Files.createDirectories(imagesDir);
            }
            // Lưu file ảnh
            String fileName = photo.getOriginalFilename();
            InputStream photoInputStream = photo.getInputStream();
            Files.copy(photoInputStream, uploadDir.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
            Files.copy(photoInputStream, imagesDir.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
            // Lưu thông tin sản phẩm
            productDAO.save(product);
            System.out.println("insert thành công");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("lỗi");
            return "MN_Index";
        }
        // Chuyển hướng người dùng đến trang danh sách sản phẩm sau khi thêm thành công
        return "redirect:/admin/products";
    }
    @RequestMapping("/admin/save")
    public String save(Model model, @ModelAttribute("product") Product product) {

        productDAO.save(product);
        model.addAttribute("message", "success");
        model.addAttribute("text", "Thêm mới thành công!");

        return "redirect:/admin/index";
    }
    @RequestMapping("/admin/table")
    public String Table(Model model) {
       model.addAttribute("products", productDAO.findAll());
        return "MN_Table";
    }
    @RequestMapping("/product/edit/{id}")
    public String UpdateTable(Model model, @PathVariable("id") Integer id) {
        Product product = productDAO.findById(Long.valueOf(id)).orElse(null);
        // Truyền sản phẩm vào model
        model.addAttribute("product", product);

        // Truyền danh sách nhãn hàng và danh mục nếu cầns
        model.addAttribute("suppliers", supplierDAO.findAll());
        model.addAttribute("categories", categoryDAO.findAll());
        return "MN_Update";
    }
    @RequestMapping(value = "/update/product", method = RequestMethod.POST)
    public String updateProduct(Model model, RedirectAttributes redirectAttributes,
                                @RequestParam("productId") Integer productId,
                                @RequestParam("productName") String productName,
                                @RequestParam("supplierId") Integer supplierId,
                                @RequestParam("categoryId") Integer categoryId,
                                @RequestParam("unitPrice") BigDecimal unitPrice,
                                @RequestParam("unitsInStock") Integer unitsInStock,
                                @RequestParam("unitsOnOrder") Integer unitsOnOrder,
                                @RequestParam(value = "discontinued", defaultValue = "false") Boolean discontinued,
                                @RequestParam("description") String description) {
        // Tìm sản phẩm cần cập nhật dựa trên ProductID
        Product product = productDAO.findById(Long.valueOf(productId)).orElse(null);

        // Kiểm tra xem sản phẩm có tồn tại không
        if (product != null) {
            // Cập nhật thông tin của sản phẩm
            product.setProductName(productName);
            Supplier supplier = supplierDAO.findById(Long.valueOf(supplierId)).orElse(null);
            ProductCategory category = categoryDAO.findById(Long.valueOf(categoryId)).orElse(null);
            product.setSupplier(supplier);
            product.setCategory(category);
            product.setUnitPrice(unitPrice);
            product.setUnitsInStock(unitsInStock);
            product.setUnitsOnOrder(unitsOnOrder);
            product.setDiscontinued(discontinued);
            product.setDescription(description);

            // Lưu sản phẩm đã cập nhật vào cơ sở dữ liệu
            productDAO.save(product);

            redirectAttributes.addFlashAttribute("message", "success");
            redirectAttributes.addFlashAttribute("text", "Cập nhật thành công!!");
            model.addAttribute("message", "success");
            model.addAttribute("text", "Cập nhật thành công!!");
        } else {
            model.addAttribute("error", "Không tìm thấy sản phẩm để cập nhật");
        }

        // Chuyển hướng về trang MN_Table sau khi cập nhật
        return "redirect:/admin/table";
    }


}



