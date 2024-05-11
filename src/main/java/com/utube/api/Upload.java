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
        // Generate video id
        String videoId = "";
        while (true) {
            videoId = VideoDAO.generateVideoId();
            if (!VideoDAO.isIdExist(videoId)) {
                break;
            }
        }

        // Prepare path for storage
        String storagePath = request.getServletContext().getRealPath(File.separator + "storage");
        Path storage = Paths.get(storagePath);
        if (!Files.exists(storage)) {
            Files.createDirectory(storage);
        }

        // Prepare path for video
        String videoPath = storagePath + File.separator + videoId;
        Path videoDir = Paths.get(videoPath);
        if (!Files.exists(videoDir)) {
            Files.createDirectory(videoDir);
        }

        // Get video file
        Part video = request.getPart("videoFile");
        // Save video
        String videoName = videoPath + File.separator + videoId + ".webm";
        video.write(videoName);

        // Get thumbnail file
        Part thumbnail = request.getPart("thumbnailFile");
        // Save thumbnail
        String thumbnailName = videoPath + File.separator + videoId + ".png";
        thumbnail.write(thumbnailName);

        // Video's information
        int userId = Integer.parseInt(request.getParameter("userId"));
        String videoTitle = request.getParameter("title");
        String videoDescription = request.getParameter("description");
        Timestamp videoDate = new Timestamp(System.currentTimeMillis());

        VideoDTO videoDTO = new VideoDTO(videoId, videoTitle, videoDescription, videoDate);
        VideoDAO.addVideo(videoDTO, userId);
    }
}
