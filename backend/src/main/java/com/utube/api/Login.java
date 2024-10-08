package com.utube.api;

import java.io.IOException;

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

@WebServlet(urlPatterns = { "/api/login" })
@MultipartConfig()
public class Login extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get input
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // ----------------
        
        String sessionDevice = request.getHeader("User-Agent");

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
    }
}
