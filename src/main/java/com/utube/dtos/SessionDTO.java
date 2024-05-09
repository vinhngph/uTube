package com.utube.dtos;

import java.sql.Timestamp;

public class SessionDTO {
    private int sessionId;
    private int sessionUser;
    private Timestamp sessionTime;
    private String sessionDevice;

    public SessionDTO(int sessionId, int sessionUser, Timestamp sessionTime, String sessionDevice) {
        this.sessionId = sessionId;
        this.sessionUser = sessionUser;
        this.sessionTime = sessionTime;
        this.sessionDevice = sessionDevice;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getSessionUser() {
        return sessionUser;
    }

    public Timestamp getSessionTime() {
        return sessionTime;
    }

    public String getSessionDevice() {
        return sessionDevice;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public void setSessionUser(int sessionUser) {
        this.sessionUser = sessionUser;
    }

    public void setSessionTime(Timestamp sessionTime) {
        this.sessionTime = sessionTime;
    }

    public void setSessionDevice(String sessionDevice) {
        this.sessionDevice = sessionDevice;
    }
}
