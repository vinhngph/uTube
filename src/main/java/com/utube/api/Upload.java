package com.utube.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

import com.utube.daos.VideoDAO;
import com.utube.dtos.VideoDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(urlPatterns = { "/api/upload" })
@MultipartConfig()
public class Upload extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Part part = request.getPart("videoFile");

        String videoId = "";
        while (true) {
            videoId = VideoDAO.generateVideoId();
            if (!VideoDAO.isIdExist(videoId)) {
                break;
            }
        }

        String storagePath = request.getServletContext().getRealPath(File.separator + "storage");
        Path storage = Paths.get(storagePath);
        if (!Files.exists(storage)) {
            Files.createDirectory(storage);
        }

        String videoPath = storagePath + File.separator + videoId;
        Path video = Paths.get(videoPath);
        if (!Files.exists(video)) {
            Files.createDirectory(video);
        }

        String fileName = videoPath + File.separator + videoId + ".webm";

        part.write(fileName);

        int userId = Integer.parseInt(request.getParameter("userId"));
        String videoTitle = request.getParameter("title");
        String videoDescription = request.getParameter("description");
        String videoThumbnail = request.getParameter("thumbnail");
        Timestamp videoDate = new Timestamp(System.currentTimeMillis());

        VideoDTO videoDTO = new VideoDTO(videoId, videoTitle, videoDescription, videoThumbnail, videoDate);
        VideoDAO.addVideo(videoDTO, userId);

        // thumbnail image
    }
}
