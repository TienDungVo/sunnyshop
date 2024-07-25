package com.example.asm.controller;

import com.example.asm.dao.*;
import com.example.asm.model.*;
import com.example.asm.service.CategoryService;
import com.example.asm.service.CookieService;
import com.example.asm.service.OrderService;
import com.example.asm.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {
    @Autowired
    SessionService sessionService;
    @Autowired
    CookieService cookieService;
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    OrderDetailDAO orderDetailDAO;
    @Autowired
    SupplierDAO supplierDAO;
  @Autowired
  ProductDAO productDAO;

    @RequestMapping("/admin/login")
    public String login() {
        sessionService.remove("username");
        cookieService.remove("username");
        cookieService.remove("password");
        return "Admin/AD_Login";
    }

    @RequestMapping("/admin/account")
    public String AD_Login(Model model, HttpSession session,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        try {
            Optional<Account> optionalAccount = accountDAO.findByUsername(username);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                if (account.getPasswordHash().equals(password)) {
                    if (account.getUserType() != null && account.getUserType()) {
                        System.out.println("Login successful");
                        sessionService.set("username", username);
                        sessionService.set("password", password);
                        sessionService.set("userType", account.getUserType());
                        model.addAttribute("message", "success");
                        model.addAttribute("text", "Đăng nhập thành công!");
                        return "redirect:/admin/load";
                    } else {
                        System.out.println("User type does not allow login");
                        model.addAttribute("message", "error");
                        model.addAttribute("text", "Tài khoản không có quyền truy cập!");
                        return "Admin/AD_Login";
                    }
                } else {
                    System.out.println("Password does not match");
                    model.addAttribute("message", "error");
                    model.addAttribute("text", "Mật khẩu không đúng!");
                    return "Admin/AD_Login";
                }
            } else {
                model.addAttribute("message", "error");
                model.addAttribute("text", "Tên đăng nhập không tồn tại!");
                return "Admin/AD_Login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "error");
            model.addAttribute("text", "Có lỗi xảy ra!");
            return "Admin/AD_Login";
        }
    }

    @RequestMapping("/admin/category")
    public String category(Model model) {
        List<ProductCategory> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("category", new ProductCategory());
        return "Admin/Category_ADD";

    }

    @RequestMapping("/category/add")
    public String categoryAdd(@ModelAttribute("category") ProductCategory category, Model model) {
        categoryService.saveCategory(category);
        List<ProductCategory> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories); // Add categories for the list view
        model.addAttribute("message", "success");
        model.addAttribute("text", "Thêm mới thành công!!");
        return "Admin/Category_ADD"; // Return the view for editing
    }

    @RequestMapping("/category/edit/{categoryID}")
    public String categoryEdit(@PathVariable Long categoryID, Model model) {
        ProductCategory category = categoryService.findById(categoryID);
        model.addAttribute("category", category);
        List<ProductCategory> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories); // Add categories for the list view
        return "Admin/Category_ADD"; // Return the view for editing
    }

    @RequestMapping("/category/update")
    public String categoryUpdate(ProductCategory category, RedirectAttributes redirectAttributes) {
        if (category.getCategoryID() != null) {
            ProductCategory existingCategory = categoryService.findById(Long.valueOf(category.getCategoryID()));
            if (existingCategory != null) {
                existingCategory.setCategoryName(category.getCategoryName());
                existingCategory.setDescription(category.getDescription());
                categoryService.saveCategory(existingCategory);
                redirectAttributes.addFlashAttribute("message", "success");
                redirectAttributes.addFlashAttribute("text", "Cập nhật thành công!!");
            } else {
                redirectAttributes.addFlashAttribute("message", "error");
                redirectAttributes.addFlashAttribute("text", "Không tìm thấy danh mục để cập nhật!!");
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "error");
            redirectAttributes.addFlashAttribute("text", "Không có ID danh mục để cập nhật!!");
        }
        return "redirect:/admin/category"; // Chuyển hướng về trang danh sách danh mục
    }

    @RequestMapping("/category/remove/{categoryID}")
    public String categoryRemove(@PathVariable Long categoryID, Model model, RedirectAttributes redirectAttributes) {
        categoryDAO.deleteById(categoryID);
        redirectAttributes.addFlashAttribute("message", "success");
        redirectAttributes.addFlashAttribute("text", "Xóa Thành Công!!");
        return "redirect:/admin/category"; // Return the view for editing
    }

    @RequestMapping("/admin/today")
    public String today(Model model) {
        List<Order> orders = orderService.getOrdersCreatedToday();
        model.addAttribute("orders", orders);
        return "Admin/AD_Manager";
    }
    @RequestMapping("/admin/recent")
    public String recent(Model model) {
        List<Order> orders = orderService.getOrdersCreatedInLastThreeDays();
        model.addAttribute("orders", orders);
        return "Admin/AD_Manager";
    }
    @RequestMapping("/admin/week")
    public String thisWeek(Model model) {
        List<Order> orders = orderService.getOrdersCreatedThisWeek();
        model.addAttribute("orders", orders);
        return "Admin/AD_Manager";
    }
    @RequestMapping("/manager/watch/{orderID}")
    public String watch(@PathVariable Integer orderID, Model model) {
        Optional<Order> optionalOrder = orderDAO.findById(orderID);


        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();


            List<OrderDetail> orderDetails = orderDetailDAO.findByOrder(order);
            model.addAttribute("order", order);
            model.addAttribute("orderDetails", orderDetails);

            return "Admin/AD_Manager";
        } else {
            return "order_not_found";
        }

    }
    @RequestMapping("/admin/load")
    public String load(Model model, RedirectAttributes redirectAttributes) {
        String username = sessionService.get("username");
        Boolean userType = sessionService.get("userType");

        // Kiểm tra người dùng đã đăng nhập và có quyền truy cập trang Admin hay không
        if (username != null && userType != null && userType) {
            // Thêm các thuộc tính vào model
            model.addAttribute("product_c", productDAO.count());
            model.addAttribute("account_c", accountDAO.count());
            model.addAttribute("orders_c", orderDAO.count());
            model.addAttribute("user_c", userDAO.count());
            model.addAttribute("ordersde_c", orderDetailDAO.count());
            model.addAttribute("categories_c", categoryDAO.count());
            model.addAttribute("supp_c", supplierDAO.count());
            model.addAttribute("doanhthu", orderDAO.findTotalAmountSum());
            return "Admin/AD_Load";
        } else {
            // Xử lý các trường hợp không hợp lệ
            if (username == null) {
                redirectAttributes.addFlashAttribute("message", "warning");
                redirectAttributes.addFlashAttribute("text", "Vui lòng đăng nhập !!");
            } else {
                redirectAttributes.addFlashAttribute("message", "warning");
                redirectAttributes.addFlashAttribute("text", "Vui lòng đăng nhập lại Admin!");
            }
            return "redirect:/admin/login";
        }
    }
    @RequestMapping("/product/sort")
    public String sort(Model model, @RequestParam(value = "field", required = false) String field) {
        Sort sort = Sort.by(Sort.Direction.DESC, field != null ? field : "unitPrice");
        model.addAttribute("field", field != null ? field.toUpperCase() : "unitPrice");

        List<Product> items = productDAO.findAll(sort);
        model.addAttribute("products", items);

        return "MN_Table";
    }


}


