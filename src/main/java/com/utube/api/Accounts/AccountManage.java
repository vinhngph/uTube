package com.utube.api.Accounts;

import java.io.IOException;

import com.utube.daos.AccountDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/api/accounts" })
@MultipartConfig()
public class AccountManage extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int current_user_id = Integer.parseInt(request.getParameter("current_user_id"));
        int current_role = AccountDAO.getRole(current_user_id);

        int modify_user_id = Integer.parseInt(request.getParameter("modify_user_id"));
        int odd_role = AccountDAO.getRole(modify_user_id);
        int modify_role = Integer.parseInt(request.getParameter("modify_role"));

        switch (current_role) {
            case 1:
                updateRole(modify_user_id, modify_role, response);
                break;
            case 2:
                if (odd_role != 1 && modify_role != 1) {
                    updateRole(modify_user_id, modify_role, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case -1:
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                break;
        }
    }

    private void updateRole(int user_id, int new_role, HttpServletResponse response) {
        if (AccountDAO.updateRole(user_id, new_role)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int current_user_id = Integer.parseInt(request.getParameter("current_user_id"));
        int current_role = AccountDAO.getRole(current_user_id);
        int delete_user_id = Integer.parseInt(request.getParameter("delete_user_id"));

        switch (current_role) {
            case 1:
                deleteUser(delete_user_id, response);
                break;
            case 2:
                int delete_user_role = AccountDAO.getRole(delete_user_id);
                if (delete_user_role == 3) {
                    deleteUser(delete_user_id, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case -1:
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                break;
        }
    }

    private void deleteUser(int user_id, HttpServletResponse response) {
        if (AccountDAO.deleteUser(user_id)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
    }
}
