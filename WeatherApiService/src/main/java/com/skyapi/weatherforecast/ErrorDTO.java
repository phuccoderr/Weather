package com.skyapi.weatherforecast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorDTO {
    private Date timeStamp;
    private int status;
    private String path;
    private List<String> error = new ArrayList<>();

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public void addError(String message) {
        this.error.add(message);
    }
}
