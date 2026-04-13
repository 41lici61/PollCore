package com.example.pollcore.models;

import java.sql.Timestamp;

public class Poll {

    private int idPoll;
    private int idUser;
    private String title;
    private String description;
    private String question;

    private String option1;
    private String option2;
    private String option3;
    private String option4;

    private int countOption1;
    private int countOption2;
    private int countOption3;
    private int countOption4;
    private int totalVotes;

    private boolean isAnonymous;
    private Timestamp createdAt;

    public Poll() {}

    public Poll(int idPoll, int idUser, String title, String description, String question,
                String option1, String option2, String option3, String option4,
                int countOption1, int countOption2, int countOption3, int countOption4,
                int totalVotes, boolean isAnonymous, Timestamp createdAt) {

        this.idPoll = idPoll;
        this.idUser = idUser;
        this.title = title;
        this.description = description;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.countOption1 = countOption1;
        this.countOption2 = countOption2;
        this.countOption3 = countOption3;
        this.countOption4 = countOption4;
        this.totalVotes = totalVotes;
        this.isAnonymous = isAnonymous;
        this.createdAt = createdAt;
    }

    // GETTERS & SETTERS
    public int getIdPoll() { return idPoll; }
    public int getIdUser() { return idUser; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getQuestion() { return question; }

    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getOption4() { return option4; }

    public int getTotalVotes() { return totalVotes; }
    public boolean isAnonymous() { return isAnonymous; }

    public void setIdPoll(int idPoll) {
        this.idPoll = idPoll;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public int getCountOption1() {
        return countOption1;
    }

    public void setCountOption1(int countOption1) {
        this.countOption1 = countOption1;
    }

    public int getCountOption2() {
        return countOption2;
    }

    public void setCountOption2(int countOption2) {
        this.countOption2 = countOption2;
    }

    public int getCountOption3() {
        return countOption3;
    }

    public void setCountOption3(int countOption3) {
        this.countOption3 = countOption3;
    }

    public int getCountOption4() {
        return countOption4;
    }

    public void setCountOption4(int countOption4) {
        this.countOption4 = countOption4;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}