package com.utube.dtos;

import java.sql.Date;

public class UserInformationDTO {
    private int user_id;
    private String user_fullname;
    private Date user_dob;

    public UserInformationDTO(int user_id, String user_fullname, Date user_dob) {
        this.user_id = user_id;
        this.user_fullname = user_fullname;
        this.user_dob = user_dob;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }

    public Date getUser_dob() {
        return user_dob;
    }

    public void setUser_dob(Date user_dob) {
        this.user_dob = user_dob;
    }
}
