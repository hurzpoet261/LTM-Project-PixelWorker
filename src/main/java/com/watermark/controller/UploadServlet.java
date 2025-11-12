package com.watermark.controller;

import com.watermark.dao.ImageJobDAO;
import com.watermark.model.ImageJob;
import com.watermark.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;

@WebServlet("/upload")
@MultipartConfig // Bắt buộc phải có để xử lý file
public class UploadServlet extends HttpServlet {
    private ImageJobDAO jobDAO = new ImageJobDAO();
    // *** ĐẢM BẢO THƯ MỤC NÀY TỒN TẠI TRÊN MÁY BẠN ***
    private static final String UPLOAD_DIR = "E:/LTM-jsp/uploads/original/"; 

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        Part filePart = request.getPart("imageFile");
        String originalFilename = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        String savedPath = UPLOAD_DIR + uniqueFilename;
        
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();
        
        filePart.write(savedPath);

        ImageJob job = new ImageJob();
        job.setUserId(user.getId());
        job.setOriginalFilename(originalFilename);
        job.setOriginalPath(savedPath);
        job.setStatus("PENDING");
        job.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        try {
            jobDAO.createJob(job);
            response.sendRedirect("history");
        } catch (SQLException e) {
            throw new ServletException("Lỗi khi tạo job trong DB", e);
        }
    }
}