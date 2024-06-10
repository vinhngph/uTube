package com.utube.api.Video;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.utube.utils.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/video/thumbnail" })
public class GetThumbail extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String videoId = request.getParameter("id");
        if (videoId == null || videoId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing id parameter");
            return;
        }

        String storagePath = null;
        if (Config.getProperty("STORAGE_PATH") == null) {
            storagePath = request.getServletContext().getRealPath(File.separator + "storage");
        } else {
            storagePath = Config.getProperty("STORAGE_PATH");
        }

        String thumbnailPath = storagePath + File.separator + videoId + File.separator + videoId + ".png";
        Path thumbnailFilePath = Paths.get(thumbnailPath);

        if (!Files.exists(thumbnailFilePath)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Thumbnail not found");
            return;
        }

        response.setContentType("image/png");

        try (InputStream thumbnailStream = Files.newInputStream(thumbnailFilePath);
                OutputStream outStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = thumbnailStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error reading thumbnail file");
        }
    }
}
