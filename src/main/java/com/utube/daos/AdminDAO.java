package com.utube.daos;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.utube.dtos.VideoDTO;
import com.utube.utils.DBConnect;

public class AdminDAO {
    public static boolean isStaff(int userId) {
        Connection conn = DBConnect.getConnection();
        try {
            String query = "SELECT user_role FROM User WHERE user_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_role") == 1 || rs.getInt("user_role") == 2;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static String getReviewVideos() {
        Connection conn = DBConnect.getConnection();
        Gson gson = new Gson();

        try {
            String query = "SELECT v.video_id,\n" +
                    "       v.video_title,\n" +
                    "       v.video_description,\n" +
                    "       v.video_date,\n" +
                    "       v.video_status,\n" +
                    "       v.video_id                                                                               AS video_thumbnail,\n"
                    +
                    "       CAST((SELECT COUNT(*) FROM Video_Like vl WHERE vl.video_id = v.video_id) AS UNSIGNED)    AS video_like,\n"
                    +
                    "       CAST((SELECT COUNT(*) FROM Video_Dislike vd WHERE vd.video_id = v.video_id) AS UNSIGNED) AS video_dislike,\n"
                    +
                    "       CAST(vv.video_view AS UNSIGNED)                                                          AS video_views,\n"
                    +
                    "       ui.user_fullname                                                                         AS video_channel_name,\n"
                    +
                    "       u.user_id                                                                                AS video_channel_id\n"
                    +
                    "FROM Video v\n" +
                    "         JOIN\n" +
                    "     Video_View vv ON v.video_id = vv.video_id\n" +
                    "         JOIN\n" +
                    "     Upload u ON v.video_id = u.video_id\n" +
                    "         JOIN\n" +
                    "     User_Information ui ON u.user_id = ui.user_id\n" +
                    "WHERE v.video_status = false\n" +
                    "ORDER BY vv.video_view DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            ArrayList<VideoDTO> videos = new ArrayList<>();

            while (rs.next()) {
                VideoDTO video = new VideoDTO(
                        rs.getString("video_id"),
                        rs.getString("video_title"),
                        rs.getString("video_description"),
                        rs.getTimestamp("video_date").toInstant().toString(),
                        rs.getBoolean("video_status"),
                        rs.getString("video_thumbnail"),
                        rs.getLong("video_like"),
                        rs.getLong("video_dislike"),
                        rs.getLong("video_views"),
                        rs.getString("video_channel_name"),
                        rs.getInt("video_channel_id"));
                videos.add(video);
            }
            return gson.toJson(videos);
        } catch (SQLException e) {
            return gson.toJson(e.getMessage());
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static boolean deleteVideo(String videoId, String storagePath) {
        Connection conn = DBConnect.getConnection();

        try {
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
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }
}
