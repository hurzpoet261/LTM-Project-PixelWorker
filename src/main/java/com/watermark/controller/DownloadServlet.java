package com.watermark.controller;

import com.watermark.dao.ImageJobDAO;
import com.watermark.model.ImageJob;
import com.watermark.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {
    private ImageJobDAO jobDAO = new ImageJobDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); 
            return;
        }

        User user = (User) session.getAttribute("user");
        
        try {
            int jobId = Integer.parseInt(request.getParameter("jobId"));
            ImageJob job = jobDAO.getJobById(jobId);

            if (job == null || job.getUserId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN); 
                return;
            }
            
            if (!"COMPLETED".equals(job.getStatus()) || job.getOutputPath() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND); 
                return;
            }

            File file = new File(job.getOutputPath());
            if (!file.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setContentType("application/octet-stream");
            response.setContentLengthLong(file.length());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + job.getOutputFilename() + "\"");

            try (FileInputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST); 
        } catch (SQLException e) {
            throw new ServletException("Lỗi database", e);
        }
    }
}