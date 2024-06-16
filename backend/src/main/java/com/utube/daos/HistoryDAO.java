package com.utube.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.utube.dtos.HistoryCardDTO;
import com.utube.dtos.HistoryDTO;
import com.utube.utils.Config;
import com.utube.utils.DBConnect;

public class HistoryDAO {
    public static boolean addHistory(HistoryDTO history) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "INSERT INTO User_History (user_id, video_id, track_date, track_time) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, history.getUserId());
            ps.setString(2, history.getVideoId());
            ps.setTimestamp(3, Timestamp.from(Instant.parse(history.getTrackDate())));
            ps.setString(4, history.getTrackTime());

            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static String getHistory(int userId) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "SELECT \n" +
                    "    v.video_id,\n" +
                    "    v.video_title,\n" +
                    "    v.video_description,\n" +
                    "    v.video_date,\n" +
                    "    v.video_status,\n" +
                    "    v.video_id AS video_thumbnail,\n" +
                    "    uh.track_date,\n" +
                    "    uh.track_time\n" +
                    "FROM \n" +
                    "    Video v\n" +
                    "JOIN \n" +
                    "    User_History uh ON v.video_id = uh.video_id\n" +
                    "WHERE \n" +
                    "    uh.user_id = ?\n" +
                    "ORDER BY \n" +
                    "    uh.track_date DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            ArrayList<HistoryCardDTO> histories = new ArrayList<>();

            while (rs.next()) {
                HistoryCardDTO history = new HistoryCardDTO(
                        rs.getString("video_id"),
                        rs.getString("video_title"),
                        rs.getString("video_description"),
                        rs.getTimestamp("video_date").toInstant().toString(),
                        rs.getBoolean("video_status"),
                        Config.getProperty("SERVER_URL") + "/api/video/thumbnail?id=" + rs.getString("video_thumbnail"),
                        rs.getTimestamp("track_date").toInstant().toString(),
                        rs.getString("track_time"));
                histories.add(history);
            }

            Gson gson = new Gson();
            return gson.toJson(histories);
        } catch (SQLException e) {
            Gson gson = new Gson();
            return gson.toJson(e.getMessage());
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static boolean updateHistory(HistoryDTO history) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "UPDATE User_History SET track_date = ?, track_time = ? WHERE user_id = ? AND video_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setTimestamp(1, Timestamp.from(Instant.parse(history.getTrackDate())));
            ps.setString(2, history.getTrackTime());
            ps.setInt(3, history.getUserId());
            ps.setString(4, history.getVideoId());

            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static boolean deleteHistory(int userId, String videoId) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "DELETE FROM User_History WHERE user_id = ? AND video_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setString(2, videoId);

            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static String getTrackTime(int userId, String videoId) {
        Connection conn = DBConnect.getConnection();
        Gson gson = new Gson();

        try {
            String query = "SELECT track_time FROM User_History WHERE user_id = ? AND video_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setString(2, videoId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Map<String, Double> trackTime = new HashMap<>();
                trackTime.put("trackTime", Double.parseDouble(rs.getString("track_time")));
                return gson.toJson(trackTime);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }
}
