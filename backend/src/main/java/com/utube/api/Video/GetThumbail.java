package com.utube.api.Video;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.utube.daos.VideoDAO;
import com.utube.utils.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/video/thumbnail" })
@MultipartConfig()
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

        if (VideoDAO.isIdExist(videoId)) {
            String storagePath = null;
            if (Config.getProperty("STORAGE_PATH") == null) {
                storagePath = request.getServletContext().getRealPath(File.separator + "storage");
            } else {
                storagePath = Config.getProperty("STORAGE_PATH");
            }

            String thumbnailPath = storagePath + File.separator + videoId;

            if (!Files.exists(Paths.get(thumbnailPath))) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Thumbnail not found");
                return;
            }

            FilenameFilter imageFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String[] imageExtensions = { ".png", ".jpg", ".jpeg" , ".webp"};
                    for (String extension : imageExtensions) {
                        if (name.toLowerCase().endsWith(extension)) {
                            return true;
                        }
                    }
                    return false;
                }
            };

            File dir = new File(thumbnailPath);
            File[] files = dir.listFiles(imageFilter);
            File thumbnailFile = files[0];
            Path thumbnailFilePath = thumbnailFile.toPath();

            String thumbExt = thumbnailFilePath.toString().substring(thumbnailFilePath.toString().lastIndexOf(".") + 1);
            response.setContentType("image/" + thumbExt);

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
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
