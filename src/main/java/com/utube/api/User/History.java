package com.utube.api.User;

import java.io.IOException;
import java.time.Instant;

import com.utube.daos.AccountDAO;
import com.utube.daos.HistoryDAO;
import com.utube.daos.VideoDAO;
import com.utube.dtos.HistoryDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/user/history" })
@MultipartConfig()
public class History extends HttpServlet {
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

        String result = HistoryDAO.getHistory(userId);

        if (result == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setContentType("application/json");
            response.getWriter().write(result);
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));
        String videoId = request.getParameter("video_id");
        Instant trackDate = Instant.now();
        String trackTime = request.getParameter("track_time");

        if (userId == 0 || videoId == null || trackTime == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!VideoDAO.isIdExist(videoId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!AccountDAO.isUser(userId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        HistoryDTO history = new HistoryDTO(userId, videoId, trackDate.toString(), trackTime);
        String result = HistoryDAO.addHistory(history);
        if (result == null) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setContentType("application/json");
            response.getWriter().write(result);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));
        String videoId = request.getParameter("video_id");
        String trackDate = Instant.now().toString();
        String trackTime = request.getParameter("track_time"); // second

        if (userId == 0 || videoId == null || trackTime == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!VideoDAO.isIdExist(videoId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!AccountDAO.isUser(userId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        HistoryDTO history = new HistoryDTO(userId, videoId, trackDate, trackTime);
        boolean result = HistoryDAO.updateHistory(history);

        if (result) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

        if (!VideoDAO.isIdExist(videoId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!AccountDAO.isUser(userId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean result = HistoryDAO.deleteHistory(userId, videoId);

        if (result) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
