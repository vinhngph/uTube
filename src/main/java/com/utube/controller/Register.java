package com.utube.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;

import com.utube.daos.Account;
import com.utube.dtos.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/register" })
public class Register extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("./register.html");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String fullName = request.getParameter("fullname");
        Date dob = Date.valueOf(request.getParameter("dob"));

        String sessionDevice = request.getParameter("session_device");
        Timestamp sessionTime = Timestamp.valueOf(request.getParameter("session_time"));

        boolean register = Account.registerUser(email, username, password, fullName, dob, sessionDevice, sessionTime);
        User user = Account.getUser(username, password);
        if (user != null) {
            Cookie user_id = new Cookie("user_id", String.valueOf(user.getUserId()));
            Cookie user_name = new Cookie("user_name", user.getUsername());
            Cookie user_email = new Cookie("user_email", user.getEmail());
            Cookie user_role = new Cookie("user_role", String.valueOf(user.getRole()));

            response.addCookie(user_id);
            response.addCookie(user_name);
            response.addCookie(user_email);
            response.addCookie(user_role);
        }

        if (register) {
            response.sendRedirect("/");
        } else {
            response.sendRedirect("./register");
        }
    }
}
