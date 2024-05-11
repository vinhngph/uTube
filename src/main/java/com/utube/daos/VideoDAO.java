package com.utube.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.utube.dtos.VideoDTO;
import com.utube.utils.DBConnect;

public class VideoDAO {
    public static boolean isIdExist(String videoId) {
        Connection conn = DBConnect.getConnection();
        String sql = "SELECT video_title FROM Video WHERE video_id = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, videoId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DBConnect.closeConnection(conn);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBConnect.closeConnection(conn);
        return false;
    }

    public static String generateVideoId() {
        String videoId = "";
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 11; i++) {
            int index = (int) (Math.random() * characters.length());
            videoId += characters.charAt(index);
        }
        return videoId;
    }

    public static void addVideo(VideoDTO video, int userId) {
        Connection conn = DBConnect.getConnection();

        String addVideo = "INSERT INTO Video (video_id, video_title, video_description, video_date) VALUES (?, ?, ?, ?)";

        String addVideoUser = "INSERT INTO Upload (video_id, user_id) VALUES (?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(addVideo);
            ps.setString(1, video.getVideoId());
            ps.setString(2, video.getVideoTitle());
            ps.setString(3, video.getVideoDescription());
            ps.setTimestamp(4, video.getVideoDate());
            ps.executeUpdate();

            ps = conn.prepareStatement(addVideoUser);
            ps.setString(1, video.getVideoId());
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnect.closeConnection(conn);
        }
    }
}
