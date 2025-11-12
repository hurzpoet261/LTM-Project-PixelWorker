package com.watermark.controller;

import com.watermark.dao.UserDAO;
import com.watermark.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        
        try {
            User dbUser = userDAO.getUserByUsername(user);
            if (dbUser != null && dbUser.getPassword().equals(pass)) {
                HttpSession session = request.getSession();
                session.setAttribute("user", dbUser);
                response.sendRedirect("upload.jsp");
            } else {
                response.sendRedirect("login.jsp?error=true");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}