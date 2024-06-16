package com.utube.api.User;

import java.io.File;
import java.io.IOException;

import com.utube.daos.AccountDAO;
import com.utube.daos.UserVideoDAO;
import com.utube.daos.VideoDAO;
import com.utube.utils.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/user/video/management" })
@MultipartConfig()
public class VideoManage extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));

        if (userId == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!AccountDAO.isUser(userId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String result = UserVideoDAO.getAllUploaded(userId);
        response.setContentType("application/json");
        response.getWriter().write(result);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));
        String videoId = request.getParameter("video_id");
        String videoTitle = request.getParameter("video_title");
        String videoDescription = request.getParameter("video_description");

        if (videoId == null || videoTitle == null || videoDescription == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (videoId.isEmpty() || videoTitle.isEmpty() || videoDescription.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!AccountDAO.isUser(userId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!VideoDAO.isIdExist(videoId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!UserVideoDAO.updateVideo(videoId, videoTitle, videoDescription, userId)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));
        String videoId = request.getParameter("video_id");

        if (userId == 0 || videoId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!AccountDAO.isUser(userId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!VideoDAO.isIdExist(videoId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String storagePath = null;
        if (Config.getProperty("STORAGE_PATH") == null) {
            storagePath = request.getServletContext().getRealPath(File.separator + "storage");
        } else {
            storagePath = Config.getProperty("STORAGE_PATH");
        }

        if (!UserVideoDAO.deleteVideo(videoId, userId, storagePath)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
