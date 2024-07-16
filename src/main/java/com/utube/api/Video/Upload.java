package com.utube.api.Video;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.utube.daos.AccountDAO;
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
    private static final Map<String, Integer> videoChunkCount = new HashMap<>();
    private static final Map<String, Integer> thumbnailChunkCount = new HashMap<>();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");

        if (type.equals("video")) {
            // Get request parameters
            Part chunk = request.getPart("chunk");
            String extension = request.getParameter("ext");
            String videoId = request.getParameter("videoId");
            int index = Integer.parseInt(request.getParameter("index"));
            int totalChunks = Integer.parseInt(request.getParameter("totalChunks"));

            if (chunk == null || extension == null || videoId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            // ----------------------------------------------------------------------------------------

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
            // ----------------------------------------------------------------------------------------

            // Prepare path for video
            String videoPath = storagePath + File.separator + videoId;
            Path videoDir = Paths.get(videoPath);
            if (!Files.exists(videoDir)) {
                Files.createDirectory(videoDir);
            }
            // ----------------------------------------------------------------------------------------

            // Save chunk to disk
            File fileChunk = new File(videoPath, videoId + "_chunk_" + index + "_video_");
            try (InputStream input = chunk.getInputStream(); OutputStream output = new FileOutputStream(fileChunk)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }

            videoChunkCount.put(videoId, videoChunkCount.getOrDefault(videoId, 0) + 1);

            if (videoChunkCount.get(videoId) == totalChunks) {
                assembleChunks(videoId, totalChunks, videoPath, extension, "_video_");
                videoChunkCount.remove(videoId);

                response.setStatus(HttpServletResponse.SC_CREATED);
                return;
            }
            // ----------------------------------------------------------------------------------------
        } else if (type.equals("thumbnail")) {
            Part chunk = request.getPart("chunk");
            String extension = request.getParameter("ext");
            String videoId = request.getParameter("videoId");
            int index = Integer.parseInt(request.getParameter("index"));
            int totalChunks = Integer.parseInt(request.getParameter("totalChunks"));

            if (chunk == null || extension == null || videoId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            // ----------------------------------------------------------------------------------------

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
            // ----------------------------------------------------------------------------------------

            // Prepare path for thumbnail
            String videoPath = storagePath + File.separator + videoId;
            Path videoDir = Paths.get(videoPath);
            if (!Files.exists(videoDir)) {
                Files.createDirectory(videoDir);
            }
            // ----------------------------------------------------------------------------------------

            // Save chunk to disk
            File fileChunk = new File(videoPath, videoId + "_chunk_" + index + "_thumbnail_");
            try (InputStream input = chunk.getInputStream(); OutputStream output = new FileOutputStream(fileChunk)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }

            thumbnailChunkCount.put(videoId, thumbnailChunkCount.getOrDefault(videoId, 0) + 1);

            if (thumbnailChunkCount.get(videoId) == totalChunks) {
                assembleChunks(videoId, totalChunks, videoPath, extension, "_thumbnail_");
                thumbnailChunkCount.remove(videoId);

                response.setStatus(HttpServletResponse.SC_CREATED);
                return;
            }
            // ----------------------------------------------------------------------------------------
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void assembleChunks(String fileName, int totalChunks, String videoPath, String extension, String suffix)
            throws IOException {
        File finalFile = new File(videoPath, fileName + "." + extension);

        try (OutputStream output = new FileOutputStream(finalFile)) {
            int i;
            for (i = 0; i < totalChunks; i++) {
                File chunkFile = new File(videoPath, fileName + "_chunk_" + i + suffix);
                try (InputStream input = new FileInputStream(chunkFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
                chunkFile.delete();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        String videoTitle = request.getParameter("title");
        String videoDescription = request.getParameter("description");

        if (userId == 0 || videoTitle == null || videoDescription == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (AccountDAO.isUser(userId) == false) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String videoId = VideoDAO.sendVideoId();
        String videoDate = Instant.now().toString();

        VideoInformationDTO videoDTO = new VideoInformationDTO(videoId, videoTitle, videoDescription, videoDate, false,
                userId);

        if (VideoDAO.addVideo(videoDTO, userId)) {
            response.setContentType("application/json");
            response.getWriter().write("{\"videoId\":\"" + videoId + "\"}");
        }
    }
}
