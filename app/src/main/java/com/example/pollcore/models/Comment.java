package com.example.pollcore.models;

import java.sql.Timestamp;

public class Comment {
    private int idComment;
    private int idPoll;
    private int idUser;
    private String content;
    private Integer replyTo;
    private Integer[] repliesIds;
    private Timestamp createdAt;
    private String username;

    public Comment() {}

    public Comment(int idComment, int idPoll, int idUser, String content,
                   Integer replyTo, Integer[] repliesIds, Timestamp createdAt) {
        this.idComment = idComment;
        this.idPoll = idPoll;
        this.idUser = idUser;
        this.content = content;
        this.replyTo = replyTo;
        this.repliesIds = repliesIds;
        this.createdAt = createdAt;
    }

    public int getIdComment() { return idComment; }
    public void setIdComment(int idComment) { this.idComment = idComment; }

    public int getIdPoll() { return idPoll; }
    public void setIdPoll(int idPoll) { this.idPoll = idPoll; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getReplyTo() { return replyTo; }
    public void setReplyTo(Integer replyTo) { this.replyTo = replyTo; }

    public Integer[] getRepliesIds() { return repliesIds; }
    public void setRepliesIds(Integer[] repliesIds) { this.repliesIds = repliesIds; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}