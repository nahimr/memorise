package com.dut2.memorise.authentication.utils;

import com.google.firebase.database.Exclude;

public final class User {
    private String uid;
    private String email;
    private String username;
    private String password;
    private double score;

    public User(String email, String username, String password) {
        if(username.isEmpty()) throw new IllegalArgumentException(
                "Username length has to be greater than 0");
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(){}

    @Exclude
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    @Exclude
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}