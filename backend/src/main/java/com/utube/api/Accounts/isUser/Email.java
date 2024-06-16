package com.utube.api.Accounts.isUser;

import java.io.IOException;

import com.utube.daos.AccountDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/accounts/isUser/email" })
@MultipartConfig()
public class Email extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        if (email == null || email.isEmpty()) {
            email = "";
        }

        boolean emailStatus = AccountDAO.isEmail(email);

        if (emailStatus) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
