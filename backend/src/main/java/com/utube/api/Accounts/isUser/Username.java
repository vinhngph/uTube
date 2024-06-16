package com.utube.api.Accounts.isUser;

import java.io.IOException;

import com.utube.daos.AccountDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/accounts/isUser/username" })
@MultipartConfig()
public class Username extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");

        if (username == null || username.isEmpty()) {
            username = "";
        }

        boolean usernameStatus = AccountDAO.isUsername(username);

        if (usernameStatus) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
