package com.grpc.server.model;

import java.util.List;

public class User {
    private String username;
    private String password;
    private String status;
    private List<String> constraints;


    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean passwordMatch(String password) {
        return this.password.equals(password);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<String> constraints) {
        this.constraints = constraints;
    }
}
