package com.utube.api.Video;

import java.io.IOException;

import com.google.gson.Gson;
import com.utube.daos.VideoDAO;
import com.utube.dtos.VideoInformationDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/video/information" })
public class GetVideoInformation extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String videoId = request.getParameter("id");

        if (VideoDAO.isIdExist(videoId)) {
            VideoInformationDTO video = VideoDAO.getVideoInformation(videoId);
            Gson gson = new Gson();
            String videoJson = gson.toJson(video);

            response.setContentType("application/json");
            response.getWriter().write(videoJson);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
