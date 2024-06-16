package com.utube.api.Accounts;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.utube.daos.AccountDAO;
import com.utube.dtos.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/accounts/users" })
@MultipartConfig()
public class ListUsers extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<UserDTO> users = AccountDAO.getAllUsers();

        for (UserDTO user : users) {
            user.setPassword(null);
        }

        Gson gson = new Gson();
        String usersJson = gson.toJson(users);

        response.setContentType("application/json");
        response.getWriter().write(usersJson);
    }
}
