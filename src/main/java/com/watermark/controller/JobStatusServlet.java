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
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/jobStatus")
public class JobStatusServlet extends HttpServlet {
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

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            String status = job.getStatus();
            String downloadUrl = "";
            
            if ("COMPLETED".equals(status)) {
                downloadUrl = request.getContextPath() + "/download?jobId=" + job.getId();
            }

            response.getWriter().write(String.format(
                "{\"status\": \"%s\", \"downloadUrl\": \"%s\"}",
                status,
                downloadUrl
            ));

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            throw new ServletException("Lỗi database", e);
        }
    }
}