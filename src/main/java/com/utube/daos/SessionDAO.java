package com.utube.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

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

    public static void createSession(int userId, Timestamp sessionTime, String inputSessionDevice) {
        Connection conn = DBConnect.getConnection();
        try {
            String query = "INSERT INTO Session (session_user, session_time, session_device) VALUES (?, ?, ?)";
            String sessionDevice = getSessionDevice(inputSessionDevice);
            if (sessionDevice == null) {
                sessionDevice = inputSessionDevice;
            }

            PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setTimestamp(2, sessionTime);
            ps.setString(3, sessionDevice);
            ps.executeUpdate();

            DBConnect.closeConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBConnect.closeConnection(conn);
    }
}
