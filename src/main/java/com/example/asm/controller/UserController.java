package com.example.asm.controller;

import com.example.asm.dao.AccountDAO;
import com.example.asm.dao.UserDAO;
import com.example.asm.model.Account;
import com.example.asm.model.Order;
import com.example.asm.model.Product;
import com.example.asm.model.User;
import com.example.asm.service.CookieService;
import com.example.asm.service.OrderService;
import com.example.asm.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    UserDAO userDAO;
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    SessionService sessionService;
    @Autowired
    CookieService cookieService;
    @Autowired
    OrderService orderService;

    @RequestMapping("/sigup/user")
    public String SignupUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("fullName") String fullName,
                             @RequestParam("email") String email,
                             @RequestParam("phoneNumber") String phoneNumber,
                             @RequestParam("gender") Boolean gender,
                             @RequestParam("address") String address,
                             Model model) {
        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setGender(gender);
        user.setAddress(address);
        userDAO.save(user);
        Account account = new Account();
        account.setUsername(username);
        account.setPasswordHash(password);
        account.setUser(user);
        accountDAO.save(account);
        model.addAttribute("message", "success");
        model.addAttribute("text", "Đăng ký thành công!");
        return "sigup";
    }

    @RequestMapping("/sunny/signup")
    public String sigup(Model model) {
        return "sigup";

    }

    @RequestMapping("/sunny/updateuser")
    public String UpdateUser(Model model) {
        String username = sessionService.get("username");
        if (username == null) {
            return "redirect:/login/home";
        } else {
            User user = userDAO.findByUsername(username);
            Optional<Account> account = accountDAO.findByUsername(username);
            model.addAttribute("account", account);
            model.addAttribute("user", user);
            return "sigup";
        }


    }

    @RequestMapping("/sunny/usermn")
    public String usermn(Model model) {
        String username = sessionService.get("username");
        if (username == null) {
            return "redirect:/login/home";
        } else {
            Integer account = accountDAO.findUserIdByUsername(username);
            String password = cookieService.getValue("password");
            Date mostRecentUpdatedDate = accountDAO.findMostRecentUpdatedDateByUsername(username);

            model.addAttribute("username", username);
            model.addAttribute("password", password);
            model.addAttribute("mostRecentUpdatedDate", mostRecentUpdatedDate);
            return "user_mn";
        }

    }

    @RequestMapping("/sunny/userup")
    public String userup(Model model, @RequestParam("password") String password, HttpSession session,RedirectAttributes redirectAttributes) {
        String username = sessionService.get("username");
        accountDAO.updatePasswordAndUpdatedAt(username, password);
        session.setAttribute("username", username);
        cookieService.add("username", username, 24 * 30); // Lưu cookie trong 30 ngày
        cookieService.add("password", password, 24 * 30); // Lưu cookie trong 30 ngày
        redirectAttributes.addFlashAttribute("message", "success");
        redirectAttributes.addFlashAttribute("text", "Cập nhật thông tin thành công!!");
        return "redirect:/sunny/usermn";
    }

    @RequestMapping("/user/info")
    public String info(Model model) {
        User user = userDAO.findByUsername(sessionService.get("username"));
        model.addAttribute("fullname", user.getFullName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("phoneNumber", user.getPhoneNumber());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("address", user.getAddress());

        return "user_user";
    }

    @RequestMapping("/user/infoup")
    public String infoUp(Model model, @RequestParam("username") String username,
                         @RequestParam("fullname") String fullName,
                         @RequestParam("email") String email,
                         @RequestParam("phoneNumber") String phoneNumber,
                         @RequestParam("address") String address,RedirectAttributes redirectAttributes
    ) {
        userDAO.updateUserInfo(username, fullName, email, phoneNumber, address);
        redirectAttributes.addFlashAttribute("message", "success");
        redirectAttributes.addFlashAttribute("text", "Cập nhật thông tin thành công!!");
        model.addAttribute("message", "success");
        model.addAttribute("text", "Cập nhật thông tin thành công!!");
        return "redirect:/user/info";
    }
    @RequestMapping ("/order/create")
    public String createOrderFromCart(Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.createOrderFromCart();
            model.addAttribute("order", order);
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được mua,bạn có thể đến mục đơn hàng để xem chi tiết!!");
            model.addAttribute("message","Sản phẩm đã được mua,bạn có thể đến mục đơn hàng để xem chi tiết!!");

            return "cart"; // The view name to display the order confirmation
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "cart"; // Redirect to cart page with error message
        }
    }

    @RequestMapping("/user/order")
    public String orderDetail(Model model) {
        String username = sessionService.get("username");
        if (username == null) {
            return "redirect:/login/home"; // Redirect to login if user is not logged in
        }

        try {
            List<Product> products = orderService.getOrderedProductsByUsername(username);
            model.addAttribute("products", products);
            return "user_order"; // The view name to display the ordered products
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "user_order"; // Redirect to error page with error message
        }
    }


}
