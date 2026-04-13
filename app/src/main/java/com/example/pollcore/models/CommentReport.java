package com.example.pollcore.models;

import java.sql.Timestamp;

public class CommentReport {

    private int idReport;
    private int idComment;
    private int idUser;
    private String reason;
    private String details;
    private boolean isResolved;
    private Timestamp createdAt;

    public CommentReport() {}

    public CommentReport(int idReport, int idComment, int idUser,
                         String reason, String details, boolean isResolved, Timestamp createdAt) {
        this.idReport = idReport;
        this.idComment = idComment;
        this.idUser = idUser;
        this.reason = reason;
        this.details = details;
        this.isResolved = isResolved;
        this.createdAt = createdAt;
    }

    public int getIdReport() {
        return idReport;
    }

    public void setIdReport(int idReport) {
        this.idReport = idReport;
    }

    public int getIdComment() {
        return idComment;
    }

    public void setIdComment(int idComment) {
        this.idComment = idComment;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}