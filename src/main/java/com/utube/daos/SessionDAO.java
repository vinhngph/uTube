package com.utube.daos;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;

import com.utube.dtos.SessionDTO;
import com.utube.utils.DBConnect;

public class SessionDAO {
    private static String getSessionDevice(String input) {
        int startIndex = input.indexOf('(');
        int endIndex = input.indexOf(')');
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            String content = input.substring(startIndex + 1, endIndex);
            if (content.contains(";")) {
                int semicolonIndex = content.indexOf(';');
                return content.substring(0, semicolonIndex).trim();
            }
            return content.trim();
        }
        return null;
    }

    public static void createSession(int userId, String inputSessionDevice) {
        Connection conn = DBConnect.getConnection();

        try {
            Instant session_time = Instant.now();
            String query = "INSERT INTO Session (session_user, session_time, session_device) VALUES (?, ?, ?)";
            String sessionDevice = getSessionDevice(inputSessionDevice);
            if (sessionDevice == null) {
                sessionDevice = inputSessionDevice;
            }

            PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setTimestamp(2, Timestamp.from(session_time));
            ps.setString(3, sessionDevice);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static ArrayList<SessionDTO> getSession(int userId) {
        Connection conn = DBConnect.getConnection();
        ArrayList<SessionDTO> sessionList = new ArrayList<SessionDTO>();

        try {
            String query = "SELECT * FROM Session WHERE session_user = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int sessionId = rs.getInt("session_id");
                String sessionTime = rs.getTimestamp("session_time").toInstant().toString();
                String sessionDevice = rs.getString("session_device");

                SessionDTO session = new SessionDTO(sessionId, userId, sessionTime, sessionDevice);
                sessionList.add(session);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnect.closeConnection(conn);
        }
        return sessionList;
    }
}
