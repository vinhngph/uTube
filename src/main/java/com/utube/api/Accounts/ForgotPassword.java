package com.utube.api.Accounts;

import java.io.IOException;

import com.utube.daos.AccountDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/accounts/forgot-password" })
@MultipartConfig()
public class ForgotPassword extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        if (email.isEmpty() || email == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int status = AccountDAO.sendOTPMail(email);

        if (status == 0) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else if (status == -1) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        int otp = Integer.parseInt(request.getParameter("otp"));
        String newPassword = request.getParameter("new_password");

        if (email.isEmpty() || email == null || newPassword.isEmpty() || newPassword == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int status = AccountDAO.verifyOTP(email, otp);

        if (status == 0) {
            boolean rs = AccountDAO.updatePassword(email, newPassword);
            if (rs) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else if (status == -1) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // OTP is invalid or email is not found
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
