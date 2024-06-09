package com.utube.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;

import com.google.gson.Gson;
import com.utube.daos.AccountDAO;
import com.utube.daos.SessionDAO;
import com.utube.dtos.UserDTO;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get input
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        Date dob = Date.valueOf(request.getParameter("dob"));
        // ----------------

        String sessionDevice = request.getHeader("User-Agent");

        // Register user
        boolean register = AccountDAO.registerUser(email, username, password, fullName, dob);
        if (register) {
            UserDTO user = AccountDAO.getUser(username, password);
            if (user != null) {
                SessionDAO.createSession(user.getUserId(), sessionDevice);

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
