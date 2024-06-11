package com.utube.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.utube.dtos.VideoDTO;
import com.utube.utils.DBConnect;

public class HomeDAO {
    public static String getVideos() {
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
                    "ORDER BY video_views DESC\n" +
                    "LIMIT 20";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            ArrayList<VideoDTO> videos = new ArrayList<>();
            while (rs.next()) {
                String videoId = rs.getString("video_id");
                String videoTitle = rs.getString("video_title");
                String videoDescription = rs.getString("video_description");
                String videoDate = rs.getTimestamp("video_date").toInstant().toString();
                boolean videoStatus = rs.getBoolean("video_status");
                String videoThumbnail = rs.getString("video_thumbnail");
                Long videoLike = rs.getLong("video_like");
                long videoDislike = rs.getLong("video_dislike");
                long videoViews = rs.getLong("video_views");
                String videoChannelName = rs.getString("video_channel_name");
                int videoChannelId = rs.getInt("video_channel_id");

                VideoDTO video = new VideoDTO(videoId, videoTitle, videoDescription, videoDate, videoStatus,
                        videoThumbnail, videoLike, videoDislike, videoViews, videoChannelName, videoChannelId);
                videos.add(video);
            }

            return gson.toJson(videos);
        } catch (SQLException e) {
            return gson.toJson(e.getMessage());
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static String searchName(String keyWord) {
        Connection conn = DBConnect.getConnection();
        Gson gson = new Gson();

        try {
            String query = "SELECT DISTINCT video_title FROM Video WHERE video_title LIKE ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%" + keyWord + "%");
            ResultSet rs = ps.executeQuery();

            ArrayList<Map<String, String>> names = new ArrayList<>();

            while (rs.next()) {
                Map<String, String> name = new HashMap<>();
                name.put("name", rs.getString("video_title"));
                names.add(name);
            }

            return gson.toJson(names);
        } catch (SQLException e) {
            return gson.toJson(e.getMessage());
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static String searchResults(String keyWord) {
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
                    "WHERE video_title LIKE ?\n" +
                    "ORDER BY vv.video_view DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%" + keyWord + "%");
            ResultSet rs = ps.executeQuery();

            ArrayList<VideoDTO> videos = new ArrayList<>();

            while (rs.next()) {
                String videoId = rs.getString("video_id");
                String videoTitle = rs.getString("video_title");
                String videoDescription = rs.getString("video_description");
                String videoDate = rs.getTimestamp("video_date").toInstant().toString();
                boolean videoStatus = rs.getBoolean("video_status");
                String videoThumbnail = rs.getString("video_thumbnail");
                Long videoLike = rs.getLong("video_like");
                long videoDislike = rs.getLong("video_dislike");
                long videoViews = rs.getLong("video_views");
                String videoChannelName = rs.getString("video_channel_name");
                int videoChannelId = rs.getInt("video_channel_id");

                VideoDTO video = new VideoDTO(videoId, videoTitle, videoDescription, videoDate, videoStatus,
                        videoThumbnail, videoLike, videoDislike, videoViews, videoChannelName, videoChannelId);
                videos.add(video);
            }

            return gson.toJson(videos);
        } catch (SQLException e) {
            return gson.toJson(e.getMessage());
        } finally {
            DBConnect.closeConnection(conn);
        }
    }
}
