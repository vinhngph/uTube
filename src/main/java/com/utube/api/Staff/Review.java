package com.utube.api.Staff;

import java.io.IOException;

import com.utube.daos.AccountDAO;
import com.utube.daos.StaffDAO;
import com.utube.daos.VideoDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/staff/review" })
@MultipartConfig()
public class Review extends HttpServlet {
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

        if (!StaffDAO.isStaff(userId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String result = StaffDAO.getReviewVideos();

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
}
