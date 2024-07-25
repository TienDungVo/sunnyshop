package com.example.asm.controller;

import com.example.asm.dao.AccountDAO;
import com.example.asm.dao.CartDAO;
import com.example.asm.dao.ProductDAO;
import com.example.asm.dao.UserDAO;
import com.example.asm.model.*;
import com.example.asm.service.OrderService;
import com.example.asm.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {
    @Autowired
    ProductDAO productDAO;
    @Autowired
    CartDAO cartDAO;
    @Autowired
    SessionService sessionService;
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    UserDAO userDAO;


    @RequestMapping("/sunny/shop")
    public String sunnyShop(Model model,@RequestParam("p") Optional<Integer> p) {
        int currentPage = p.orElse(0);
        Pageable pageable = PageRequest.of(currentPage, 8);
        Page<Product> page = productDAO.findAll(pageable);
        model.addAttribute("page", page);
//        List<Product> products = productDAO.findAll();
//        model.addAttribute("products", products);
        return "shop";
    }

    @RequestMapping("/product/{id}")
    public String productID(Model model, @PathVariable Integer id) {
        Optional<Product> item = productDAO.findById((long) id);

        if (item.isPresent()) {
            model.addAttribute("product", item.get());
            return "product";
        } else {
            return "productNotFound";
        }
    }

    @RequestMapping("/cart/ID")
    public String cartID(Model model) {
        String username = sessionService.get("username");
        if (username == null) {
            return "redirect:/login/home"; // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
        }
        Integer userID = accountDAO.findUserIdByUsername(username);
        if (userID == null) {
            // Xử lý trường hợp không tìm thấy userID
            // Ví dụ: thông báo lỗi hoặc chuyển hướng đến một trang lỗi
            return "redirect:/error"; // Ví dụ: chuyển hướng đến trang lỗi
        }
        User demo = userDAO.findByUsername(username);
        model.addAttribute("demo", demo);
        List<Cart> carts = cartDAO.findByUserID(userID);
        model.addAttribute("carts", carts);
        BigDecimal totalPrice = carts.stream()
                .map(cart -> cart.getProduct().getUnitPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalPrice", totalPrice);
        return "cart";
    }


    @RequestMapping("/cart/add")
    public String cartAdd(ModelMap model, RedirectAttributes redirectAttributes, @RequestParam("productId") Long productId, @RequestParam("quantity") int quantity) {
        String username = sessionService.get("username");
        if (username == null) {
            return "redirect:/login/home"; // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
        }
        Optional<Account> optionalAccount = accountDAO.findByUsername(username);
        if (optionalAccount.isPresent()) {
            User user = optionalAccount.get().getUser(); // Giả sử có phương thức getUser() để lấy thông tin người dùng
            Optional<Product> optionalProduct = productDAO.findById(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();

                // Kiểm tra xem sản phẩm đã có trong giỏ hàng hay chưa
                List<Cart> existingCarts = cartDAO.findByUserAndProduct(user, product);
                if (!existingCarts.isEmpty()) {
                    // Sản phẩm đã có trong giỏ hàng, cộng dồn số lượng
                    Cart existingCart = existingCarts.get(0); // Giả sử chỉ có một mục cho mỗi sản phẩm
                    existingCart.setQuantity(existingCart.getQuantity() + quantity);
                    cartDAO.save(existingCart);
                    model.addAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng!!");
                } else {
                    // Sản phẩm chưa có trong giỏ hàng, thêm mới
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setProduct(product);
                    cart.setQuantity(quantity);
                    cartDAO.save(cart);
                    model.addAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng!!");
                }

                redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng!!");
                redirectAttributes.addAttribute("id", productId);
                return "redirect:/product/{id}"; // Chuyển hướng về trang chủ sau khi thêm vào giỏ hàng
            }
        }
        return "redirect:/sunny/shop"; // Nếu có lỗi, chuyển hướng về trang chủ
    }

    @RequestMapping("/cart/remove")
    public String cartRemove(@RequestParam("productId") Long productId) {
        String username = sessionService.get("username");
        if (username == null) {
            return "redirect:/login/home";
        }
        Optional<Account> optionalAccount = accountDAO.findByUsername(username);
        if (optionalAccount.isPresent()) {
            User user = optionalAccount.get().getUser();
            Optional<Product> optionalProduct = productDAO.findById(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                List<Cart> carts = cartDAO.findByUserAndProduct(user, product);
                if (!carts.isEmpty()) {
                    cartDAO.deleteAll(carts);
                }
                return "redirect:/cart/ID";
            }
        }
        return "redirect:/cart/ID";
    }



}
