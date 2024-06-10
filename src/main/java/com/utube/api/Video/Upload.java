package com.utube.api.Video;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import com.utube.daos.VideoDAO;
import com.utube.dtos.VideoInformationDTO;
import com.utube.utils.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(urlPatterns = { "/api/video/upload" })
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
        String storagePath = null;
        if (Config.getProperty("STORAGE_PATH") == null) {
            storagePath = request.getServletContext().getRealPath(File.separator + "storage");
        } else {
            storagePath = Config.getProperty("STORAGE_PATH");
        }

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
        String videoDate = Instant.now().toString();

        VideoInformationDTO videoDTO = new VideoInformationDTO(videoId, videoTitle, videoDescription, videoDate);

        if (VideoDAO.addVideo(videoDTO, userId)) {
            response.setContentType("application/json");
            response.getWriter().write("{\"videoId\":\"" + videoId + "\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
