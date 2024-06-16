package com.utube.daos;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.utube.dtos.VideoCardDTO;
import com.utube.utils.DBConnect;

public class UserVideoDAO {
    public static boolean isOwner(int userId, String videoId) {
        Connection conn = DBConnect.getConnection();

        try {
            String isOwner = "SELECT user_id FROM Upload WHERE video_id = ?";
            PreparedStatement ps = conn.prepareStatement(isOwner);
            ps.setString(1, videoId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next() || (rs.getInt("user_id") != userId)) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static String getAllUploaded(int userId) {
        Connection conn = DBConnect.getConnection();
        Gson gson = new Gson();

        try {
            String query = "SELECT v.video_id,\n" +
                    "       v.video_title,\n" +
                    "       v.video_description,\n" +
                    "       v.video_date,\n" +
                    "       v.video_status,\n" +
                    "       v.video_id AS video_thumbnail\n" +
                    "FROM Video v\n" +
                    "         JOIN Upload u ON v.video_id = u.video_id\n" +
                    "WHERE u.user_id = ?\n" +
                    "ORDER BY v.video_date DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            ArrayList<VideoCardDTO> videos = new ArrayList<>();

            while (rs.next()) {
                VideoCardDTO video = new VideoCardDTO(
                        rs.getString("video_id"),
                        rs.getString("video_title"),
                        rs.getString("video_description"),
                        rs.getTimestamp("video_date").toInstant().toString(),
                        rs.getBoolean("video_status"),
                        rs.getString("video_thumbnail"),
                        userId);

                videos.add(video);
            }

            return gson.toJson(videos);
        } catch (SQLException e) {
            return gson.toJson(e.getMessage());
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static boolean updateVideo(String videoId, String videoTitle, String videoDescription, int userId) {
        Connection conn = DBConnect.getConnection();

        try {
            if (!isOwner(userId, videoId)) {
                return false;
            }

            String query = "UPDATE Video\n" +
                    "SET video_title = ?, video_description = ?\n" +
                    "WHERE video_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, videoTitle);
            ps.setString(2, videoDescription);
            ps.setString(3, videoId);

            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static boolean deleteVideo(String videoId, int userId, String storagePath) {
        Connection conn = DBConnect.getConnection();

        try {
            if (!isOwner(userId, videoId)) {
                return false;
            }

            String query_1 = "DELETE FROM Video_Like WHERE video_id = ?";
            String query_2 = "DELETE FROM Video_Dislike WHERE video_id = ?";
            String query_3 = "DELETE FROM Video_View WHERE video_id = ?";
            String query_4 = "DELETE FROM Upload WHERE video_id = ?";
            String query_5 = "DELETE FROM User_History WHERE video_id = ?";
            String query_6 = "DELETE FROM Video WHERE video_id = ?";

            PreparedStatement ps = conn.prepareStatement(query_1);
            ps.setString(1, videoId);
            ps.executeUpdate();

            ps = conn.prepareStatement(query_2);
            ps.setString(1, videoId);
            ps.executeUpdate();

            ps = conn.prepareStatement(query_3);
            ps.setString(1, videoId);
            ps.executeUpdate();

            ps = conn.prepareStatement(query_4);
            ps.setString(1, videoId);
            ps.executeUpdate();

            ps = conn.prepareStatement(query_5);
            ps.setString(1, videoId);
            ps.executeUpdate();

            ps = conn.prepareStatement(query_6);
            ps.setString(1, videoId);
            ps.executeUpdate();

            String videoPath = storagePath + File.separator + videoId;
            File videoDir = new File(videoPath);

            if (videoDir.exists()) {
                File[] files = videoDir.listFiles();
                for (File file : files) {
                    file.delete();
                }
                videoDir.delete();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }
}
