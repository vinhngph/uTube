package com.utube.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.utube.dtos.UserDTO;
import com.utube.utils.DBConnect;

public class AccountDAO {
    public static boolean registerUser(String email, String username, String password, String fullName, Date dob) {
        Connection conn = DBConnect.getConnection();

        try {
            String user = "INSERT INTO User (user_username, user_email, user_password, user_role) VALUES (?, ?, ?, ?)";
            String info = "INSERT INTO UserInformation (user_id, user_fullname, user_dob) VALUES (?, ?, ?)";

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
            }

            stm = conn.prepareStatement(info);
            stm.setInt(1, userId);
            stm.setString(2, fullName);
            stm.setDate(3, dob);
            stm.executeUpdate();

            DBConnect.closeConnection(conn);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBConnect.closeConnection(conn);
        return false;
    }

    public static UserDTO getUser(String username, String password) {
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

                DBConnect.closeConnection(conn);

                UserDTO user = new UserDTO(user_id, user_username, user_email, user_password, user_role);

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBConnect.closeConnection(conn);
        return null;
    }
}
