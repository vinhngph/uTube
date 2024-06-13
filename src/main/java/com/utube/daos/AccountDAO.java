package com.utube.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.Gson;
import com.utube.dtos.SessionDTO;
import com.utube.dtos.UserDTO;
import com.utube.dtos.UserInformationDTO;
import com.utube.utils.DBConnect;
import com.utube.utils.EmailService;

public class AccountDAO {
    public static boolean registerUser(String email, String username, String password, String fullName, Date dob) {
        Connection conn = DBConnect.getConnection();

        try {
            String user = "INSERT INTO User (user_username, user_email, user_password, user_role) VALUES (?, ?, ?, ?)";
            String info = "INSERT INTO User_Information (user_id, user_fullname, user_dob) VALUES (?, ?, ?)";

            PreparedStatement stm = conn.prepareStatement(user, PreparedStatement.RETURN_GENERATED_KEYS);
            stm.setString(1, username);
            stm.setString(2, email);
            stm.setString(3, password);
            stm.setInt(4, 3);
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
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBConnect.closeConnection(conn);
        return null;
    }

    public static ArrayList<UserDTO> getAllUsers() {
        Connection conn = DBConnect.getConnection();
        ArrayList<UserDTO> users = new ArrayList<UserDTO>();

        try {
            String query = "SELECT * FROM User WHERE user_role = 3";
            PreparedStatement stm = conn.prepareStatement(query);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                int user_id = rs.getInt("user_id");
                String user_username = rs.getString("user_username");
                String user_email = rs.getString("user_email");
                String user_password = rs.getString("user_password");
                int user_role = rs.getInt("user_role");

                UserDTO user = new UserDTO(user_id, user_username, user_email, user_password, user_role);
                users.add(user);
            }

            return users;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static ArrayList<UserDTO> getAllStaffs() {
        Connection conn = DBConnect.getConnection();
        ArrayList<UserDTO> users = new ArrayList<UserDTO>();

        try {
            String query = "SELECT * FROM User WHERE user_role = 2";
            PreparedStatement stm = conn.prepareStatement(query);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                int user_id = rs.getInt("user_id");
                String user_username = rs.getString("user_username");
                String user_email = rs.getString("user_email");
                String user_password = rs.getString("user_password");
                int user_role = rs.getInt("user_role");

                UserDTO user = new UserDTO(user_id, user_username, user_email, user_password, user_role);
                users.add(user);
            }

            return users;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static UserInformationDTO getDetails(int user_id) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "SELECT * FROM User_Information WHERE user_id = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setInt(1, user_id);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                String user_fullname = rs.getString("user_fullname");
                Date user_dob = rs.getDate("user_dob");

                UserInformationDTO user = new UserInformationDTO(user_id, user_fullname, user_dob);

                return user;
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

    public static boolean updateRole(int user_id, int new_role) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "UPDATE User SET user_role = ? WHERE user_id = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setInt(1, new_role);
            stm.setInt(2, user_id);
            stm.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static int getRole(int user_id) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "SELECT user_role FROM User WHERE user_id = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setInt(1, user_id);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_role");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static boolean deleteUser(int user_id) {
        Connection conn = DBConnect.getConnection();

        try {
            String query_1 = "DELETE FROM User_Information WHERE user_id = ?";
            String query_2 = "DELETE FROM Session WHERE session_user = ?";
            String query_3 = "DELETE FROM Upload WHERE user_id = ?";
            String query_4 = "DELETE FROM User WHERE user_id = ?";

            PreparedStatement stm = conn.prepareStatement(query_1);
            stm.setInt(1, user_id);
            stm.executeUpdate();

            stm = conn.prepareStatement(query_2);
            stm.setInt(1, user_id);
            stm.executeUpdate();

            stm = conn.prepareStatement(query_3);
            stm.setInt(1, user_id);
            stm.executeUpdate();

            stm = conn.prepareStatement(query_4);
            stm.setInt(1, user_id);
            stm.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static boolean isUser(int userId) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "SELECT * FROM User WHERE user_id = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setInt(1, userId);
            ResultSet rs = stm.executeQuery();

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

    public static String getSessions(int userId) {
        Connection conn = DBConnect.getConnection();
        Gson gson = new Gson();

        try {
            String query = "SELECT * FROM Session WHERE session_user = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setInt(1, userId);

            ResultSet rs = stm.executeQuery();
            ArrayList<SessionDTO> sessions = new ArrayList<SessionDTO>();

            while (rs.next()) {
                int sessionId = rs.getInt("session_id");
                int sessionUser = rs.getInt("session_user");
                String sessionTime = rs.getTimestamp("session_time").toInstant().toString();
                String sessionDevice = rs.getString("session_device");

                SessionDTO session = new SessionDTO(sessionId, sessionUser, sessionTime, sessionDevice);
                sessions.add(session);
            }

            return gson.toJson(sessions);
        } catch (SQLException e) {
            return null;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    private static int isEmailExist(String email) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "SELECT user_id FROM User WHERE user_email = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, email);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            return -2;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    private static boolean isOTPExist(int userId) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "SELECT otp FROM Password_OTP WHERE user_id = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setInt(1, userId);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    private static int generateOTP(int userId) {
        Connection conn = DBConnect.getConnection();

        try {
            boolean isExist = isOTPExist(userId);

            Random random = new Random();
            int otp = random.nextInt(900000) + 100000;

            if (isExist) {
                String query = "UPDATE Password_OTP SET otp = ? WHERE user_id = ?";
                PreparedStatement stm = conn.prepareStatement(query);
                stm.setInt(1, otp);
                stm.setInt(2, userId);

                stm.executeUpdate();

                return otp;
            } else {
                String query = "INSERT INTO Password_OTP (user_id, otp) VALUES (?, ?)";
                PreparedStatement stm = conn.prepareStatement(query);

                stm.setInt(1, userId);
                stm.setInt(2, otp);

                stm.executeUpdate();

                return otp;
            }
        } catch (SQLException e) {
            return -2;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static int sendOTPMail(String email) {
        int userId = isEmailExist(email);

        // -1: Email not exist
        // -2: SQL Error

        if (userId == -1) {
            return -1;
        } else if (userId == -2) {
            return -2;
        }

        int otp = generateOTP(userId);
        if (otp == -2) {
            return -2;
        }

        String subject = "uTube - Reset Password OTP";
        String body = "<p>Your OTP is: " + "<strong>" + otp + "</strong>" + "</p>" + "<br>"
                + "<p>Do not share this OTP with anyone.</p>";
        EmailService.sendEmail(email, subject, body);

        return 0;
    }

    public static int verifyOTP(String email, int otp) {
        Connection conn = DBConnect.getConnection();

        try {
            int userId = isEmailExist(email);

            // -1: Email not exist
            // -2: SQL Error

            if (userId == -1) {
                return -1;
            } else if (userId == -2) {
                return -2;
            }

            String query = "SELECT otp FROM Password_OTP WHERE user_id = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setInt(1, userId);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                int correctOTP = rs.getInt("otp");

                if (correctOTP == otp) {
                    String deleteQuery = "DELETE FROM Password_OTP WHERE user_id = ?";
                    stm = conn.prepareStatement(deleteQuery);
                    stm.setInt(1, userId);
                    stm.executeUpdate();

                    return 0;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } catch (SQLException e) {
            return -2;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public static boolean updatePassword(String email, String newPassword) {
        Connection conn = DBConnect.getConnection();

        try {
            String query = "UPDATE User SET user_password = ? WHERE user_email = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, newPassword);
            stm.setString(2, email);

            stm.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            DBConnect.closeConnection(conn);
        }
    }
}
