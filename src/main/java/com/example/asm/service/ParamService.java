package com.example.asm.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ParamService {
    @Autowired
    HttpServletRequest request; // Tự động tiêm HttpServletRequest vào lớp

    public String getString(String name, String defaultValue) {
        String value = request.getParameter(name); // Lấy giá trị tham số từ request
        return value != null ? value : defaultValue; // Trả về giá trị tham số hoặc giá trị mặc định
    }
    public int getInt(String name, int defaultValue) {
        String value = request.getParameter(name); // Lấy giá trị tham số từ request
        try {
            return value != null ? Integer.parseInt(value) : defaultValue; // Trả về giá trị tham số hoặc giá trị mặc
            // định
        } catch (NumberFormatException e) {
            return defaultValue; // Trả về giá trị mặc định nếu có lỗi định dạng
        }
    }
    public double getDouble(String name, double defaultValue) {
        String value = request.getParameter(name); // Lấy giá trị tham số từ request
        try {
            return value != null ? Double.parseDouble(value) : defaultValue; // Trả về giá trị tham số hoặc giá trị mặc
            // định
        } catch (NumberFormatException e) {
            return defaultValue; // Trả về giá trị mặc định nếu có lỗi định dạng
        }
    }
    public boolean getBoolean(String name, boolean defaultValue) {
        String value = request.getParameter(name); // Lấy giá trị tham số từ request
        return value != null ? Boolean.parseBoolean(value) : defaultValue; // Trả về giá trị tham số hoặc giá trị mặc
        // định
    }
    public Date getDate(String name, String pattern) {
        String value = request.getParameter(name); // Lấy giá trị tham số từ request
        if (value == null)
            return null; // Trả về null nếu tham số không tồn tại
        try {
            return (Date) new SimpleDateFormat(pattern).parse(value); // Trả về giá trị thời gian
        } catch (ParseException e) {
            throw new RuntimeException("Date format error", e); // Ném lỗi nếu định dạng sai
        }
    }

    public File save(MultipartFile file, String uploadDir) {
        if (file.isEmpty())
            return null;
        try {
            File savedFile = new File(uploadDir, file.getOriginalFilename()); // Tạo đối tượng File với đường dẫn và tên file
            file.transferTo(savedFile); // Lưu file vào đĩa
            return savedFile; // Trả về đối tượng File đã lưu
        } catch (IOException e) {
            throw new RuntimeException("File save error", e); // Ném lỗi nếu có vấn đề khi lưu file
        }
    }
}