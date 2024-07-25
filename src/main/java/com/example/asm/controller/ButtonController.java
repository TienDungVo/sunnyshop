package com.example.asm.controller;

import com.example.asm.dao.OrderDAO;
import com.example.asm.dao.OrderDetailDAO;
import com.example.asm.dao.UserDAO;
import com.example.asm.model.MailInfo;
import com.example.asm.model.Order;
import com.example.asm.model.OrderDetail;
import com.example.asm.model.User;
import com.example.asm.service.MailerService;
import com.example.asm.service.MailerServiceImpl;
import com.example.asm.service.OrderService;
import com.example.asm.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class ButtonController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderDAO orderDAO;
    @Autowired
    SessionService sessionService;
    @Autowired
    UserDAO userDAO;
    @Autowired
    OrderDetailDAO orderDetailDAO;
    @Autowired
    MailerService mailer;

    @RequestMapping("/user/card")
    public String customer(Model model) {
        String username = sessionService.get("username");
        User user = userDAO.findByUsername(username);
        List<Order> orders = orderDAO.findByCustomer(user);
        model.addAttribute("orders", orders);
        return "user_card";
    }
    @GetMapping("/userCard/{orderid}")
    public String showOrderDetail(@PathVariable("orderid") Integer orderId, Model model) {

        Optional<Order> optionalOrder = orderDAO.findById(orderId);


        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();


            List<OrderDetail> orderDetails = orderDetailDAO.findByOrder(order);


            model.addAttribute("order", order);
            model.addAttribute("orderDetails", orderDetails);

            return "user_order";
        } else {
            return "order_not_found";
        }
    }
    @RequestMapping("/admin/send")
    public String contact(Model model) {

        model.addAttribute("mails", userDAO.findAllEmails());
        return "Admin/AD_SendMail";
    }
    @RequestMapping("/mail/send")
    public String demo(Model model, @RequestParam("to") String to, @RequestParam("subject") String subject,
                       @RequestParam("body") String body, RedirectAttributes redirectAttributes) {
        try {
            mailer.send(to, subject, body);
            redirectAttributes.addFlashAttribute("message", "success");
            redirectAttributes.addFlashAttribute("text", "Thông báo đã được gửi đi!!");
            model.addAttribute("message", "success");
            model.addAttribute("text", "Thông báo đã được gửi đi!!");
            return "redirect:/admin/send";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
