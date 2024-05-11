package com.utube.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.gson.Gson;
import com.utube.utils.DBConnect;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/isUser" })
public class IsUser extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } finally {
            reader.close();
        }

        Gson gson = new Gson();
        Connection conn = DBConnect.getConnection();

        try {
            MyJSON data = gson.fromJson(sb.toString(), MyJSON.class);
            String email = data.getEmail();
            String username = data.getUsername();

            String query = "SELECT user_id FROM User WHERE user_email = ? OR user_username = ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, email);
            stm.setString(2, username);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                conn.close();
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            } else {
                conn.close();
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            }
        } catch (Exception e) {
            DBConnect.closeConnection(conn);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private class MyJSON {
        private String email;
        private String username;

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }
    }
}