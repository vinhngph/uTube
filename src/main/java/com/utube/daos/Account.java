package com.utube.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.utube.dtos.User;
import com.utube.utils.DBConnect;

public class Account {
    public static boolean registerUser(String email, String username, String password, String fullName, Date dob,
            String sessionDevice, Timestamp sessionTime) {
        Connection conn = DBConnect.getConnection();

        try {
            String user = "INSERT INTO User (user_username, user_email, user_password, user_role) VALUES (?, ?, ?, ?)";
            String info = "INSERT INTO UserInformation (user_id, user_fullname, user_dob) VALUES (?, ?, ?)";
            String session = "INSERT INTO Session (session_user, session_time, session_device) VALUES (?, ?, ?)";

            PreparedStatement stm = conn.prepareStatement(user, PreparedStatement.RETURN_GENERATED_KEYS);
            stm.setString(1, username);
            stm.setString(2, email);
            stm.setString(3, password);
            stm.setInt(4, 103);
            stm.executeUpdate();

            ResultSet rs = stm.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
                System.out.println("User ID: " + userId);
            }

            stm = conn.prepareStatement(info);
            stm.setInt(1, userId);
            stm.setString(2, fullName);
            stm.setDate(3, dob);
            stm.executeUpdate();

            stm = conn.prepareStatement(session);
            stm.setInt(1, userId);
            stm.setTimestamp(2, sessionTime);
            stm.setString(3, sessionDevice);
            stm.executeUpdate();

            conn.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBConnect.closeConnection(conn);
        return false;
    }

    public static User getUser(String username, String password) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "SELECT * FROM User WHERE user_username = ? AND user_password = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, username);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                int user_id = rs.getInt("user_id");
                String user_username = rs.getString("user_username");
                String user_email = rs.getString("user_email");
                String user_password = rs.getString("user_password");
                int user_role = rs.getInt("user_role");
                conn.close();

                User user = new User(user_id, user_username, user_email, user_password, user_role);
                System.out.println(user.toString());

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBConnect.closeConnection(conn);
        return null;
    }

    public static boolean checkExist(String email) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "SELECT * FROM User WHERE user_email = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, email);
            ResultSet rs = stm.executeQuery();
            
            if (rs.next()) {
                conn.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBConnect.closeConnection(conn);
        return false;
    }
}
