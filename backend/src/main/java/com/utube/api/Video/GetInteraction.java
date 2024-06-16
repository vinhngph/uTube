package com.utube.api.Video;

import java.io.IOException;

import com.google.gson.Gson;
import com.utube.daos.VideoDAO;
import com.utube.dtos.VideoInteractionDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/video/interaction")
@MultipartConfig()
public class GetInteraction extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String videoId = request.getParameter("id");

        if (VideoDAO.isIdExist(videoId)) {
            VideoInteractionDTO videoInteraction = VideoDAO.getInteraction(videoId);
            Gson gson = new Gson();
            String videoInteractionJson = gson.toJson(videoInteraction);

            response.setContentType("application/json");
            response.getWriter().write(videoInteractionJson);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
