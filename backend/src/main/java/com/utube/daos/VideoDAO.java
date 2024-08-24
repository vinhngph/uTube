package com.utube.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import com.google.gson.Gson;
import com.utube.dtos.VideoInformationDTO;
import com.utube.dtos.VideoInteractionDTO;
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

    private static String generateVideoId() {
        String videoId = "";
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 11; i++) {
            int index = (int) (Math.random() * characters.length());
            videoId += characters.charAt(index);
        }
        return videoId;
    }

    public static String sendVideoId() {
        String videoId = "";
        while (true) {
            videoId = generateVideoId();
            if (!isIdExist(videoId)) {
                break;
            }
        }
        return videoId;
    }

    public static boolean addVideo(VideoInformationDTO video, int userId) {
        Connection conn = DBConnect.getConnection();

        try {
            String addVideo = "INSERT INTO Video (video_id, video_title, video_description, video_date, video_status) VALUES (?, ?, ?, ?, ?)";
            String addVideoUser = "INSERT INTO Upload (video_id, user_id) VALUES (?, ?)";
            String defaultView = "INSERT INTO Video_View (video_id, video_view) VALUES (?, ?)";

            Instant video_date = Instant.parse(video.getVideoDate());

            PreparedStatement ps = conn.prepareStatement(addVideo);
            ps.setString(1, video.getVideoId());
            ps.setString(2, video.getVideoTitle());
            ps.setString(3, video.getVideoDescription());
            ps.setTimestamp(4, Timestamp.from(video_date));
            ps.setBoolean(5, false);
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

    public static boolean isLike(int user_id, String video_id) {
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

    public static void like(int user_id, String video_id) {
        boolean isLike = isLike(user_id, video_id);

        Connection conn = DBConnect.getConnection();
        if (isLike) {
            String sql = "DELETE FROM Video_Like WHERE user_id = ? AND video_id = ?";
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, user_id);
                ps.setString(2, video_id);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
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
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnect.closeConnection(conn);
            }
        }
    }

    public static boolean isDislike(int user_id, String video_id) {
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

    public static void dislike(int user_id, String video_id) {
        boolean isDislike = isDislike(user_id, video_id);

        Connection conn = DBConnect.getConnection();
        if (isDislike) {
            String sql = "DELETE FROM Video_Dislike WHERE user_id = ? AND video_id = ?";
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, user_id);
                ps.setString(2, video_id);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
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
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnect.closeConnection(conn);
            }
        }
    }

    public static boolean addView(String videoId) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "UPDATE Video_View SET video_view = video_view + 1 WHERE video_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, videoId);
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static VideoInteractionDTO getInteraction(String videoId) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "SELECT\n" +
                    "    v.video_id,\n" +
                    "    COALESCE(like_count.video_like, 0) AS video_like,\n" +
                    "    COALESCE(dislike_count.video_dislike, 0) AS video_dislike,\n" +
                    "    COALESCE(vv.video_view, 0) AS video_view\n" +
                    "FROM\n" +
                    "    Video v\n" +
                    "LEFT JOIN\n" +
                    "    (SELECT video_id, COUNT(user_id) AS video_like\n" +
                    "     FROM Video_Like\n" +
                    "     GROUP BY video_id) like_count ON v.video_id = like_count.video_id\n" +
                    "LEFT JOIN\n" +
                    "    (SELECT video_id, COUNT(user_id) AS video_dislike\n" +
                    "     FROM Video_Dislike\n" +
                    "     GROUP BY video_id) dislike_count ON v.video_id = dislike_count.video_id\n" +
                    "LEFT JOIN\n" +
                    "    Video_View vv ON v.video_id = vv.video_id\n" +
                    "WHERE\n" +
                    "    v.video_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, videoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new VideoInteractionDTO(rs.getString("video_id"), rs.getLong("video_like"),
                        rs.getLong("video_dislike"), rs.getLong("video_view"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static String getVideoInformation(String video_id) {
        Connection conn = DBConnect.getConnection();
        Gson gson = new Gson();

        try {
            String query = "SELECT \n" +
                    "    v.video_id,\n" +
                    "    v.video_title,\n" +
                    "    v.video_description,\n" +
                    "    v.video_date,\n" +
                    "    v.video_status,\n" +
                    "    u.user_id AS video_owner\n" +
                    "FROM \n" +
                    "    Video v\n" +
                    "JOIN \n" +
                    "    Upload u ON v.video_id = u.video_id\n" +
                    "WHERE \n" +
                    "    v.video_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, video_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String videoId = rs.getString("video_id");
                String videoTitle = rs.getString("video_title");
                String videoDescription = rs.getString("video_description");
                String videoDate = rs.getTimestamp("video_date").toInstant().toString();
                boolean videoStatus = rs.getBoolean("video_status");
                int videoOwner = rs.getInt("video_owner");

                VideoInformationDTO video = new VideoInformationDTO(videoId, videoTitle, videoDescription, videoDate,
                        videoStatus, videoOwner);

                return gson.toJson(video);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return gson.toJson(e.getMessage());
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static void toggleStatus(String videoId) {
        Connection conn = DBConnect.getConnection();

        try {
            String getStatus = "SELECT video_status FROM Video WHERE video_id = ?";
            PreparedStatement ps = conn.prepareStatement(getStatus);
            ps.setString(1, videoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean status = rs.getBoolean("video_status");

                if (status) {
                    String query = "UPDATE Video SET video_status = false WHERE video_id = ?";
                    ps = conn.prepareStatement(query);
                    ps.setString(1, videoId);
                    ps.executeUpdate();
                } else {
                    String query = "UPDATE Video SET video_status = true WHERE video_id = ?";
                    ps = conn.prepareStatement(query);
                    ps.setString(1, videoId);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static String getVideoName(String videoId) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "SELECT video_title FROM Video WHERE video_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, videoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("video_title");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }
}
