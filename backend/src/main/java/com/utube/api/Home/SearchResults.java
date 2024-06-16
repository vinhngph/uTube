package com.utube.api.Home;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.utube.daos.HomeDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/home/search/results" })
@MultipartConfig()
public class SearchResults extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String keyWord = request.getParameter("key");

        if (keyWord == null || keyWord.isEmpty()) {
            response.getWriter().write(nullJson());
            return;
        }

        String results = HomeDAO.searchResults(keyWord);

        if (results.equals("[]")) {
            response.getWriter().write(nullJson());
            return;
        }

        response.getWriter().write(results);
    }

    private static String nullJson() {
        Gson gson = new Gson();
        Map<String, String> nullJson = new HashMap<>();
        nullJson.put("message", "No search results found");
        return gson.toJson(nullJson);
    }
}
