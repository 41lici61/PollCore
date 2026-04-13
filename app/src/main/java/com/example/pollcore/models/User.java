package com.example.pollcore.models;

import java.sql.Timestamp;

public class User {

    private int idUser;
    private String username;
    private String email;
    private String passwordHash;
    private boolean isPrivate;
    private Integer[] answeredPolls;
    private Timestamp createdAt;

    public User() {}

    public User(int idUser, String username, String email, String passwordHash,
                boolean isPrivate, Integer[] answeredPolls, Timestamp createdAt) {
        this.idUser = idUser;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isPrivate = isPrivate;
        this.answeredPolls = answeredPolls;
        this.createdAt = createdAt;
    }

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // GETTERS & SETTERS
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean aPrivate) { isPrivate = aPrivate; }

    public Integer[] getAnsweredPolls() { return answeredPolls; }
    public void setAnsweredPolls(Integer[] answeredPolls) { this.answeredPolls = answeredPolls; }

    public Timestamp getCreatedAt() { return createdAt; }
}