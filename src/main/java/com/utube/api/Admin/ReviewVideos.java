package com.utube.api.Admin;

import java.io.File;
import java.io.IOException;

import com.utube.daos.AccountDAO;
import com.utube.daos.AdminDAO;
import com.utube.daos.VideoDAO;
import com.utube.utils.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/admin/review" })
@MultipartConfig()
public class ReviewVideos extends HttpServlet {
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

        if (!AdminDAO.isStaff(userId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String result = AdminDAO.getReviewVideos();

        response.setContentType("application/json");
        response.getWriter().write(result);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String videoId = request.getParameter("video_id");
        int userId = Integer.parseInt(request.getParameter("user_id"));

        if (userId == 0 || videoId == null || videoId.isEmpty()) {
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

        int userRole = AccountDAO.getRole(userId);
        if (userRole == 2 || userRole == 1) {
            VideoDAO.toggleStatus(videoId);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));
        String videoId = request.getParameter("video_id");

        if (userId == 0 || videoId == null || videoId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!AccountDAO.isAdmin(userId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String storagePath = null;
        if (Config.getProperty("STORAGE_PATH") == null) {
            storagePath = request.getServletContext().getRealPath(File.separator + "storage");
        } else {
            storagePath = Config.getProperty("STORAGE_PATH");
        }

        if (AdminDAO.deleteVideo(videoId, storagePath)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
