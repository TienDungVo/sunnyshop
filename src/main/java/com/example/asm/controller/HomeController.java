package com.example.asm.controller;

import com.example.asm.dao.AccountDAO;
import com.example.asm.dao.ProductDAO;
import com.example.asm.model.Account;
import com.example.asm.model.Product;
import com.example.asm.service.CookieService;
import com.example.asm.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    ProductDAO productDAO;
    @Autowired
    CookieService cookieService;
    @Autowired
    SessionService sessionService;

    @RequestMapping("/sunny/home")

    public String Home(Model model) {
        List<Product> products = productDAO.findTop3ByOrderByProductIDAsc();
        model.addAttribute("products", products);
        String username = sessionService.get("username");
        if (username != null) {
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "index";
    }

    @RequestMapping("/login/home")
    public String showLoginForm(Model model) {
        String username = cookieService.getValue("username");
        String password = cookieService.getValue("password");
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        return "login";
    }

    @RequestMapping("/sunny/login")
    public String login(Model model, HttpSession session,
                        @RequestParam("username_AC") String username,
                        @RequestParam("password_AC") String password,
                        @RequestParam(value = "remember", required = false) String remember) {
        try {
            Optional<Account> optionalAccount = accountDAO.findByUsername(username);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                if (account.getPasswordHash().equals(password)) {
                    System.out.println("Login successful");
                    session.setAttribute("username", username);
                    model.addAttribute("username", username);
                    model.addAttribute("password", password);
                    if (remember != null) {
                        cookieService.add("username", username, 24 * 30); // Lưu cookie trong 30 ngày
                        cookieService.add("password", password, 24 * 30); // Lưu cookie trong 30 ngày
                    }
                    model.addAttribute("message", "success");
                    model.addAttribute("text", "Đăng nhập thành công!");
                    return "login";
                } else {
                    System.out.println("Password does not match");
                    model.addAttribute("message", "error");
                    model.addAttribute("text", "Mật Khẩu không đúng!");
                    return "login";
                }
            } else {
                model.addAttribute("message", "error");
                model.addAttribute("text", "Tên đăng nhâp không tồn tại!");
                return "login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "error");
            model.addAttribute("text", "Có lỗi sảy ra!");
            return "login";
        }
    }


    @RequestMapping("/sunny/logout")
    public String logout() {
        sessionService.remove("username");
        cookieService.remove("username");
        cookieService.remove("password");
        return "redirect:/sunny/home";
    }
    @RequestMapping("/sunny/about")
    public String About(Model model) {

        return "about";
    }
    @RequestMapping("/product/page")
    public String paginate(Model model, @RequestParam("p") Optional<Integer> p) {
        int currentPage = p.orElse(0);
        Pageable pageable = PageRequest.of(currentPage, 8);
        Page<Product> page = productDAO.findAll(pageable);
        model.addAttribute("page", page);
        return "shop";
    }

}
