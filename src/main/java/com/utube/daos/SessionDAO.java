package com.utube.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

    private static String formatDate(String input) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (z)");
            Date date = sdf.parse(input);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return localDateTime.format(formatter);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createSession(int userId, String inputSessionTime, String inputSessionDevice) {
        String query = "INSERT INTO Session (session_user, session_time, session_device) VALUES (?, ?, ?)";
        Timestamp sessionTime = Timestamp.valueOf(formatDate(inputSessionTime));
        String sessionDevice = getSessionDevice(inputSessionDevice);

        Connection conn = DBConnect.getConnection();
        try {
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
