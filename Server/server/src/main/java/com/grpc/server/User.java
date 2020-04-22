package com.grpc.server;

import javax.xml.stream.Location;
import java.util.ArrayList;

public class User {
    private String password;
    private String status;
    private String constraints;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }
}
