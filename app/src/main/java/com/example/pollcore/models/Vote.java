package com.example.pollcore.models;

import java.sql.Timestamp;

public class Vote {

    private int idVote;
    private int idUser;
    private int idPoll;
    private int selectedOption;
    private Timestamp votedAt;

    public Vote() {}

    public Vote(int idVote, int idUser, int idPoll, int selectedOption, Timestamp votedAt) {
        this.idVote = idVote;
        this.idUser = idUser;
        this.idPoll = idPoll;
        this.selectedOption = selectedOption;
        this.votedAt = votedAt;
    }

    // GETTERS
    public int getIdVote() { return idVote; }
    public int getIdUser() { return idUser; }
    public int getIdPoll() { return idPoll; }
    public int getSelectedOption() { return selectedOption; }

    public void setIdVote(int idVote) {
        this.idVote = idVote;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setIdPoll(int idPoll) {
        this.idPoll = idPoll;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public Timestamp getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(Timestamp votedAt) {
        this.votedAt = votedAt;
    }
}