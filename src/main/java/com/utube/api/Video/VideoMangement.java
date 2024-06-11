package com.utube.api.Video;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.utube.daos.AccountDAO;
import com.utube.daos.VideoDAO;
import com.utube.utils.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/video" })
@MultipartConfig()
public class VideoMangement extends HttpServlet {
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String videoId = request.getParameter("id");

        if (videoId == null || videoId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing id parameter");
            return;
        }

        if (VideoDAO.isIdExist(videoId)) {
            String storagePath = Config.getProperty("STORAGE_PATH");
            if (storagePath == null) {
                storagePath = request.getServletContext().getRealPath(File.separator +
                        "storage");
            }

            String videoPath = storagePath + File.separator + videoId + File.separator +
                    videoId + ".webm";
            Path videoFilePath = Paths.get(videoPath);

            if (!Files.exists(videoFilePath)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Video not found");
                return;
            }

            String range = request.getHeader("Range");
            if (range == null) {
                range = "bytes=0-";
            }

            long fileLength = Files.size(videoFilePath);
            long start = 0;
            long end = fileLength - 1;

            Matcher matcher = Pattern.compile("bytes=(\\d*)-(\\d*)").matcher(range);
            if (matcher.matches()) {
                String startGroup = matcher.group(1);
                if (!startGroup.isEmpty()) {
                    start = Long.parseLong(startGroup);
                }
                String endGroup = matcher.group(2);
                if (!endGroup.isEmpty()) {
                    end = Long.parseLong(endGroup);
                }
            }

            if (start > end || start < 0 || end >= fileLength) {
                response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                response.setHeader("Content-Range", "bytes */" + fileLength);
                return;
            }

            String videoName = VideoDAO.getVideoName(videoId);

            response.setContentType("video/webm");
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" +
                    fileLength);
            response.setHeader("Content-Length", String.valueOf(end - start + 1));
            response.setHeader("Content-Disposition", "inline; filename=\"" + videoName + ".webm\"");
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

            try (InputStream input = Files.newInputStream(videoFilePath);
                    OutputStream output = response.getOutputStream()) {
                input.skip(start);
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int bytesRead;
                long bytesToRead = end - start + 1;
                while ((bytesRead = input.read(buffer)) != -1) {
                    if (bytesToRead > bytesRead) {
                        output.write(buffer, 0, bytesRead);
                        bytesToRead -= bytesRead;
                    } else {
                        output.write(buffer, 0, (int) bytesToRead);
                        break;
                    }
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String videoId = request.getParameter("video_id");
        boolean isExist = VideoDAO.isIdExist(videoId);

        if (isExist) {
            int userId = Integer.parseInt(request.getParameter("user_id"));
            int userRole = AccountDAO.getRole(userId);
            if (userRole == 2 || userRole == 1) {
                VideoDAO.toggleStatus(videoId);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
