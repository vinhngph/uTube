package com.utube.api.Video;

import java.io.IOException;

import com.utube.daos.VideoDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/video/dislike")
@MultipartConfig()
public class Dislike extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String video_id = request.getParameter("video_id");

        if (VideoDAO.isIdExist(video_id)) {
            int user_id = Integer.parseInt(request.getParameter("user_id"));
            boolean isLike = VideoDAO.isLike(user_id, video_id);

            if (isLike) {
                VideoDAO.like(user_id, video_id);
                VideoDAO.dislike(user_id, video_id);
            } else {
                VideoDAO.dislike(user_id, video_id);
            }

            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
