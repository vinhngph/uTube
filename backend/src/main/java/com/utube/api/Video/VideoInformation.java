package com.utube.api.Video;

import java.io.IOException;

import com.utube.daos.VideoDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/video/information" })
@MultipartConfig()
public class VideoInformation extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String videoId = request.getParameter("id");
        if (!VideoDAO.isIdExist(videoId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String result = VideoDAO.getVideoInformation(videoId);
        if (result == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } else {
            response.setContentType("application/json");
            response.getWriter().write(result);
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
