package com.watermark.controller;

import com.watermark.dao.ImageJobDAO;
import com.watermark.model.ImageJob;
import com.watermark.model.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {
    private ImageJobDAO jobDAO = new ImageJobDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        try {
            List<ImageJob> jobs = jobDAO.getJobsByUser(user.getId());
            request.setAttribute("jobs", jobs);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("history.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Lỗi khi lấy lịch sử job", e);
        }
    }
}