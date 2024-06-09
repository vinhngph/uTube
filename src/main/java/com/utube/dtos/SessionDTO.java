package com.utube.dtos;

public class SessionDTO {
    private int sessionId;
    private int sessionUser;
    private String sessionTime;
    private String sessionDevice;

    public SessionDTO(int sessionId, int sessionUser, String sessionTime, String sessionDevice) {
        this.sessionId = sessionId;
        this.sessionUser = sessionUser;
        this.sessionTime = sessionTime;
        this.sessionDevice = sessionDevice;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(int sessionUser) {
        this.sessionUser = sessionUser;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }

    public String getSessionDevice() {
        return sessionDevice;
    }

    public void setSessionDevice(String sessionDevice) {
        this.sessionDevice = sessionDevice;
    }

}
