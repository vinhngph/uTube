package com.utube.api.Accounts;

import java.io.IOException;

import com.google.gson.Gson;
import com.utube.daos.AccountDAO;
import com.utube.dtos.UserInformationDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/accounts/details" })
public class ShowDetails extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int user_id = Integer.parseInt(request.getParameter("user_id"));
        UserInformationDTO user = AccountDAO.getDetails(user_id);

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            Gson gson = new Gson();
            String userJson = gson.toJson(user);

            response.setContentType("application/json");
            response.getWriter().write(userJson);
        }
    }
}
