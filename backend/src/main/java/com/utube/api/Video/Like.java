package com.utube.api.Video;

import java.io.IOException;

import com.utube.daos.VideoDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/video/like")
@MultipartConfig()
public class Like extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String video_id = request.getParameter("video_id");

        if (VideoDAO.isIdExist(video_id)) {
            int user_id = Integer.parseInt(request.getParameter("user_id"));
            boolean isDislike = VideoDAO.isDislike(user_id, video_id);

            if (isDislike) {
                VideoDAO.dislike(user_id, video_id);
                VideoDAO.like(user_id, video_id);
            } else {
                VideoDAO.like(user_id, video_id);
            }

            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
