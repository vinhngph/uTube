package com.utube.api.Home;

import java.io.IOException;

import com.utube.daos.HomeDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/home/search" })
@MultipartConfig()
public class SearchVideos extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String key = request.getParameter("key");

        if (key == null || key.isEmpty()) {
            String nullJson = "[]";
            response.getWriter().write(nullJson);
            return;
        }

        String results = HomeDAO.searchName(key);
        response.getWriter().write(results);
    }
}
