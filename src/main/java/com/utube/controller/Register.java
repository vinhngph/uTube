package com.utube.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;

import com.google.gson.Gson;
import com.utube.daos.Account;
import com.utube.dtos.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/register" })
@MultipartConfig()
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

        if (register) {
            User user = Account.getUser(username, password);
            if (user != null) {
                Gson gson = new Gson();
                String json = gson.toJson(user);
                response.setContentType("application/json");
                response.getWriter().write(json);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }
}
