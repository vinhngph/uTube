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

    public static boolean addVideo(VideoDTO video, int userId) {
        Connection conn = DBConnect.getConnection();

        try {
            String addVideo = "INSERT INTO Video (video_id, video_title, video_description, video_date) VALUES (?, ?, ?, ?)";
            String addVideoUser = "INSERT INTO Upload (video_id, user_id) VALUES (?, ?)";
            String defaultView = "INSERT INTO Video_View (video_id, video_view) VALUES (?, ?)";

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

            ps = conn.prepareStatement(defaultView);
            ps.setString(1, video.getVideoId());
            ps.setInt(2, 0);
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    private static boolean isLike(int user_id, String video_id) {
        Connection conn = DBConnect.getConnection();

        try {
            String sql = "SELECT * FROM Video_Like WHERE user_id = ? AND video_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, user_id);
            ps.setString(2, video_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static boolean like(int user_id, String video_id) {
        boolean isLike = isLike(user_id, video_id);

        Connection conn = DBConnect.getConnection();
        if (isLike) {
            String sql = "DELETE FROM Video_Like WHERE user_id = ? AND video_id = ?";
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, user_id);
                ps.setString(2, video_id);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                DBConnect.closeConnection(conn);
            }
        } else {
            String sql = "INSERT INTO Video_Like (user_id, video_id) VALUES (?, ?)";
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, user_id);
                ps.setString(2, video_id);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                DBConnect.closeConnection(conn);
            }
        }
    }

    private static boolean isDislike(int user_id, String video_id) {
        Connection conn = DBConnect.getConnection();

        try {
            String sql = "SELECT * FROM Video_Dislike WHERE user_id = ? AND video_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, user_id);
            ps.setString(2, video_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static boolean dislike(int user_id, String video_id) {
        boolean isDislike = isDislike(user_id, video_id);

        Connection conn = DBConnect.getConnection();
        if (isDislike) {
            String sql = "DELETE FROM Video_Dislike WHERE user_id = ? AND video_id = ?";
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, user_id);
                ps.setString(2, video_id);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                DBConnect.closeConnection(conn);
            }
        } else {
            String sql = "INSERT INTO Video_Dislike (user_id, video_id) VALUES (?, ?)";
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, user_id);
                ps.setString(2, video_id);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                DBConnect.closeConnection(conn);
            }
        }
    }
}
