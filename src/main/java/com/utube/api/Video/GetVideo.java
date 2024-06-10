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

import com.utube.daos.VideoDAO;
import com.utube.utils.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/video" })
public class GetVideo extends HttpServlet {
    private static final int DEFAULT_BUFFER_SIZE = 10240;

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
            String storagePath = null;
            if (Config.getProperty("STORAGE_PATH") == null) {
                storagePath = request.getServletContext().getRealPath(File.separator + "storage");
            } else {
                storagePath = Config.getProperty("STORAGE_PATH");
            }

            String videoPath = storagePath + File.separator + videoId + File.separator + videoId + ".webm";
            Path videoFilePath = Paths.get(videoPath);

            if (!Files.exists(videoFilePath)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Video not found");
                return;
            }

            String range = request.getHeader("Range");
            if (range == null) {
                // If there's no Range header, serve the entire video file.
                range = "bytes=0-";
            }

            // Extract the range values.
            long fileLength = Files.size(videoFilePath);
            long start = 0;
            long end = fileLength - 1;

            Matcher matcher = Pattern.compile("bytes=(\\d*)-(\\d*)").matcher(range);
            if (matcher.matches()) {
                String startGroup = matcher.group(1);
                if (startGroup.length() > 0) {
                    start = Long.parseLong(startGroup);
                }
                String endGroup = matcher.group(2);
                if (endGroup.length() > 0) {
                    end = Long.parseLong(endGroup);
                }
            }

            // Validate range
            if (start > end || start < 0 || end >= fileLength) {
                response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                response.setHeader("Content-Range", "bytes */" + fileLength); // Required in 416.
                return;
            }

            // Set the response headers.
            response.setContentType("video/webm");
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            response.setHeader("Content-Length", String.valueOf(end - start + 1));
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

            // Copy the requested range of the file to the response.
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
}
