package com.utube.api.Home;

import java.io.IOException;

import com.utube.daos.HomeDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/home/videos" })
public class GetVideos extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String results = HomeDAO.getVideos();
        response.setContentType("application/json");
        response.getWriter().write(results);
    }
}
