package com.example.asm.service;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CookieService {
    @Autowired
    HttpServletRequest request; // Tự động tiêm HttpServletRequest vào lớp
    @Autowired
    HttpServletResponse response; // Tự động tiêm HttpServletResponse vào lớp

    public Cookie get(String name) {
        Cookie[] cookies = request.getCookies(); // Lấy tất cả cookies từ request
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie; // Trả về cookie nếu tên khớp
                }
            }
        }
        return null; // Trả về null nếu không tìm thấy cookie
    }

    /**
     * Đọc giá trị của cookie từ request
     * @param name tên cookie cần đọc
     * @return chuỗi giá trị đọc được hoặc rỗng nếu không tồn tại
     */
    public String getValue(String name) {
        Cookie cookie = get(name); // Lấy cookie từ tên
        return cookie != null ? cookie.getValue() : ""; // Trả về giá trị cookie hoặc chuỗi rỗng nếu không tồn tại
    }

    /**
     * Tạo và gửi cookie về client
     * @param name tên cookie
     * @param value giá trị cookie
     * @param hours thời hạn (giờ)
     * @return đối tượng cookie đã tạo
     */
    public Cookie add(String name, String value, int hours) {
        Cookie cookie = new Cookie(name, value); // Tạo đối tượng cookie với tên và giá trị
        cookie.setMaxAge(hours * 3600); // Thiết lập thời hạn sống của cookie
        cookie.setPath("/"); // Thiết lập đường dẫn cho cookie
        response.addCookie(cookie); // Thêm cookie vào response
        return cookie; // Trả về đối tượng cookie đã tạo
    }

    /**
     * Xóa cookie khỏi client
     * @param name tên cookie cần xóa
     */
    public void remove(String name) {
        Cookie cookie = new Cookie(name, ""); // Tạo đối tượng cookie với tên và giá trị rỗng
        cookie.setMaxAge(0); // Thiết lập thời hạn sống của cookie bằng 0 để xóa
        cookie.setPath("/"); // Thiết lập đường dẫn cho cookie
        response.addCookie(cookie); // Thêm cookie vào response
    }
}
