package com.example.asm.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    @Autowired
    HttpSession session; // Tự động tiêm HttpSession vào lớp

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) session.getAttribute(name); // Lấy giá trị attribute từ session
    }

    public void set(String name, Object value) {
        session.setAttribute(name, value); // Thiết lập giá trị attribute trong session
    }

    public void remove(String name) {
        session.removeAttribute(name); // Xóa attribute khỏi session
    }
}